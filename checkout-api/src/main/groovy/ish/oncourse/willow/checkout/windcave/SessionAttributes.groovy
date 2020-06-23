/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.checkout.windcave

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

    String transactionId
    String statusText
    String creditCardExpiry
    String creditCardName
    String creditCardNumber
    CreditCardType creditCardType
    LocalDate paymentDate
    String billingId
}
