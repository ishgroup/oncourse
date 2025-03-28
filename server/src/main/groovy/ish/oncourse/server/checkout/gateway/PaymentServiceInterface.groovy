/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway

import ish.common.checkout.gateway.SessionAttributes
import ish.math.Money
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutCCResponseDTO
import ish.oncourse.server.cayenne.Contact

interface PaymentServiceInterface extends PaymentServiceTrait {

    CheckoutCCResponseDTO succeedPaymentAndCompleteTransaction(Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, String merchantReference)

    SessionAttributes checkStatus(String sessionIdOrAccessCode)

    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId)

    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId)
}