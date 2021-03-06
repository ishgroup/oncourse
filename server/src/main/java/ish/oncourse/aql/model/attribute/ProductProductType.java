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

import ish.common.types.ProductType;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Product;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class ProductProductType implements SyntheticAttributeDescriptor {

    public ProductProductType(EntityFactory factory) {
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Product.class;
    }

    @Override
    public String getAttributeName() {
        return "type";
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
                    ctx.reportError(-1, -1, "Invalid arguments for property resolution.");
                }

                Property<? extends Serializable> property = null;

                String path = ((ASTPath) args.get(1)).getPath();
                for (String part : path.split("\\.")) {
                    if (property == null) {
                        property = getNextProperty(part);
                    } else {
                        property = property.dot(getNextProperty(part));
                    }
                }
                if (property == null) {
                    property = Product.TYPE;
                }

                ASTScalar arg2 = (ASTScalar) args.get(2);
                ProductType productType = (ProductType) arg2.getValue();
                ASTScalar value = new ASTScalar(productType.getDatabaseValue());

                ExpressionUtil.addChild(parent, property.getExpression(), 0);
                ExpressionUtil.addChild(parent, value, 1);

                return parent;
            }

            @Override
            public SimpleNode resolveSelf(CompilationContext ctx) {
                return this;
            }

            private Property<? extends Serializable> getNextProperty(String value) {
                if (value.equals("type")) {
                    return Product.TYPE;
                }
                return Property.create(value, Persistent.class);
            }
        };
    }

    @Override
    public Optional<Class<?>> getAttributeType() {
        return Optional.of(ProductType.class);
    }
}
