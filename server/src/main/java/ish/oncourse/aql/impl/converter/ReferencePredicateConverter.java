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

import ish.oncourse.aql.impl.AqlParser;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.TypeClassifier;
import org.apache.cayenne.exp.parser.ASTEqual;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converts reference expression, i.e. single path without operators.
 * This predicate is valid only for boolean data type.
 * Allows to use shorthand syntax for boolean fields, e.g. "<b>not deleted</b> and name contains 'test'".
 *

 */
class ReferencePredicateConverter implements Converter<AqlParser.ReferencePredicateContext>{

    @Override
    public SimpleNode apply(AqlParser.ReferencePredicateContext reference, CompilationContext ctx) {
        var classifier = TypeClassifier.of(ctx.getCurrentPathJavaType());
        if(classifier != TypeClassifier.BOOLEAN) {
            ctx.reportError(reference.path().start.getLine(), reference.path().start.getCharPositionInLine(),
                    "Can't use non boolean attribute " + reference.path().getText() + " as predicate");
            return null;
        }

        var not = reference.NOT() != null;

        SimpleNode value = new ASTScalar(!not);
        SimpleNode equals = new ASTEqual();
        ExpressionUtil.addChild(equals, value, 1);

        return equals;
    }
}
