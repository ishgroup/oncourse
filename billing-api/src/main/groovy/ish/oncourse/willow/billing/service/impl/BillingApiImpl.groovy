package ish.oncourse.willow.billing.service.impl

import com.amazonaws.services.identitymanagement.model.AccessKey
import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.College
import ish.oncourse.model.KeyStatus
import ish.oncourse.model.Preference
import ish.oncourse.model.WebSite
import ish.oncourse.services.mail.EmailBuilder
import ish.oncourse.services.mail.SendEmail
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.util.PreferenceUtil
import ish.oncourse.willow.billing.v1.model.CollegeDTO
import ish.oncourse.willow.billing.v1.service.BillingApi
import ish.oncourse.willow.billing.website.CreateNewWebSite
import ish.persistence.Preferences
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.InternalServerErrorException

import static ish.oncourse.configuration.Configuration.AdminProperty.S_ROOT

class BillingApiImpl implements BillingApi {
    
    @Inject
    private ICayenneService cayenneService
    
    @Inject
    private IS3Service s3Service

    @Inject
    private RequestService requestService
    
    private static final String BUCKET_NAME_FORMAT = "ish-oncourse-%s"
    private static final String AWS_USER_NAME_FORMAT = "college.%s"

    private static final Logger logger = LogManager.logger
    
    @Override
    void createCollege(CollegeDTO collegeDTO) {

        try {

            AngelConfig angelConfig = new AngelConfig()

            angelConfig.securityCode = SecurityUtil.generateRandomPassword(16)
            angelConfig.collegeKey = collegeDTO.collegeKey

            College college = recordNewCollege(angelConfig.securityCode, angelConfig.collegeKey, collegeDTO.organisationName, collegeDTO.timeZone)
            angelConfig.s3bucketName = String.format(BUCKET_NAME_FORMAT, angelConfig.collegeKey)

            s3Service.createBucket(angelConfig.s3bucketName)
            AccessKey key = s3Service.createS3User(String.format(AWS_USER_NAME_FORMAT, angelConfig.collegeKey), angelConfig.s3bucketName)

            angelConfig.s3accessId = key.getAccessKeyId()
            angelConfig.s3accessKey = key.getSecretAccessKey()

            angelConfig.userFirstName = collegeDTO.userFirstName
            angelConfig.userLastName = collegeDTO.userLastName
            angelConfig.useEmail = collegeDTO.userEmail


            ObjectContext context = cayenneService.newContext()

            PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_NAME, collegeDTO.organisationName)
            PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_ABN, collegeDTO.abn)
            PreferenceUtil.createPreference(context, college, Preferences.ONCOURSE_SERVER_DEFAULT_TZ, collegeDTO.timeZone)
            if (collegeDTO.webSiteTemplate) {
                PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_URL, "https://${collegeDTO.collegeKey}.oncourse.cc")
            }

            PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_COLLEGENAME, collegeDTO.traidingName)
            PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_ADDRESS1, collegeDTO.address)

            PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_SUBURB, collegeDTO.suburb)
            PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_STATE, collegeDTO.state)
            PreferenceUtil.createPreference(context, college, Preferences.AVETMISS_POSTCODE, collegeDTO.postcode)

            PreferenceUtil.createPreference(context, college, Preference.STORAGE_BUCKET_NAME, angelConfig.s3bucketName)
            PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_ID, angelConfig.s3accessId)
            PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_KEY, angelConfig.s3accessKey)
            PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_NAME, collegeDTO.organisationName)

            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_ACCESS_CONTROL, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_LDAP, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_BUDGET, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_EXTENRNAL_DB, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_SSL, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_SMS, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_CC_PROCESSING, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_PAYROLL, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_VOUCHER, String.valueOf(false))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_MEMBERSHIP, String.valueOf(true))
            PreferenceUtil.createPreference(context, college, Preferences.LICENSE_ATTENDANCE, String.valueOf(true))

            context.commitChanges()

            Map<String, String>  errors = [:]
            if (collegeDTO.webSiteTemplate) {
                context = cayenneService.newNonReplicatingContext()

                WebSite template = ObjectSelect.query(WebSite)
                        .where(WebSite.SITE_KEY.eq("template-$collegeDTO.webSiteTemplate".toString()))
                        .selectOne(context)

                CreateNewWebSite createNewWebSite = CreateNewWebSite.valueOf(collegeDTO.organisationName,
                        collegeDTO.collegeKey,
                        template,
                        Configuration.getValue(S_ROOT),
                        context.localObject(college), context)
                createNewWebSite.create()
                errors = createNewWebSite.errors
            }           
            if (errors) {
                errors.each { k, v ->
                    logger.error("$k: $v")
                }
            }
            
            logger.warn("College was created:$collegeDTO.collegeKey")

            sendEmail('College was created', "college info: $collegeDTO \n errors: $errors")

        } catch (Exception e) {
            logger.catching(e)
            logger.error("Error appears while creating new college: $collegeDTO".toString())
            sendEmail('College was not created', "errors: ${e.toString()} \n college info: $collegeDTO".toString())
            throw new InternalServerErrorException("Something unexpected has happened. Please contact ish support or try again")
        } finally {
            //destroy session after process finished
            requestService.setSessionToken(null, 0)
        }
    }

    private static void sendEmail(String subject, String message) {

        EmailBuilder email = new EmailBuilder()
        email.setFromEmail('no-reply@provisioning.ish.com.au')
        email.setSubject(subject)
        email.setBody(message)
        email.setToEmails('support@ish.com.ua')
        SendEmail.valueOf(email, true)
    }
    
    @Override
    Boolean verifyCollegeName(String name, String xGRecaptcha) {
        return ObjectSelect.query(College)
                .where(College.COLLEGE_KEY.eq(name))
                .or(College.WEB_SITES.dot(WebSite.NAME).eq(name))
                .select(cayenneService.newContext()).empty
    }

    private College recordNewCollege(String securityCode, String collegeKey, String name, String timeZone) {
        Date createdOn = new Date()
        ObjectContext objectContext = cayenneService.newNonReplicatingContext()

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
        
        college.setIsTestingWebServicePayments(false)
        college.setIsTestingWebSitePayments(false)
        college.setIsWebServicePaymentsEnabled(false)
        college.setIsWebSitePaymentsEnabled(false)
        college.setRequiresAvetmiss(true)
        college.setTimeZone(timeZone)

        objectContext.commitChanges()

        return college
    }
    
    
    static class AngelConfig {
        String securityCode
        String collegeKey
        String s3bucketName
        String s3accessId
        String s3accessKey
        
        String userFirstName
        String userLastName
        String useEmail

    }
    
}
