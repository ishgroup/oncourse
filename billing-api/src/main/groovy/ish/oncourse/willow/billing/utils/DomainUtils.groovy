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
    private static final String UNKNOWN_HOST_ERROR = "domain setup is not correct. Go to your DNS hosting provider and set the following:\n" +
            "sttrinians.ish.com.au A 202.167.247.94\n" +
            "sttrinians.ish.com.au AAAA 2404:4f00:1010:1::6"

    /**
     * @return error if any of this domain ip addresses are checked by some python salt as not in range and WebHostName
     * status downgraded to NOT_VERIFIED
     */
    static String findNotInRangeIp(WebHostName host) {
        return host.status == WebHostNameStatus.NOT_VERIFIED ? UNKNOWN_HOST_ERROR : null
    }
}
