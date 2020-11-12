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

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentInLine;
import ish.oncourse.server.cayenne.Product;
import ish.oncourse.server.cayenne.ProductItem;
import ish.oncourse.server.cayenne.Voucher;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.parser.ASTBetween;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VoucherPaymentIn  implements SyntheticAttributeDescriptor {

    private final EntityFactory factory;

    public VoucherPaymentIn(EntityFactory factory)  {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return ProductItem.class;
    }

    @Override
    public String getAttributeName() {
        return "redeemedPaymentIn";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(PaymentIn.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new PaymentInAttributeLazyNode();
    }
    private static class PaymentInAttributeLazyNode extends LazyExpressionNode {
        @Override
        public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
            if (ctx.hasErrors()) {
                return null;
            }
            if(args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
                ctx.reportError(-1, -1, "Invalid arguments in paymentIn property resolution.");
            }

            var idx = 0;
            var basePath = ((ASTPath) args.get(1)).getPath();
            args.set(1, (SimpleNode) Voucher.VOUCHER_PAYMENTS_IN.dot(ish.oncourse.server.cayenne.VoucherPaymentIn.PAYMENT_IN).dot(getSearchProperty(basePath)).getExpression());

            for (var child : args.subList(1, args.size())) {
                if (child instanceof LazyExpressionNode) {
                    parent = ((LazyExpressionNode) child).resolveParent(parent, args.subList(1, args.size()), ctx);
                } else {
                    ExpressionUtil.addChild(parent, child, idx++);
                }
            }

            var vouchers = ObjectSelect.columnQuery(Voucher.class, Voucher.ID).where(parent).select(ctx.getContext());
            return (SimpleNode) ProductItem.ID.in(vouchers);
        }

        @Override
        public SimpleNode resolveSelf(CompilationContext ctx) {
            return this;
        }

        private String getSearchProperty(String path) {
            if(path.isEmpty()) {
                return "id";
            }

            var pathComponents = path.split("\\.");
            return pathComponents[pathComponents.length-1];
        }
    }
}
