/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.services.chargebee.ChargebeeItemType

import javax.sql.DataSource

import static ish.oncourse.server.services.chargebee.ChargebeeQueryUtils.TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT
import static ish.oncourse.server.util.DbConnectionUtils.getLongForDbQuery

class TotalCreditProperty extends ChargebeePropertyProcessor{
    TotalCreditProperty(Date startDate, Date endDate) {
        super(startDate, endDate)
    }

    @Override
    Long getValue(DataSource dataSource) {
        String paymentsInQuery = String.format(TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT, PaymentIn.simpleName, formattedStartDate, formattedEndDate)
        def paymentsInTotal = getLongForDbQuery(paymentsInQuery, dataSource)

        String paymentsOutQuery = String.format(TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT, PaymentOut.simpleName, formattedStartDate, formattedEndDate)
        def paymentsOutTotal = getLongForDbQuery(paymentsOutQuery, dataSource)

        return paymentsInTotal + paymentsOutTotal
    }

    @Override
    ChargebeeItemType getType() {
        return ChargebeeItemType.TOTAL_CREDIT_PAYMENT
    }
}
