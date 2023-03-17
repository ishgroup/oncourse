/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway

import ish.math.Money
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.windcave.SessionAttributes

interface PaymentServiceInterface extends PaymentServiceTrait {

    public static String Test = "400"

    SessionAttributes createSession(String origin, Money amount, String merchantReference, Boolean storeCard, Contact contact)

    void succeedPaymentAndCompleteTransaction(CheckoutResponseDTO dtoResponse, Checkout checkout, Boolean sendInvoice, SessionAttributes sessionAttributes, Money amount, String merchantReference)

    SessionAttributes checkStatus(String sessionIdOrAccessCode)

    SessionAttributes makeRefund(Money amount, String merchantReference, String transactionId)

    SessionAttributes makeTransaction(Money amount, String merchantReference, String cardId)

}