/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.checkout.gateway.stripe.session

import org.apache.commons.lang3.StringUtils

enum StripeSessionStatus {

    Not_Initializaed('not_init'),
    Open('open'),
    Complete('complete'),
    Expired('expired'),
    Failed('failed')

    private String displayName

    StripeSessionStatus(String displayName) {
        this.displayName = displayName
    }

    static StripeSessionStatus from(String displayName) {
        values().find { StringUtils.equalsIgnoreCase(it.displayName, displayName) }
    }

    String getDisplayName() {
        return displayName
    }
}