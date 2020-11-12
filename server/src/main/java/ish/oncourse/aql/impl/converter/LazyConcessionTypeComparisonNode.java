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

import ish.oncourse.aql.impl.Op;
import ish.oncourse.server.cayenne.ConcessionType;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

public class LazyConcessionTypeComparisonNode extends LazyEntityComparisonNode {

    LazyConcessionTypeComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        Expression expression = null;
        if (this.jjtGetNumChildren() > 1) {
            var searchString = ((ASTScalar) this.jjtGetChild(1)).getValue().toString();
            if (searchString != null) {
                searchString = searchString.replace("%", "");
                expression = ConcessionType.NAME.startsWithIgnoreCase(searchString);
            }
        }
        return (SimpleNode)expression;
    }
}
