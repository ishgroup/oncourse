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

import ish.common.types.InvoiceType
import ish.oncourse.server.cayenne.AbstractInvoice
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.Quote
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class InvoiceDao implements CayenneLayer<AbstractInvoice> {

    @Override
    AbstractInvoice newObject(ObjectContext context) {
        return null
    }

    AbstractInvoice newObject(ObjectContext context, InvoiceType invoiceType) {
        switch (invoiceType) {
            case InvoiceType.QUOTE:
                return context.newObject(Quote.class)
            default:
                return context.newObject(Invoice.class)
        }
    }

    @Override
    AbstractInvoice getById(ObjectContext context, Long id) {
        SelectById.query(AbstractInvoice, id)
                .selectOne(context)
    }

    Integer getInvoicesCount(Contact contact) {
        return getInvoiceCount(contact) + getQuoteCount(contact)
    }

    Integer getInvoiceCount(Contact contact) {
        ObjectSelect.query(Invoice.class)
                .where(Invoice.CONTACT.eq(contact))
                .selectCount(contact.getContext())
    }

    Integer getQuoteCount(Contact contact) {
        ObjectSelect.query(Quote.class)
                .where(Quote.CONTACT.eq(contact))
                .selectCount(contact.getContext())
    }
}
