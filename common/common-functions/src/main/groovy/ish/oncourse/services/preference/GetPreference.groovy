package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.Preference
import org.apache.cayenne.query.ObjectSelect

class GetPreference {

    College college
    String key
    
    String get() {
        Preference preference = (ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(key)) & Preference.COLLEGE.eq(college))
                .selectOne(college.objectContext)
        preference ? preference.valueString : null
    }
}
