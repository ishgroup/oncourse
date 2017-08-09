package ish.oncourse.services.preference

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

class GetRefundPolicyUrl extends GetPreference {

    GetRefundPolicyUrl(College college, ObjectContext objectContext) {
        super(college, Preferences.REFUND_POLICY_URL, objectContext)
    }

    String get() {
        String url = StringUtils.trimToNull(getValue())
        if (url && !new URI(url).isAbsolute() && !url.startsWith("/")) {
            return  "/$url"
        }
        return url
    }
}
