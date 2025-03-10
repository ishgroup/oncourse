/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.common.checkout.gateway.stripe.payment

import org.apache.commons.lang3.StringUtils

enum PaymentIntentStatus {

    Canceled ('canceled'),
    Processing ('processing'),
    RequiresAction ('requires_action'),

    RequiresCapture ('requires_capture'),
    RequiresConfirmation ('requires_confirmation'),
    RequiresPaymentMethod ('requires_payment_method'),
    Succeeded ('succeeded'),
    Failed ('failed')

    private String displayName

    PaymentIntentStatus(String displayName) {
        this.displayName = displayName
    }

    static PaymentIntentStatus from(String displayName) {
        values().find { StringUtils.equalsIgnoreCase(it.displayName, displayName) }
    }

    String getDisplayName() {
        return displayName
    }

    static boolean paymentIsAuthorized(PaymentIntentStatus status) {
        return [RequiresCapture, RequiresConfirmation, RequiresPaymentMethod, Succeeded].contains(status)
    }

    static boolean paymentIsAuthorized(String status) {
        def statusItem = from(status)
        if (statusItem == null) {
            return false
        }
        return [RequiresCapture, RequiresConfirmation, RequiresPaymentMethod, Succeeded].contains(statusItem)
    }
}
