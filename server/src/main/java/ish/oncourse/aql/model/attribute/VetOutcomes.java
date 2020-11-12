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
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.Outcome;
import ish.oncourse.server.cayenne.PaymentOut;
import ish.oncourse.server.cayenne.PaymentOutLine;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.Optional;

public class VetOutcomes implements SyntheticAttributeDescriptor {

    public static final String ATTRIBUTE_NAME = "vet";

    public VetOutcomes(EntityFactory factory) {
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Outcome.class;
    }

    @Override
    public String getAttributeName() {
        return ATTRIBUTE_NAME;
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
                    ctx.reportError(-1, -1, "Invalid arguments in VET property resolution.");
                }
                var arg2 = args.get(2);
                if (arg2 instanceof ASTScalar && ((ASTScalar)arg2).getValue() == null) {
                    ctx.reportError(-1, -1, "VET property need true/false value");
                }

                Expression fullExpression = null;
                Property<? extends Persistent> property = null;

                String path = ((ASTPath) args.get(1)).getPath();
                for (String part: path.split("\\.")) {
                    if (property == null) {
                        property = getNextProperty(part);
                    } else {
                        property = property.dot(getNextProperty(part));
                    }
                }
                if (property == null) {
                    property = Outcome.MODULE;
                }

                if (arg2 instanceof ASTScalar && ((ASTScalar)arg2).getValue() == Boolean.FALSE) {
                    if (Expression.EQUAL_TO == parent.getType()) {
                        fullExpression = property.isNull();
                    } else {
                        fullExpression = property.isNotNull();
                    }

                } else {
                    if (Expression.EQUAL_TO == parent.getType()) {
                        fullExpression = property.isNotNull();
                    } else {
                        fullExpression = property.isNull();
                    }
                }

                return (SimpleNode) fullExpression;
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) { return this; }

            private Property<? extends Persistent> getNextProperty(String value) {
                if (value.equals("vet")) {
                    return Outcome.MODULE;
                } else {
                    return Property.create(value, Persistent.class);
                }
            }
        };


//        return new SyntheticNodeTemplate(property.getExpression(), getAttributeName());
    }

    @Override
    public Optional<Class<?>> getAttributeType() {
        return Optional.of(Boolean.class);
    }

}
