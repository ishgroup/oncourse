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
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.cayenne.VoucherPaymentIn;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.Optional;

public class AccountTransactionPaymentIn implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public AccountTransactionPaymentIn(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return AccountTransaction.class;
    }

    @Override
    public String getAttributeName() {
        return "paymentIn";
    }


    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(PaymentIn.class));
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
                    ctx.reportError(-1, -1, "Invalid arguments in paymentIn property resolution.");
                }
                var basePath = ((ASTPath) args.get(1)).getPath();

                var idx = 0;
                var tempParent = parent;
                args.set(1, (SimpleNode) PaymentInLine.PAYMENT_IN.dot(getSearchProperty(basePath)).getExpression());

                for (var child : args.subList(1, args.size())) {
                    if (child instanceof LazyExpressionNode) {
                        tempParent = ((LazyExpressionNode) child).resolveParent(tempParent, args.subList(1, args.size()), ctx);
                    } else {
                        ExpressionUtil.addChild(tempParent, child, idx++);
                    }
                }
                var paymentInLineIds = ObjectSelect.columnQuery(PaymentInLine.class, PaymentInLine.ID).where(tempParent).select(ctx.getContext());


                idx = 0;
                args.set(1, (SimpleNode) InvoiceLine.INVOICE.dot(Invoice.PAYMENT_IN_LINES).dot(PaymentInLine.PAYMENT_IN).dot(getSearchProperty(basePath)).getExpression());

                for (var child : args.subList(1, args.size())) {
                    if (child instanceof LazyExpressionNode) {
                        parent = ((LazyExpressionNode) child).resolveParent(parent, args.subList(1, args.size()), ctx);
                    } else {
                        ExpressionUtil.addChild(parent, child, idx++);
                    }
                }
                var invoiceLineIds = ObjectSelect.columnQuery(InvoiceLine.class, InvoiceLine.ID).where(parent).select(ctx.getContext());

                return (SimpleNode) AccountTransaction.FOREIGN_RECORD_ID.in(paymentInLineIds).andExp(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE)).
                        orExp(AccountTransaction.FOREIGN_RECORD_ID.in(invoiceLineIds).andExp(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.INVOICE_LINE)));
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }

            private String getSearchProperty(String path) {
                if (path.isEmpty()) {
                    return "id";
                }

                var pathComponents = path.split("\\.");
                return pathComponents[pathComponents.length-1];
            }
        };
    }

    @Override
    public Optional<Class<?>> getAttributeType() {
        return Optional.of(Integer.class);
    }
}
