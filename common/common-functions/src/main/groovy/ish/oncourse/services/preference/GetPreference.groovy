package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.Preference
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils


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
                .localCache(Preference.simpleName)
                .selectOne(objectContext)
    }

    Integer getIntegerValue() {
        String valueString = value
        if (valueString) {
            return Integer.parseInt(valueString)
        }
        return 0
    }
    void setIntegerValue(Integer integer) {
        if (value != null) {
            setValue(String.valueOf(integer))
        }
    }
    
    Boolean getBooleanValue() {
        return Boolean.parseBoolean(getValue())
    }
    
    void setBooleanValue(Boolean value) {
        if (value != null) {
            setValue(Boolean.toString(value))
        }
    }

    String getStringValue() {
        return StringUtils.trimToNull(getValue())
    }
    
    void setStringValue(String value) {
        setValue(value)
    }
    
    void setValue(String value) {
        Preference pref = getPreference()
        college = objectContext.localObject(college)
        if (pref == null) {
            pref = objectContext.newObject(Preference)
            pref.setName(key)
        } 
        
        pref.college = college
        if (webSite) {
            pref.webSite = objectContext.localObject(webSite)
        }
        
        pref.valueString = StringUtils.trimToNull(value)
    }
}
