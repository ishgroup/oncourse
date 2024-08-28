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
import ish.oncourse.server.services.chargebee.ChargebeeItemType
import org.apache.commons.lang3.StringUtils

import javax.sql.DataSource

import static ish.oncourse.server.util.DbConnectionUtils.getLongForDbQuery

class TotalCorporatePassProperty extends ChargebeePropertyProcessor{
    private static final String QUERY_FORMAT = "SELECT SUM(p.amount) AS value" +
            "          FROM %s pil JOIN %s p ON pil.%sId = p.id JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          JOIN Invoice i ON pil.invoiceId = i.id" +
            "          WHERE pm.type = 2 " +
            "          AND p.createdOn >= '%s'" +
            "          AND p.createdOn < '%s'" +
            "          AND i.corporatePassId IS NOT NULL" +
            "          AND p.status IN (3, 6)"

    TotalCorporatePassProperty(Date startDate, Date endDate) {
        super(startDate, endDate)
    }

    @Override
    Long getValue(DataSource dataSource) {
        String paymentsInQuery = String.format(QUERY_FORMAT, PaymentInLine.simpleName, PaymentIn.simpleName,
                StringUtils.toRootLowerCase(PaymentIn.simpleName))
        def paymentsInTotal = getLongForDbQuery(paymentsInQuery, dataSource)

        String paymentsOutQuery = String.format(QUERY_FORMAT, PaymentOutLine.simpleName, PaymentOut.simpleName,
                StringUtils.toRootLowerCase(PaymentOut.simpleName))
        def paymentsOutTotal = getLongForDbQuery(paymentsOutQuery, dataSource)

        return paymentsInTotal + paymentsOutTotal
    }

    @Override
    ChargebeeItemType getType() {
        return ChargebeeItemType.TOTAL_CORPORATE_PASS
    }
}
