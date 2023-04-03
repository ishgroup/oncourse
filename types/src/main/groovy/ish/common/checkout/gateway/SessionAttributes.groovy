/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.checkout.gateway

import ish.common.types.CreditCardType

import java.time.LocalDate

class SessionAttributes {

    String sessionId
    String ccFormUrl
    Boolean authorised
    Boolean complete
    String type
    String responceJson
    String errorMessage
    String reCo

    String transactionId
    String statusText
    String creditCardExpiry
    String creditCardName
    String creditCardNumber
    CreditCardType creditCardType
    LocalDate paymentDate
    String billingId
}
