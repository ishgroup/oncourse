package ish.oncourse.willow.billing.service.impl

import com.amazonaws.services.identitymanagement.model.AccessKey
import com.amazonaws.services.s3.model.Region
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.api.request.RequestService
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.*
import ish.oncourse.services.mail.EmailBuilder
import ish.oncourse.services.mail.SendEmail
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.util.PreferenceUtil
import ish.oncourse.willow.billing.v1.model.CollegeDTO
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.service.BillingApi
import ish.oncourse.willow.billing.website.CreateNewWebSite
import ish.oncourse.willow.billing.website.WebSiteService
import ish.persistence.Preferences
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.tmatesoft.svn.core.SVNDirEntry
import org.tmatesoft.svn.core.SVNProperties
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager
import org.tmatesoft.svn.core.io.ISVNEditor
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator
import org.tmatesoft.svn.core.wc.SVNWCUtil

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
    
    private static final String BUCKET_NAME_FORMAT = "ish-oncourse-%s"
    private static final String AWS_USER_NAME_FORMAT = "college.%s"
    private static final String svnRepo =  Configuration.getValue(SVN_URL)
    private static final String svnUser =  Configuration.getValue(SVN_USER)
    private static final String svnPass =  Configuration.getValue(SVN_PASS)

    private static final Logger logger = LogManager.logger
    
    @Override
    void createCollege(CollegeDTO collegeDTO) {

        if (!verifyCollegeName(collegeDTO.collegeKey)) {
            throw new BadRequestException("College $collegeDTO.collegeKey already exists")
        }
        
        Boolean s3Done = false
        Boolean svnDone = false
        Boolean dbDone = false
        
        ObjectContext context = cayenneService.newContext()

        try {

            //1.Create s3 bucket
            String bucketName =  String.format(BUCKET_NAME_FORMAT, collegeDTO.collegeKey)
            s3Service.createBucket(bucketName)
            AccessKey key = s3Service.createS3User(String.format(AWS_USER_NAME_FORMAT, collegeDTO.collegeKey), bucketName)

            s3Done = true
            
            //2.Commit svn config
            AngelConfig angelConfig = new AngelConfig()
            
            angelConfig.securityCode = SecurityUtil.generateRandomPassword(16)
            angelConfig.collegeKey = collegeDTO.collegeKey
            angelConfig.s3bucketName = bucketName
            angelConfig.s3accessId = key.getAccessKeyId()
            angelConfig.s3accessKey = key.getSecretAccessKey()
            angelConfig.userFirstName = collegeDTO.userFirstName
            angelConfig.userLastName = collegeDTO.userLastName
            angelConfig.userEmail = collegeDTO.userEmail
            angelConfig.userPhone = collegeDTO.userPhone
            angelConfig.commit()
            
            svnDone = true
            
            //3.Create db records in on transaction
            cayenneService.performTransaction {

                College college = recordNewCollege(angelConfig.securityCode, angelConfig.collegeKey, collegeDTO.organisationName, collegeDTO.timeZone, context)
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

                PreferenceUtil.createPreference(context, college, Preference.STORAGE_BUCKET_NAME, angelConfig.s3bucketName)
                PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_ID, angelConfig.s3accessId)
                PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_KEY, angelConfig.s3accessKey)

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

                PreferenceUtil.createPreference(context, college, ish.oncourse.services.preference.Preferences.ENROLMENT_CORPORATEPASS_PAYMENT_ENABLED,String.valueOf(false))
                PreferenceUtil.createPreference(context, college, ish.oncourse.services.preference.Preferences.ENROLMENT_CREDITCARD_PAYMENT_ENABLED, String.valueOf(false))
                PreferenceUtil.createPreference(context, college, ish.oncourse.services.preference.Preferences.PAYMENT_GATEWAY_TYPE, PaymentGatewayType.DISABLED.toString())
                PreferenceUtil.createPreference(context, college, Preferences.SERVICES_CC_AMEX_ENABLED,  String.valueOf(false))

                context.commitChanges()

                if (collegeDTO.webSiteTemplate) {
                    webSiteService.createWebSite(college, collegeDTO.webSiteTemplate, collegeDTO.organisationName, collegeDTO.collegeKey)
                }
            }
            
            dbDone = true
            
            logger.warn("College was created:$collegeDTO.collegeKey")
            sendEmail('College was created', "college info: $collegeDTO")

        } catch (Exception e) {
            context.rollbackChanges()
            logger.catching(e)
            logger.error("Error appears while creating new college: $collegeDTO".toString())
            
            sendEmail('College was not created',
                            "Exception: ${e.toString()} \n"+ 
                            "s3 done: ${s3Done} \n"+ 
                            "svn done: ${svnDone} \n"+
                            "db done: ${dbDone} \n"+ 
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

    private College recordNewCollege(String securityCode, String collegeKey, String name, String timeZone, ObjectContext objectContext ) {
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
        
        college.setIsTestingWebServicePayments(false)
        college.setIsTestingWebSitePayments(false)
        college.setIsWebServicePaymentsEnabled(false)
        college.setIsWebSitePaymentsEnabled(false)
        college.setRequiresAvetmiss(true)
        college.setTimeZone(timeZone)
        college.lastRemoteAuthentication = new Date(0)
        college.firstRemoteAuthentication = new Date()

        return college
    }
    
    
    static class AngelConfig {
        
        String securityCode
        String collegeKey
        
        String s3bucketName
        String s3accessId
        String s3accessKey
        String s3Region = Region.AP_Sydney.toString()
        
        String userFirstName
        String userLastName
        String userEmail
        String userPhone

        String paidUntil
        String port
        
        AngelConfig() {
            LocalDate untilDate = LocalDate.now()
            if (untilDate.getDayOfMonth() < 15) {
                untilDate = untilDate.plusMonths(1)
            } else {
                untilDate = untilDate.plusMonths(2)
            }
            paidUntil = untilDate.format("yyyy-MM-01")
        }
        
        Map<String, Object> toMap() {
           return [
                   (collegeKey): [
                           security_key: securityCode, 
                           version: '"{{ small }}"',
                           server: [
                                   max_users: 1,
                                   port: port,
                                   minion: 'colo.splash'
                           ],
                           db: [
                                   pass: SecurityUtil.generateRandomPassword(12)
                           ],
                           document: [
                                   bucket: s3bucketName,
                                   accessKeyId: s3accessId,
                                   accessSecretKey: s3accessKey,
                                   region: s3Region,
                                   limit: '1G'
                           ],
                           user: [
                                   firstName: userFirstName,
                                   lastName: userLastName,
                                   email: userEmail,
                           ],
                           billing: [
                                   code: collegeKey,
                                   plan: 'basic', 
                                   paid_until: "\"$paidUntil\"", 
                                   web: [
                                           plan: "WEB-6"
                                   ]
                           ]
                   ]
           ] as Map<String, Object>
        }
        
        String toString() {
            return  "$collegeKey:\n"+
                    "  security_key: $securityCode\n" +
                    "  version: \"{{ small }}\"\n" +
                    "  server:\n" +
                    "    max_users: 1\n" +
                    "    port: $port\n" +
                    "    minion: colo.splash\n" +
                    "  db:\n" +
                    "    pass: ${SecurityUtil.generateRandomPassword(12)}\n" +
                    "  document:\n" +
                    "    bucket: $s3bucketName\n" +
                    "    accessKeyId: $s3accessId\n" +
                    "    accessSecretKey: $s3accessKey\n" +
                    "    region: $s3Region\n" +
                    "    limit: 1G\n" +
                    "  user:\n"+
                    "    firstName: $userFirstName\n" +
                    "    lastName: $userLastName\n" +
                    "    email: $userEmail\n" +
                    "  billing:\n"+
                    "    code: $collegeKey\n"+
                    "    plan: basic\n"+
                    "    paid_until: \"$paidUntil\"\n" +
                    "    web:\n" +
                    "      plan: WEB-6\n"
        }

        void commit() {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory())

            String fileName = "${collegeKey}.sls"
            SVNRepository repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded(svnRepo))
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnUser, svnPass.toCharArray())
            repository.setAuthenticationManager(authManager)

            int maxPort = 0
            try {
                (repository.getDir('.', -1, null, (Collection<SVNDirEntry>) null) as Collection<SVNDirEntry>)
                        .each { SVNDirEntry entry -> 
                            if (entry.name.endsWith('.sls')) {
                                SVNProperties fileProperties = new SVNProperties()
                                ByteArrayOutputStream baos = new ByteArrayOutputStream()
                                repository.getFile(entry.relativePath, -1, fileProperties, baos)
                                String collegeKey = entry.name.replace('.sls', '')
                                Map<String, Object> yaml = mapper.readValue(baos.toString(), new TypeReference<Map<String, Object>>() {})
                                int collegePort = yaml[(collegeKey)]['server']['port'] as int
                                if (collegePort > maxPort) {
                                    maxPort = collegePort
                                }
                    }
                }
            } catch (Exception e) {
                logger.catching(e)
                port='10000'
            }
            
            port = ++ maxPort
            
            ISVNEditor editor = repository.getCommitEditor( "Create $collegeKey angel instance", null)
            
            editor.openRoot(-1)
            
            editor.addFile(fileName, null, -1)
            editor.applyTextDelta(fileName, null)
            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator()
            editor.applyTextDelta(svnRepo, null)

            String yaml = toString()
            InputStream is = IOUtils.toInputStream(yaml, "UTF-8")
            
            String chksm = deltaGenerator.sendDelta(svnRepo, is, editor, true)

            is.close()
            editor.textDeltaEnd(fileName)
            editor.closeFile(fileName, chksm)
            editor.closeEdit()

        }
        
    }
    
}
