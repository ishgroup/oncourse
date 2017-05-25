package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.Preference
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class GetPreference {

    College college
    String key
    ObjectContext objectContext

    GetPreference(College college, String key, ObjectContext objectContext) {
        this.college = college
        this.key = key
        this.objectContext = objectContext
    }
    
    String getValue() {
        preference ? preference.valueString : null
    }

    Preference getPreference() {
        (ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(key)) & Preference.COLLEGE.eq(college))
                .localCache(Preference.class.getSimpleName())
                .selectOne(objectContext)
    }

    Integer getInteger() {
        String valueString = value
        if (getPreference()) {
            Integer.parseInt(valueString)
        }
        return 0
    }
}
