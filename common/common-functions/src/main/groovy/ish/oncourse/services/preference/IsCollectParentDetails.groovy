package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import static ish.oncourse.services.preference.Preferences.CHECKOUT_collectParentDetails

class IsCollectParentDetails extends GetPreference {
    
    IsCollectParentDetails(College college, ObjectContext objectContext, WebSite site) {
        super(college, CHECKOUT_collectParentDetails, objectContext, site)
    }
}
