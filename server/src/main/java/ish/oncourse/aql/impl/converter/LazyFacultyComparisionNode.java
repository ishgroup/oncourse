/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.Op;
import ish.oncourse.server.cayenne.Faculty;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTOr;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class LazyFacultyComparisionNode extends LazyEntityComparisonNode {

    public LazyFacultyComparisionNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());

        var name = createComparisionNode(pathString + Faculty.NAME.getName(), value.getSearchString());
        var code = createComparisionNode(pathString + Faculty.CODE.getName(), value.getSearchString());

        var idx = 0;
        var or = new ASTOr();

        if (name != null) {
            ExpressionUtil.addChild(or, name, idx++);
        }
        if (code != null) {
            ExpressionUtil.addChild(or, code, idx);
        }

        return or.jjtGetNumChildren() > 0 ? or : null;
    }

    private static class NameValue {

        private final Op op;
        private String searchString;

        private NameValue(String nameString, Op op) {
            this.op = op;
            searchString = trimToNull(nameString.replaceAll("%", ""));
        }

        private String getSearchString() {
            if ((op == Op.EQ) || (op == Op.NE)) {
                return searchString;
            }
            return searchString == null ? null : "%" + searchString + "%";
        }
    }
}
