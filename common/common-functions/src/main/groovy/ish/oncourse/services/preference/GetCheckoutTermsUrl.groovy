/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import static ish.oncourse.services.preference.Preferences.CHECKOUT_TERMS_URL

class GetCheckoutTermsUrl extends GetPreference {
    
    GetCheckoutTermsUrl(College college, ObjectContext objectContext, WebSite webSite) {
        super(college, CHECKOUT_TERMS_URL, objectContext, webSite)
    }
    
}
