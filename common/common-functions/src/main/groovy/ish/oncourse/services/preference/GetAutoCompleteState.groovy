package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import static ish.oncourse.services.preference.Preferences.SUBURB_AUTOCOMPLITE_STATE


class GetAutoCompleteState extends GetPreference {
    
    GetAutoCompleteState(College college, ObjectContext objectContext, WebSite webSite) {
        super(college, SUBURB_AUTOCOMPLITE_STATE, objectContext, webSite)
        if (!webSite) {
            throw new IllegalAccessException("$SUBURB_AUTOCOMPLITE_STATE preference awalible for web site only")
        }
    }

    String get() {
        return StringUtils.trimToNull(getValue())
    }
}
