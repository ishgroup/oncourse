package ish.oncourse.willow.preference

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.services.preference.GetCheckoutTermsLabel
import ish.oncourse.services.preference.GetCheckoutTermsUrl
import ish.oncourse.services.preference.GetContactAgeWhenNeedParent
import ish.oncourse.services.preference.IsCorporatePassEnabled
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
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
        ObjectContext context = cayenneService.sharedContext()
        College college = collegeService.college
        preferences.corporatePassEnabled = new IsCorporatePassEnabled(college, context).get()
        preferences.creditCardEnabled = new IsPaymentGatewayEnabled(college, context).get()
        preferences.minAge =  new GetContactAgeWhenNeedParent(college, context, collegeService.webSite).integerValue?.doubleValue()
        preferences.termsLabel = new GetCheckoutTermsLabel(college, context, collegeService.webSite).value
        preferences.termsUrl = new GetCheckoutTermsUrl(college, context, collegeService.webSite).value
        return preferences
    }
}
