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
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.PaymentIn;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;
import java.util.Optional;

public class PaymentInReversedById implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public PaymentInReversedById(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return PaymentIn.class;
    }

    @Override
    public String getAttributeName() {
        return "reversedById";
    }

    @Override
    public SimpleNode spawnNode() {
        return new LazyExpressionNode() {
            @Override
            public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
                if (ctx.hasErrors()) {
                    return null;
                }
                ExpressionUtil.addChild(parent, PaymentIn.REVERSED_BY.outer().getExpression(), 0);
                ExpressionUtil.addChild(parent, args.get(2), 1);
                return parent;
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }
        };
    }

    @Override
    public Optional<Class<?>> getAttributeType() {
        return Optional.of(Integer.class);
    }
}
