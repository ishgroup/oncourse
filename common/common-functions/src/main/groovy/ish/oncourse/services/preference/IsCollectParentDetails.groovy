package ish.oncourse.services.preference

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import static ish.oncourse.services.preference.Preferences.ENROLMENT_collectParentDetails

class IsCollectParentDetails extends GetPreference {
    
    IsCollectParentDetails(College college, ObjectContext objectContext) {
        super(college, ENROLMENT_collectParentDetails, objectContext)
    }
    
    boolean get() {
        return Boolean.valueOf(StringUtils.trimToNull(getValue()))
    }
}
