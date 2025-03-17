/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.checkout.gateway.stripe

import ish.common.types.CreditCardType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CardTypeAdapter {
    private static final Logger logger = LogManager.getLogger(CardTypeAdapter)


    static CreditCardType convertFromStripeBrand(String cardBrand) {
        switch (cardBrand) {
            case "visa":
                return CreditCardType.VISA
            case "mastercard":
                return CreditCardType.MASTERCARD
            case "amex":
            case "american_express":
                return CreditCardType.AMEX
            default:
                logger.error("Can't map stripe credit card type ($cardBrand) to CreditCardType.")
        }
    }
}
