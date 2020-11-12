/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.print.transformations

class AmountOwingSql {
    String sqlInvoice
    String sqlPaymentIn
    String sqlPaymentOut
    String sqlFinances
    String sqlContact

    String sqlResult

    AmountOwingSql() {
        init()
    }

    private void init() {
        assert sqlResult == null
        String file = this.class.getResourceAsStream("AmountOwingSql.sql").text
        String[] sqls = file.substring(file.indexOf("select")).split(';')
        assert sqls != null && sqls.length != 5
        sqlInvoice = sqls[0].trim()
        sqlPaymentIn = sqls[1].trim()
        sqlPaymentOut = sqls[2].trim()
        sqlFinances = sqls[3].trim()
        sqlContact = sqls[4].trim()

        sqlResult = String.format(sqlContact, String.format(sqlFinances, sqlInvoice, sqlPaymentIn, sqlPaymentOut))
    }

}
