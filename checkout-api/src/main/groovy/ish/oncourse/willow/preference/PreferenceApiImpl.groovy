package ish.oncourse.willow.preference

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.services.preference.GetEnrolSuccessUrl
import ish.oncourse.services.preference.GetFeatureEnrolmentDisclosure
import ish.oncourse.services.preference.GetRefundPolicyUrl
import ish.oncourse.services.preference.IsCorporatePassEnabled
import ish.oncourse.services.preference.IsCreditCardPaymentEnabled
import ish.oncourse.willow.model.common.Preferences
import ish.oncourse.willow.service.PreferenceApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PreferenceApiImpl implements PreferenceApi {
    
    final static  Logger logger = LoggerFactory.getLogger(PreferenceApiImpl)

    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    PreferenceApiImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }

    @Override
    Preferences getPreferences() {
        Preferences preferences = new Preferences()
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        preferences.corporatePassEnabled = new IsCorporatePassEnabled(college, context).get()
        preferences.creditCardEnabled = new IsCreditCardPaymentEnabled(college, context).get()
        preferences.successLink = new GetEnrolSuccessUrl(college, context).get()
        preferences.refundPolicyUrl = new GetRefundPolicyUrl(college, context).get()
        preferences.featureEnrolmentDisclosure = new GetFeatureEnrolmentDisclosure(college, context).get()
        preferences.googleTagmanagerAccount = collegeService.webSite.googleTagmanagerAccount
        return preferences
    }
}
