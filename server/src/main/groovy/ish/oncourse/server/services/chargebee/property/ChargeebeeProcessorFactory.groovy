/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType

class ChargeebeeProcessorFactory {
    static ChargebeePropertyProcessor valueOf(ChargebeePropertyType type, Date startDate, Date endDate) {
        switch (type) {
            case ChargebeePropertyType.SMS:
                return new SmsChargebeeProperty(startDate, endDate)
            case ChargebeePropertyType.TOTAL_CORPORATE_PASS:
                return new TotalCorporatePassProperty(startDate, endDate)
            case ChargebeePropertyType.TOTAL_CREDIT_PAYMENT:
                return new TotalCreditProperty(startDate, endDate)
            case ChargebeePropertyType.TOTAL_CREDIT_WEB_PAYMENT_IN:
                return new TotalCreditWebPaymentInProperty(startDate, endDate)
            case ChargebeePropertyType.TOTAL_CREDIT_PAYMENT_IN:
                return new TotalCreditPaymentInProperty(startDate, endDate)
            case ChargebeePropertyType.TOTAL_OFFICE_PAYMENT_IN_NUMBER:
                return new TotalOfficePaymentProperty(startDate, endDate)
            default:
                throw new IllegalArgumentException("Try to upload chargebee usage for unsupported item type: " + type)
        }
    }
}
