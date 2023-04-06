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
import org.apache.cayenne.exp.parser.*;

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
        SimpleNode node = getRoot();
        if(node == null){
            ctx.reportError(-1, -1, "Unexpected operator for the relationship: " + op);
            return this;
        }

        // make the whole path "outer"
        ASTObjPath path = children == null ? new ASTObjPath("") : (ASTObjPath) jjtGetChild(0);
        initRootFromPath(node, path);

        return node;
    }

    public SimpleNode resolveSelf(ASTObjPath path, CompilationContext ctx) {
        SimpleNode node = getRoot();
        if(node == null){
            ctx.reportError(-1, -1, "Unexpected operator for the relationship: " + op);
            return this;
        }

        // make the whole path "outer"
        initRootFromPath(node, path);
        return node;
    }

    private SimpleNode getRoot(){
        switch (op) {
            case EQ:
                return new ASTEqual();
            case NE:
                return new ASTNotEqual();
            default:
                return null;
        }
    }

    private void initRootFromPath(SimpleNode node, ASTObjPath path){
        String outerPath = path.getPath() + "+";
        outerPath = outerPath.replace(".", "+.");
        outerPath = outerPath.replace("++", "+");

        var outerPathNode = new ASTObjPath(outerPath);
        outerPathNode.setPathAliases(path.getPathAliases());

        ExpressionUtil.addChild(node, outerPathNode, 0);
        ExpressionUtil.addChild(node, new ASTScalar(null), 1);
    }
}
