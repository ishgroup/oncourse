/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut

import javax.sql.DataSource

import static ish.oncourse.server.services.chargebee.ChargebeeQueryUtils.TOTAL_CREDIT_PAYMENT_COUNT_QUERY_FORMAT
import static ish.oncourse.server.util.DbConnectionUtils.getBigDecimalForDbQuery

class TotalCreditCountProperty extends ChargebeePropertyProcessor{
    private DataSource dataSource

    TotalCreditCountProperty(Date startDate, Date endDate, DataSource dataSource) {
        super(startDate, endDate)
        this.dataSource = dataSource
    }

    @Override
    BigDecimal getValue() {
        String paymentsInQuery = String.format(TOTAL_CREDIT_PAYMENT_COUNT_QUERY_FORMAT, PaymentIn.simpleName, formattedStartDate, formattedEndDate)
        def paymentsInTotal = getBigDecimalForDbQuery(paymentsInQuery, dataSource)

        String paymentsOutQuery = String.format(TOTAL_CREDIT_PAYMENT_COUNT_QUERY_FORMAT, PaymentOut.simpleName, formattedStartDate, formattedEndDate)
        def paymentsOutTotal = getBigDecimalForDbQuery(paymentsOutQuery, dataSource)

        return paymentsInTotal.add(paymentsOutTotal)
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_CREDIT_PAYMENT
    }
}
