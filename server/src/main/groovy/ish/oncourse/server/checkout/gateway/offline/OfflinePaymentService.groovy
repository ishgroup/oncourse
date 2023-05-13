/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.offline

import ish.common.checkout.gateway.SessionAttributes
import ish.math.Money
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.PaymentServiceInterface

class OfflinePaymentService implements PaymentServiceInterface {
    @Override
    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact) {
        throw new IllegalArgumentException("Sorry, you can't use createSession method using Offline payment type. Please contact the administrator.")
    }

    @Override
    void succeedPaymentAndCompleteTransaction(CheckoutResponseDTO dtoResponse, Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, Money amount, String merchantReference) {
        throw new IllegalArgumentException("Sorry, you can't use succeedPaymentAndCompleteTransaction method using Offline payment type. Please contact the administrator.")
    }

    @Override
    SessionAttributes checkStatus(String sessionIdOrAccessCode) {
        throw new IllegalArgumentException("Sorry, you can't use checkStatus method using Offline payment type. Please contact the administrator.")
    }

    @Override
    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId) {
        throw new IllegalArgumentException("Sorry, you can't use makeRefund method using Offline payment type. Please contact the administrator.")
    }

    @Override
    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId) {
        throw new IllegalArgumentException("Sorry, you can't use makeTransaction method using Offline payment type. Please contact the administrator.")
    }
}
