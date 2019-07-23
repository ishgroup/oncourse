package ish.oncourse.services.preference

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang.StringUtils

import static ish.oncourse.services.preference.Preferences.*

class IsCreditNoteEnabled extends GetPreference {

    IsCreditNoteEnabled(College college, ObjectContext objectContext) {
        super(college,  ENROLMENT_CREDIT_NOTE_ENABLED, objectContext)
    }

    boolean get() {
        String valueString = StringUtils.trimToNull(getValue())
        if (valueString == null)
            return true
        return Boolean.valueOf(value)
    }
}
