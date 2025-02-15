/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway

import ish.common.checkout.gateway.SessionAttributes
import ish.math.Money
import ish.oncourse.server.api.v1.model.CheckoutSubmitRequestDTO

interface TransactionPaymentServiceInterface extends PaymentServiceInterface{
    String getClientKey()

    SessionAttributes sendTwoStepPayment(Money amount, CheckoutSubmitRequestDTO requestDTO)

    SessionAttributes confirmExistedPayment(Money amount, CheckoutSubmitRequestDTO requestDTO)
}