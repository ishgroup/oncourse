/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.utils

import groovy.transform.CompileStatic
import ish.oncourse.model.WebHostName
import ish.oncourse.model.WebHostNameStatus

@CompileStatic
class DomainUtils {
    /**
     * @return error if any of this domain ip addresses are checked by some python salt as not in range and WebHostName
     * status downgraded to NOT_VERIFIED and error message added to database
     */
    static String findNotInRangeIp(WebHostName host) {
        return host.status == WebHostNameStatus.NOT_VERIFIED ? host.message : null
    }
}
