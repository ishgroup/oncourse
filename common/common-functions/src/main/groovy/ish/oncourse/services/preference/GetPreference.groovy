package ish.oncourse.services.preference

import ish.math.Country
import ish.oncourse.model.College
import ish.oncourse.model.Preference
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.commons.lang3.StringUtils

import static ish.persistence.Preferences.ACCOUNT_CURRENCY


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

    Country getCountry() {
        String result = getValue()
        if (result == null) {
            return Country.AUSTRALIA
        }
        return Country.forCurrencySymbol(result)
    }
    
    String getValue() {
        preference ? preference.valueString : null
    }

    Preference getPreference() {
        Expression exception = Preference.NAME.eq(key)
        if (college) {
            exception = exception.andExp(Preference.COLLEGE.eq(college))
        } else {
            exception = exception.andExp(Preference.COLLEGE.isNull())
        }
        if (webSite) {
            exception = exception.andExp(Preference.WEB_SITE.eq(webSite))
        }
        
        return ObjectSelect.query(Preference)
                .where(exception)
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Preference.simpleName)
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
        if (integer != null) {
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

        if (pref == null) {
            pref = objectContext.newObject(Preference)
            pref.setName(key)
        }

        if (college) {
            pref.college = objectContext.localObject(college)
        }

        if (webSite) {
            pref.webSite = objectContext.localObject(webSite)
        }
        
        pref.valueString = StringUtils.trimToEmpty(value)
    }
}
