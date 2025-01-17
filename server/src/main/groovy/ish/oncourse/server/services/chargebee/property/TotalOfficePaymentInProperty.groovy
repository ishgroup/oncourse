/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType
import ish.common.types.AccountTransactionType
import ish.common.types.PaymentSource
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import org.apache.commons.lang3.StringUtils

import javax.sql.DataSource

import static ish.oncourse.server.util.DbConnectionUtils.getBigDecimalForDbQuery

class TotalOfficePaymentInProperty extends ChargebeePropertyProcessor{
    private static final String QUERY_FORMAT = "SELECT COUNT(*) AS value" +
            "          FROM PaymentIn p " +
            "          JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 "+
            "          AND p.createdOn >= '%s'" +
            "          AND p.createdOn < '%s'" +
            "          AND p.source = '$PaymentSource.SOURCE_ONCOURSE.databaseValue'"

    private DataSource dataSource

    TotalOfficePaymentInProperty(Date startDate, Date endDate, DataSource dataSource) {
        super(startDate, endDate)
        this.dataSource = dataSource
    }

    @Override
    BigDecimal getValue() {
        String paymentsInQuery = String.format(QUERY_FORMAT, formattedStartDate, formattedEndDate)
        return getBigDecimalForDbQuery(paymentsInQuery, dataSource)
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_OFFICE_PAYMENT_IN_NUMBER
    }
}
