/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.checkout

import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import java.time.LocalDate

import static ish.common.types.ConfirmationStatus.DO_NOT_SEND
import static ish.common.types.ConfirmationStatus.NOT_SENT
import static ish.common.types.PaymentType.CREDIT_CARD
import static java.math.BigDecimal.ZERO

class Checkout {
    PaymentIn paymentIn
    Invoice invoice
    List<CheckoutValidationErrorDTO> errors
    ObjectContext context

    Boolean isCreditCard() {
        PaymentType.CREDIT_CARD == paymentIn?.paymentMethod?.type
    }

    void configurePaymentMethod(String paymentMethodId, CheckoutModelDTO checkout) {
        PaymentMethod method
        if (paymentIn.amount > ZERO) {
            if (paymentMethodId != null) {
                method = SelectById.query(PaymentMethod, paymentMethodId).selectOne(context)
            } else if (checkout.payWithSavedCard) {
                method = PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod)
            } else {
                throw new IllegalStateException('Payment method must be set')
            }
        } else {
            method = PaymentMethodUtil.getCONTRAPaymentMethods(context, PaymentMethod)
        }

        paymentIn.paymentMethod = method

        if (CREDIT_CARD != paymentIn.paymentMethod.type) {
            paymentIn.paymentDate = checkout.paymentDate ?: LocalDate.now()
        }

        if (CREDIT_CARD == paymentIn.paymentMethod.type) {
            paymentIn.status = PaymentStatus.IN_TRANSACTION
            //send only when payment success
            paymentIn.confirmationStatus = DO_NOT_SEND
        } else {
            paymentIn.status = PaymentStatus.SUCCESS
            paymentIn.confirmationStatus = checkout.sendInvoice ? NOT_SENT : DO_NOT_SEND
        }
    }
}
