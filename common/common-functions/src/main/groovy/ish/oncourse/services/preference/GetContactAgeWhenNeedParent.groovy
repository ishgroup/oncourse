package ish.oncourse.services.preference

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.services.preference.Preferences.DEFAULT_contactAgeWhenNeedParent
import static ish.oncourse.services.preference.Preferences.ENROLMENT_contactAgeWhenNeedParent

class GetContactAgeWhenNeedParent extends GetPreference {
    
    GetContactAgeWhenNeedParent(College college, ObjectContext objectContext) {
        super(college, ENROLMENT_contactAgeWhenNeedParent, objectContext)
    }
    
    Integer get() {
        String valueString = StringUtils.trimToNull(getValue())
        return (valueString != null && StringUtils.isNumeric(valueString)) ? Integer.valueOf(valueString): DEFAULT_contactAgeWhenNeedParent
    }
}
