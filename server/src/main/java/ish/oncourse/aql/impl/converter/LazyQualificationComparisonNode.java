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

import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.Op;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTOr;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

public class LazyQualificationComparisonNode extends LazyEntityComparisonNode {

    LazyQualificationComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());
        var title = createComparisionNode(pathString + "title", value.getSearchString());
        var nationalCode = createComparisionNode(pathString + "nationalCode", value.getSearchString());
        var or = new ASTOr();
        ExpressionUtil.addChild(or, title, 0);
        ExpressionUtil.addChild(or, nationalCode, 1);
        return or;
    }

    private static class NameValue {

        private final Op op;
        private String searchString;

        private NameValue(String nameString, Op op) {
            this.op = op;
            searchString = nameString.replaceAll("%", "");
        }

        private String getSearchString() {
            if ((op == Op.EQ) || (op == Op.NE)) {
                return searchString;
            }
            return searchString == null ? null : searchString + "%";
        }
    }
}
