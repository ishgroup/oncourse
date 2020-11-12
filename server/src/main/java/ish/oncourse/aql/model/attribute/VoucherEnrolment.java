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
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentInLine;
import ish.oncourse.server.cayenne.PaymentOut;
import ish.oncourse.server.cayenne.PaymentOutLine;
import ish.oncourse.server.cayenne.ProductItem;
import ish.oncourse.server.cayenne.Voucher;
import ish.oncourse.server.cayenne.VoucherPaymentIn;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.Optional;

public class VoucherEnrolment implements SyntheticAttributeDescriptor {


    private final EntityFactory factory;

    public VoucherEnrolment(EntityFactory factory) {
        this.factory = factory;
    }


    @Override
    public Class<? extends Persistent> getEntityType() {
        return ProductItem.class;
    }

    @Override
    public String getAttributeName() {
        return "redeemedEnrolment";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Enrolment.class));
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
                    ctx.reportError(-1, -1, "Invalid arguments in redeemedEnrolment property resolution.");
                }

                var idx = 1;
                for (var child : args.subList(2, args.size())) {
                    ExpressionUtil.addChild(parent, child, idx++);
                }
                ExpressionUtil.addChild(parent, Voucher.VOUCHER_PAYMENTS_IN.dot(VoucherPaymentIn.PAYMENT_IN).dot(PaymentIn.PAYMENT_IN_LINES)
                        .dot(PaymentInLine.INVOICE).dot(Invoice.INVOICE_LINES).dot(InvoiceLine.ENROLMENT).dot(Enrolment.ID).getExpression(), 0);
                var voucherIds = ObjectSelect.columnQuery(Voucher.class, PaymentOutLine.ID).where(parent).select(ctx.getContext());


                return (SimpleNode) ProductItem.ID.in(voucherIds);
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }
        };

    }
}
