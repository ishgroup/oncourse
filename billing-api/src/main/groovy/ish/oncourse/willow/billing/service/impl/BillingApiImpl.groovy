package ish.oncourse.willow.billing.service.impl

import com.amazonaws.services.identitymanagement.model.AccessKey
import com.amazonaws.services.s3.model.Region
import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.*
import ish.oncourse.services.mail.EmailBuilder
import ish.oncourse.services.mail.SendEmail
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.util.PreferenceUtil
import ish.oncourse.willow.billing.env.EnvironmentService
import ish.oncourse.willow.billing.v1.model.BillingPlan
import ish.oncourse.willow.billing.v1.model.CollegeDTO
import ish.oncourse.willow.billing.v1.model.Currency
import ish.oncourse.willow.billing.v1.service.BillingApi
import ish.oncourse.willow.billing.website.WebSiteService
import ish.persistence.Preferences
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.BadRequestException
import javax.ws.rs.InternalServerErrorException
import java.time.LocalDate

import static ish.oncourse.configuration.Configuration.AdminProperty.*

@CompileStatic
class BillingApiImpl implements BillingApi {
    
    @Inject
    private ICayenneService cayenneService
    
    @Inject
    private IS3Service s3Service

    @Inject
    private RequestService requestService
    
    @Inject
    private WebSiteService webSiteService

    @Inject
    private EnvironmentService environmentService

    private static final String BUCKET_NAME_FORMAT = "ish-oncourse-%s"
    private static final String AWS_USER_NAME_FORMAT = "college.%s"


    private static final Logger logger = LogManager.logger
    
    @Override
    void createCollege(CollegeDTO collegeDTO) {

        if (!verifyCollegeName(collegeDTO.collegeKey)) {
            throw new BadRequestException("College $collegeDTO.collegeKey already exists")
        }
        
        Boolean s3Done = false
        Boolean dbDone = false
        Boolean saltDone = false
        
        ObjectContext context = cayenneService.newContext()

        try {

            //1.Create s3 bucket
            String bucketName =  String.format(BUCKET_NAME_FORMAT, collegeDTO.collegeKey)
            s3Service.createBucket(bucketName)
            AccessKey key = s3Service.createS3User(String.format(AWS_USER_NAME_FORMAT, collegeDTO.collegeKey), bucketName)

            s3Done = true
            
            //2.Create db records in on transaction
            cayenneService.performTransaction {

                College college = recordNewCollege(SecurityUtil.generateRandomPassword(16), collegeDTO.collegeKey, collegeDTO.organisationName, context)
                context.commitChanges()

                PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_NAME, collegeDTO.organisationName)
                PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_ABN, collegeDTO.abn)
                PreferenceUtil.createPreference(context, college, Preferences.ONCOURSE_SERVER_DEFAULT_TZ, collegeDTO.timeZone)
                if (collegeDTO.webSiteTemplate) {
                    PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_URL, "https://${collegeDTO.collegeKey}.oncourse.cc")
                }

                PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_COLLEGENAME, collegeDTO.tradingName)
                PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_ADDRESS1, collegeDTO.address)

                PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_SUBURB, collegeDTO.suburb)
                PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_STATE, collegeDTO.state)
                PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_POSTCODE, collegeDTO.postcode)
                
                PreferenceUtil.createSetting(context, college, Settings.STORAGE_BUCKET_NAME, bucketName)
                PreferenceUtil.createSetting(context, college, Settings.STORAGE_ACCESS_ID, key.accessKeyId)
                PreferenceUtil.createSetting(context, college, Settings.STORAGE_ACCESS_KEY, key.secretAccessKey)
                PreferenceUtil.createSetting(context, college, Settings.STORAGE_REGION, Region.AP_Sydney.toString())

                PreferenceUtil.createSetting(context, college, Settings.BILLING_USERS, '1')
                PreferenceUtil.createSetting(context, college, Settings.BILLING_PLAN, BillingPlan.STARTER_21.toString())
                PreferenceUtil.createSetting(context, college, Settings.BILLING_CURRENCY, (collegeDTO.currency ? collegeDTO.currency.toString() : Currency.AU.toString()))
                PreferenceUtil.createSetting(context, college, Settings.BILLING_CONTACT_NAME, "$collegeDTO.userFirstName $collegeDTO.userLastName")
                PreferenceUtil.createSetting(context, college, Settings.BILLING_CONTACT_EMAIL,  collegeDTO.userEmail)
                PreferenceUtil.createSetting(context, college, Settings.BILLING_CONTACT_PHONE,  collegeDTO.userPhone)

                LocalDate untilDate = LocalDate.now()
                if (untilDate.getDayOfMonth() < 15) {
                    untilDate = untilDate.plusMonths(1)
                } else {
                    untilDate = untilDate.plusMonths(2)
                }
                
                PreferenceUtil.createSetting(context, college, Settings.BILLING_PAID_UNTIL,  untilDate.format("yyyy-MM-01"))

                context.commitChanges()

                if (collegeDTO.webSiteTemplate) {
                    webSiteService.createWebSite(college, collegeDTO.webSiteTemplate, collegeDTO.organisationName, collegeDTO.collegeKey)
                }
            }
            
            dbDone = true

            saltDone = environmentService.collegeCreated(collegeDTO.collegeKey)
            

            
            logger.warn("College was created:$collegeDTO.collegeKey")
            sendEmail('College was created', "college info: $collegeDTO")

        } catch (Exception e) {
            context.rollbackChanges()
            logger.catching(e)
            logger.error("Error appears while creating new college: $collegeDTO".toString())
            
            sendEmail('College was not created',
                            "Exception: ${e.toString()} \n"+ 
                            "s3 done: ${s3Done} \n"+ 
                            "db done: ${dbDone} \n"+ 
                            "salt done: ${saltDone} \n"+ 
                            "college info: $collegeDTO".toString()
            )
            throw new InternalServerErrorException("Something unexpected has happened. Please contact ish support or try again")
        } finally {
            //destroy session after process finished
            requestService.setSessionToken(null, 0)
        }
    }

    @Override
    String getCollegeKey() {
        return requestService.college.collegeKey
    }

    private static void sendEmail(String subject, String message) {

        EmailBuilder email = new EmailBuilder()
        email.setFromEmail('support@ish.com.au')
        email.setSubject(subject)
        email.setBody(message)
        email.setToEmails('support@ish.com.au')
        SendEmail.valueOf(email, true).send()
    }
    
    @Override
    Boolean verifyCollegeName(String name, String xGRecaptcha = null) {
        return ObjectSelect.query(College)
                .where(College.COLLEGE_KEY.eq(name))
                .or(College.WEB_SITES.outer().dot(WebSite.NAME).eq(name))
                .select(cayenneService.newContext()).empty
    }

    private College recordNewCollege(String securityCode, String collegeKey, String name, ObjectContext objectContext ) {
        Date createdOn = new Date()

        College college = objectContext.newObject(College)

        // TODO An entity factory would be handy here... perhaps another time
        college.setWebServicesSecurityCode(securityCode)
        college.setName(name)
        college.setCreated(createdOn)
        college.setModified(createdOn)
        college.setCommunicationKey(-1l)
        college.setCommunicationKeyStatus(KeyStatus.VALID)
        college.setBillingCode(collegeKey)
        college.setCollegeKey(collegeKey)
        college.lastRemoteAuthentication = new Date(0)
        return college
    }
}
