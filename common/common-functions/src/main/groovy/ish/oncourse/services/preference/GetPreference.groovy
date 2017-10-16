package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.Preference
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect


class GetPreference {

    College college
    String key
    ObjectContext objectContext
    WebSite webSite

    GetPreference(College college, String key, ObjectContext objectContext) {
        this(college, key, objectContext, null)
    }
    
    GetPreference(College college, String key, ObjectContext objectContext, WebSite webSite) {
        this.college = college
        this.key = key
        this.objectContext = objectContext
        this.webSite = webSite
    }
    
    String getValue() {
        preference ? preference.valueString : null
    }

    Preference getPreference() {
        Expression exception = Preference.NAME.eq(key).andExp(Preference.COLLEGE.eq(college))
        if (webSite) {
            exception = exception.andExp(Preference.WEB_SITE.eq(webSite))
        }
        
        return ObjectSelect.query(Preference)
                .where(exception)
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
