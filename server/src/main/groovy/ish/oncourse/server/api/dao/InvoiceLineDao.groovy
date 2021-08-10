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

package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.AbstractInvoiceLine
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.QuoteLine
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class InvoiceLineDao implements CayenneLayer<AbstractInvoiceLine> {

    @Override
    AbstractInvoiceLine newObject(ObjectContext context) {
        context.newObject(AbstractInvoiceLine)
    }

    InvoiceLine newInvoiceLine(ObjectContext context) {
        context.newObject(InvoiceLine)
    }

    QuoteLine newQuoteLine(ObjectContext context) {
        context.newObject(QuoteLine)
    }

    @Override
    AbstractInvoiceLine getById(ObjectContext context, Long id) {
        SelectById.query(AbstractInvoiceLine, id)
                .selectOne(context)
    }

    AbstractInvoiceLine getById(ObjectContext context, Long id, Class<? extends AbstractInvoiceLine> aClass) {
        if (id == null) {
            return null
        }
        return SelectById.query(aClass, id).selectOne(context)
    }
}
