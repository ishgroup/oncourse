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
import ish.oncourse.aql.impl.ExpressionUtil;
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
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountTransactionContact implements SyntheticAttributeDescriptor {

    private final EntityFactory factory;

    public AccountTransactionContact(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return AccountTransaction.class;
    }

    @Override
    public String getAttributeName() {
        return "contact";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Contact.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new LazyExpressionNode() {
            @Override
            public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
                if (ctx.hasErrors()) {
                    return null;
                }

                if(args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
                    ctx.reportError(-1, -1, "Invalid arguments in contact property resolution.");
                }
                var pathExp = (ASTPath) args.get(1);
                var other = args.subList(2, args.size());

                var path = pathExp.getPath();
                var contactPos = path.indexOf(".contact");
                var pathPrefix = contactPos <= 0 ? "" : path.substring(0, path.indexOf(".contact"));
                contactPos = path.indexOf("contact.");
                var pathSuffix = contactPos < 0 ? "" : path.substring(contactPos + "contact.".length());

                var idx = 1;
                for (var child : other) {
                    ExpressionUtil.addChild(parent, child, idx++);
                }

                Property<?> contactPath = InvoiceLine.INVOICE.dot(Invoice.CONTACT);
                if(pathSuffix.length() > 0) {
                    contactPath = contactPath.dot(pathSuffix);
                }
                ExpressionUtil.addChild(parent, contactPath.getExpression(), 0);
                var invoiceLineIds = ObjectSelect.columnQuery(InvoiceLine.class, InvoiceLine.ID)
                        .where(parent)
                        .select(ctx.getContext());

                contactPath = PaymentInLine.PAYMENT_IN.dot(PaymentIn.PAYER);
                if(pathSuffix.length() > 0) {
                    contactPath = contactPath.dot(pathSuffix);
                }
                ExpressionUtil.addChild(parent, contactPath.getExpression(), 0);
                var paymentInLineIds = ObjectSelect.columnQuery(PaymentInLine.class, PaymentInLine.ID)
                        .where(parent)
                        .select(ctx.getContext());

                contactPath = PaymentOutLine.PAYMENT_OUT.dot(PaymentOut.PAYEE);
                if(pathSuffix.length() > 0) {
                    contactPath = contactPath.dot(pathSuffix);
                }
                ExpressionUtil.addChild(parent, contactPath.getExpression(), 0);
                var paymentOutLineExp = ObjectSelect.columnQuery(PaymentOutLine.class, PaymentOutLine.ID)
                        .where(parent)
                        .select(ctx.getContext());

                Property<?> pathPrefixProp = Property.create(pathPrefix, Persistent.class);
                var fkRecordId = pathPrefix.isEmpty() ?
                        AccountTransaction.FOREIGN_RECORD_ID :
                        pathPrefixProp.dot(AccountTransaction.FOREIGN_RECORD_ID);
                var tableName = pathPrefix.isEmpty() ?
                        AccountTransaction.TABLE_NAME :
                        pathPrefixProp.dot(AccountTransaction.TABLE_NAME);

                return (SimpleNode) fkRecordId.in(invoiceLineIds)
                        .andExp(tableName.eq(AccountTransactionType.INVOICE_LINE))
                        .orExp(fkRecordId.in(paymentInLineIds)
                                .andExp(tableName.eq(AccountTransactionType.PAYMENT_IN_LINE)))
                        .orExp(fkRecordId.in(paymentOutLineExp)
                                .andExp(tableName.eq(AccountTransactionType.PAYMENT_OUT_LINE)));
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }
        };
    }
}
