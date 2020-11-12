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

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.impl.Op;
import ish.oncourse.aql.impl.TypeClassifier;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;

/**
 * Lazy string scalar that is resolved into proper SQL search expression depending on parent operator.
 *

 */
class LazyStringScalar extends LazyExpressionNode {

    private final String value;
    private final Op op;

    LazyStringScalar(String value, Op op) {
        this.value = value;
        this.op = op;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        return parent;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return wrapScalar(ctx);
    }

    @SuppressWarnings("unchecked")
    private ASTScalar wrapScalar(CompilationContext ctx) {
        var type = TypeClassifier.of(ctx.getCurrentPathJavaType());

        switch (type) {
            case BOOLEAN:
                return new ASTScalar(Boolean.valueOf(value));
            case NUMERIC:
                try {
                    return value.contains(".") ?
                            new ASTScalar(Double.valueOf(value)) :
                            new ASTScalar(Long.valueOf(value));
                } catch (NumberFormatException ex) {
                    ctx.reportError(-1, -1, "Can't convert string '" + value + "' to number");
                    return null;
                }
            case ENUM:
                var result = EnumIdentifierConverter
                        .getValueForEnumClass((Class<Enum<?>>)ctx.getCurrentPathJavaType(), value);
                if(result != null) {
                    return result;
                }
                ctx.reportError(-1, -1, "Can't convert string '" + value + "' to enum "
                        + ctx.getCurrentPathJavaType().getSimpleName());
                return null;
            case DATE:
                ctx.reportError(-1, -1, "Can't compare field of type " + type + " with string");
                return null;
        }


        String expression;
        switch (op) {
            case LIKE:
            case NOT_LIKE:
            case CONTAINS:
            case NOT_CONTAINS:
                expression = '%' + value + '%';
                break;

            case STARTS_WITH:
            case NOT_STARTS_WITH:
                expression = value + '%';
                break;

            case ENDS_WITH:
            case NOT_ENDS_WITH:
                expression = '%' + value;
                break;

            default:
                expression = value;
        }

        return new ASTScalar(expression);
    }
}
