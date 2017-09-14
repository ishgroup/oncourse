package ish.oncourse.services.preference

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import static ish.oncourse.services.preference.Preferences.SUBURB_AUTOCOMPLITE_STATE


class GetAutoCompleteState extends GetPreference {
    
    GetAutoCompleteState(College college, ObjectContext objectContext) {
        super(college, SUBURB_AUTOCOMPLITE_STATE, objectContext)
    }

    String get() {
        return StringUtils.trimToNull(getValue())
    }
}
