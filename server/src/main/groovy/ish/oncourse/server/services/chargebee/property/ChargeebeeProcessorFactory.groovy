/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.oncourse.server.services.chargebee.ChargebeeItemType

class ChargeebeeProcessorFactory {
    static ChargebeePropertyProcessor valueOf(ChargebeeItemType type, Date startDate, Date endDate) {
        switch (type) {
            case ChargebeeItemType.SMS:
                return new SmsChargebeeProperty(startDate, endDate)
            case ChargebeeItemType.TOTAL_CORPORATE_PASS:
                return new TotalCorporatePassProperty(startDate, endDate)
            case ChargebeeItemType.TOTAL_CREDIT_PAYMENT:
                return new TotalCreditProperty(startDate, endDate)
            case ChargebeeItemType.TOTAL_CREDIT_WEB_PAYMENT_IN:
                return new TotalCreditWebPaymentInProperty(startDate, endDate)
            case ChargebeeItemType.TOTAL_CREDIT_PAYMENT_IN:
                return new TotalCreditPaymentInProperty(startDate, endDate)
            default:
                throw new IllegalArgumentException("Try to upload chargebee usage for unsupported item type: " + type)
        }
    }
}
