/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee

class ChargebeeQueryUtils {

    public static final String TOTAL_CREDIT_PAYMENT_AMOUNT_QUERY_FORMAT = "SELECT SUM(p.amount) AS value" +
            "          FROM %s p JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 " +
            "          AND p.createdOn >= '%s'" +
            "          AND p.createdOn < '%s'" +
            "          AND p.status IN (3, 6)"

    public static final String TOTAL_CREDIT_PAYMENT_COUNT_QUERY_FORMAT = "SELECT COUNT(*) AS value" +
            "          FROM %s p JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 " +
            "          AND p.createdOn >= '%s'" +
            "          AND p.createdOn < '%s'" +
            "          AND p.status IN (3, 6)"
}
