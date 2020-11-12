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
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentInLine;
import ish.oncourse.server.cayenne.PaymentOut;
import ish.oncourse.server.cayenne.PaymentOutLine;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactAccountTransactions implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public ContactAccountTransactions(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Contact.class;
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
        return new LazyExpressionNode() {
            @Override
            public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
                if (ctx.hasErrors()) {
                    return null;
                }
                if (args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
                    ctx.reportError(-1, -1, "Invalid arguments in account transaction property resolution.");
                }

                List<Property<Long>> queryList = new ArrayList<>();
                queryList.add(Contact.INVOICES.dot(Invoice.INVOICE_LINES).dot(InvoiceLine.ID));
                queryList.add(Contact.PAYMENTS_IN.dot(PaymentIn.PAYMENT_IN_LINES).dot(PaymentInLine.ID));
                queryList.add(Contact.PAYMENTS_OUT.dot(PaymentOut.PAYMENT_OUT_LINES).dot(PaymentOutLine.ID));

                List<AccountTransactionType> transactionTypes = new ArrayList<>() ;
                transactionTypes.add(AccountTransactionType.INVOICE_LINE);
                transactionTypes.add(AccountTransactionType.PAYMENT_IN_LINE);
                transactionTypes.add(AccountTransactionType.PAYMENT_OUT_LINE);

                Expression result = null;
                for (int i = 0; i < transactionTypes.size(); i++) {
                    var nodeToProcess = (SimpleNode) parent.deepCopy();
                    var argsToProcess = args;
                    var currNode = new AccountTransactionLazyNode(queryList.get(i), transactionTypes.get(i), true)
                            .resolveParent(nodeToProcess, argsToProcess, ctx);
                    if (result == null) {
                        result = currNode;
                    } else {
                        result = result.orExp(currNode);
                    }
                }

                return (SimpleNode) result;
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }
        };
    }
}
