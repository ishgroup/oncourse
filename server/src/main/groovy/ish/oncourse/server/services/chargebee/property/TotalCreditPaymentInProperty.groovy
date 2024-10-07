/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.oncourse.server.cayenne.PaymentIn
import ish.common.chargebee.ChargebeePropertyType

import static ish.oncourse.server.services.chargebee.ChargebeeQueryUtils.TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT

class TotalCreditPaymentInProperty extends ChargebeeSimplePropertyProcessor{
    TotalCreditPaymentInProperty(Date startDate, Date endDate) {
        super(startDate, endDate)
    }

    @Override
    String getQuery() {
        return String.format(TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT, PaymentIn.simpleName, formattedStartDate, formattedEndDate)
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_CREDIT_PAYMENT_IN
    }
}
