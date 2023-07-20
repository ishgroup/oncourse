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

package ish.oncourse.aql.model.attribute;

import ish.common.types.AccountTransactionType;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

public class InvoiceAccountTransactions implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public InvoiceAccountTransactions(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return AbstractInvoice.class;
    }

    @Override
    public String getAttributeName() {
        return "accountTransactions";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(AccountTransaction.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new AccountTransactionLazyNode(Invoice.INVOICE_LINES.dot(InvoiceLine.ID), AccountTransactionType.INVOICE_LINE, false);
    }
}
