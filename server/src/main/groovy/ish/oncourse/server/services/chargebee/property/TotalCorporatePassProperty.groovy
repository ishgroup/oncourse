/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import ish.common.chargebee.ChargebeePropertyType
import org.apache.commons.lang3.StringUtils

import javax.sql.DataSource

import static ish.oncourse.server.util.DbConnectionUtils.getBigDecimalForDbQuery

class TotalCorporatePassProperty extends ChargebeePropertyProcessor{
    private static final String QUERY_FORMAT = "SELECT SUM(p.amount) AS value" +
            "          FROM %s pil JOIN %s p ON pil.%sId = p.id JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          JOIN Invoice i ON pil.invoiceId = i.id" +
            "          WHERE pm.type = 2 " +
            "          AND p.createdOn >= '%s'" +
            "          AND p.createdOn < '%s'" +
            "          AND i.corporatePassId IS NOT NULL" +
            "          AND p.status IN (3, 6)"

    private DataSource dataSource

    TotalCorporatePassProperty(Date startDate, Date endDate, DataSource dataSource) {
        super(startDate, endDate)
        this.dataSource = dataSource
    }

    @Override
    BigDecimal getValue() {
        String paymentsInQuery = String.format(QUERY_FORMAT, PaymentInLine.simpleName, PaymentIn.simpleName,
                StringUtils.toRootLowerCase(PaymentIn.simpleName), formattedStartDate, formattedEndDate)
        def paymentsInTotal = getBigDecimalForDbQuery(paymentsInQuery, dataSource)

        String paymentsOutQuery = String.format(QUERY_FORMAT, PaymentOutLine.simpleName, PaymentOut.simpleName,
                StringUtils.toRootLowerCase(PaymentOut.simpleName), formattedStartDate, formattedEndDate)
        def paymentsOutTotal = getBigDecimalForDbQuery(paymentsOutQuery, dataSource)

        return paymentsInTotal.add(paymentsOutTotal)
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_CORPORATE_PASS
    }
}
