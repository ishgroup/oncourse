package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.persistence.CommonPreferenceController
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

class GetFeatureEnrolmentDisclosure extends GetPreference {


    GetFeatureEnrolmentDisclosure(College college, ObjectContext objectContext) {
        super(college, CommonPreferenceController.FEATURE_ENROLMENT_DISCLOSURE, objectContext)
    }

    String get() {
        return StringUtils.trimToNull(getValue())
    }
}
