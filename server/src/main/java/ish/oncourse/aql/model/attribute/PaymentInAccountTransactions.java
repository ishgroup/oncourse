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
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentInLine;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

public class PaymentInAccountTransactions implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public PaymentInAccountTransactions(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return PaymentIn.class;
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
        return new AccountTransactionLazyNode(PaymentIn.PAYMENT_IN_LINES.dot(PaymentInLine.ID), AccountTransactionType.PAYMENT_IN_LINE, false);
    }
}
