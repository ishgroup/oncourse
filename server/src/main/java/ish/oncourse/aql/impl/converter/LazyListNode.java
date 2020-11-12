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
import org.apache.cayenne.exp.parser.ASTList;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.List;

/**
 * Lazy Lists node that folds all children into array.
 *
 *          root                         root
 *            |                           |
 *     ___lazy_list___      --->    ASTList(Object[])
 *    |       |       |
 * scalar1  scalar2  scalar3
 *

 */
public class LazyListNode extends LazyExpressionNode {

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        return parent;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return createList();
    }

    private ASTList createList() {
        var list = new ASTList();
        var childrenCount = jjtGetNumChildren();
        var values = new Object[childrenCount];
        for(var i = 0; i<childrenCount; i++) {
            var child = jjtGetChild(i);
            if(child instanceof ASTScalar) {
                values[i] = ((ASTScalar) child).getValue();
            }
        }
        list.setOperand(0, values);
        return list;
    }
}
