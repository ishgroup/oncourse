package ish.oncourse.willow.billing.service.impl

import com.amazonaws.services.identitymanagement.model.AccessKey
import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.KeyStatus
import ish.oncourse.model.Preference
import ish.oncourse.model.WebSite
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.util.PreferenceUtil
import ish.oncourse.willow.billing.v1.model.CollegeDTO
import ish.oncourse.willow.billing.v1.service.BillingApi
import ish.persistence.Preferences
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect 

class BillingApiImpl implements BillingApi {

    @Inject
    private ICayenneService cayenneService
    
    @Inject
    private IS3Service s3Service

    private static final String BUCKET_NAME_FORMAT = "ish-oncourse-%s"
    private static final String AWS_USER_NAME_FORMAT = "college.%s"
    
    
    @Override
    void createCollege(CollegeDTO collegeDTO) {
        AngelConfig angelConfig = new AngelConfig()
        
        angelConfig.securityCode = SecurityUtil.generateRandomPassword(16)
        angelConfig.collegeKey = collegeDTO.collegeKey
        
        College college = recordNewCollege(angelConfig.securityCode, angelConfig.collegeKey, collegeDTO.organisationName)
        angelConfig.s3bucketName = String.format(BUCKET_NAME_FORMAT, angelConfig.collegeKey)

        s3Service.createBucket(angelConfig.s3bucketName)
        AccessKey key = s3Service.createS3User( String.format(AWS_USER_NAME_FORMAT, angelConfig.collegeKey), angelConfig.s3bucketName)

        angelConfig.s3accessId = key.getAccessKeyId()
        angelConfig.s3accessKey = key.getSecretAccessKey()
        
        ObjectContext context = cayenneService.newContext()
        
        PreferenceUtil.createPreference(context, college, Preference.STORAGE_BUCKET_NAME,  angelConfig.s3bucketName)
        PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_ID,  angelConfig.s3accessId)
        PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_KEY,  angelConfig.s3accessKey)
        PreferenceUtil.createPreference(context, college, Preferences.COLLEGE_NAME,   collegeDTO.organisationName)

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

        

    }

    @Override
    Boolean verifyCollegeName(String name, String xGRecaptcha) {
        return ObjectSelect.query(College)
                .where(College.COLLEGE_KEY.eq(name))
                .or(College.WEB_SITES.dot(WebSite.NAME).eq(name))
                .select(cayenneService.newContext()).empty
    }

    private College recordNewCollege(String securityCode, String collegeKey, String name) {
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
        college.setPaymentGatewayAccount(null)
        college.setPaymentGatewayPass(null)
        college.setRequiresAvetmiss(true)
        college.setTimeZone("Australia/Sydney")

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
