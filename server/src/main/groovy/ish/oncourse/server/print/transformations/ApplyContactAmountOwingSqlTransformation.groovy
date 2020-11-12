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

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.DataRow
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SQLSelect
import org.apache.cayenne.query.SelectById

@CompileStatic
class ApplyContactAmountOwingSqlTransformation {
    AmountOwingSql sql = new AmountOwingSql()

    ObjectContext context
    Date date

    List<? extends PersistentObjectI> apply() {
        List<DataRow> list = SQLSelect.dataRowQuery(sql.sqlResult).params('date', date).select(context)
        return list.collect { row ->
            new ContactDataRowDelegator(contact: SelectById.query(Contact, row.ID).selectOne(context), dataRow: row)
        }
    }
}

