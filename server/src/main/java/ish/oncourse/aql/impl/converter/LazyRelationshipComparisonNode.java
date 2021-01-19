/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.aql.impl.converter;

import java.util.List;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.impl.Op;
import org.apache.cayenne.exp.parser.ASTEqual;
import org.apache.cayenne.exp.parser.ASTNotEqual;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

public class LazyRelationshipComparisonNode extends LazyExpressionNode {

    private final Op op;

    public LazyRelationshipComparisonNode(Op op) {
        this.op = op;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        return this;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        SimpleNode node;
        switch (op) {
            case EQ:
                node = new ASTEqual();
                break;
            case NE:
                node = new ASTNotEqual();
                break;
            default:
                ctx.reportError(-1, -1, "Unexpected operator for the relationship: " + op);
                return this;
        }

        // make the whole path "outer"
        ASTObjPath path = (ASTObjPath)jjtGetChild(0);
        String outerPath = path.getPath() + "+";
        outerPath = outerPath.replace(".", "+.");
        outerPath = outerPath.replace("++", "+");

        ExpressionUtil.addChild(node, new ASTObjPath(outerPath), 0);
        ExpressionUtil.addChild(node, new ASTScalar(null), 1);

        return node;
    }
}
