/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute;

import ish.common.types.CourseClassType;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import org.apache.cayenne.exp.parser.*;

import java.util.List;

public class SyntheticCourseClassIsDistantLearningCourseNode extends LazyExpressionNode {
    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (ctx.hasErrors()) {
            return null;
        }

        // arg 0 - this
        // arg 1 - original path
        // arg 2..N - original args for parent expression
        if (args.size() < 3) {
            return null;
        }

        if (!(args.get(1) instanceof ASTObjPath)) {
            return null;
        }

        var pathNode = (ASTObjPath) args.get(1);
        var path = pathNode.getPath();
        var index = path.indexOf("." +  getAttributeName());
        var prefix = index <= 0 ? "" : path.substring(0, index);

        Boolean value;
        if (args.get(2) instanceof ASTScalar &&  ((ASTScalar) args.get(2)).getValue() instanceof  Boolean) {
            value = (Boolean) ((ASTScalar) args.get(2)).getValue();
        } else {
            throw new IllegalArgumentException("Invalid search expression, can not convert " + args.get(2) + " to Boolean value");
        }

        if (value == false) {
            parent = (parent instanceof ASTEqual) ? new ASTNotEqual() : new ASTEqual();
        }
        var counter = 0;
        ASTObjPath astObjPath = prefix.isBlank() ? new ASTObjPath("type") : new ASTObjPath(prefix + "." + "type");
        ExpressionUtil.addChild(parent, astObjPath , counter++);
        ASTScalar distant_learning = new ASTScalar(CourseClassType.DISTANT_LEARNING.getDatabaseValue());
        ExpressionUtil.addChild(parent, distant_learning, counter++);

        return parent;
    }

    protected String getAttributeName() {
        return "isDistantLearningCourse";
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
