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
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.Op;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

public class LazySystemUserComparisonNode extends LazyEntityComparisonNode {

    LazySystemUserComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode(CompilationContext ctx) {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();

        var vaue = ((ASTScalar)this.jjtGetChild(1));
        if (vaue == null || vaue.getValue() == null) {
            if(!pathString.isEmpty()) {
                pathString += "+.";
            }

            var node = opToNode(getOp());
            ExpressionUtil.addChild(node, new ASTObjPath(pathString + "id"), 0);
            ExpressionUtil.addChild(node, new ASTScalar(null), 1);
            return node;

        } else if (SystemUserIdentifierConverter.ME.equals(vaue.getValue())) {
            if(!pathString.isEmpty()) {
                pathString += '.';
            }
            Long id = ctx.getSystemUserService().getCurrentUser().getId();
            return createComparisionNode(pathString + "id", id.toString());
        } else {
            return null;
        }
    }

    @Override
    protected SimpleNode createNode() {
        return null;
    }

}
