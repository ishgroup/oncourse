package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext

import static ish.oncourse.services.preference.Preferences.CHECKOUT_guardianRequiredAge

class GetContactAgeWhenNeedParent extends GetPreference {
    
    GetContactAgeWhenNeedParent(College college, ObjectContext objectContext, WebSite webSite) {
        super(college, CHECKOUT_guardianRequiredAge, objectContext, webSite)
    }
}
