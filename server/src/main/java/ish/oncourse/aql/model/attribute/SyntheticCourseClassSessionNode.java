/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.cayenne.CourseClass;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyntheticCourseClassSessionNode extends LazyExpressionNode {
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
        Set<Long> classIds = new HashSet<>();

        var value =  args.get(2) instanceof ASTScalar &&  ((ASTScalar) args.get(2)).getValue() instanceof  Integer ? (Integer) ((ASTScalar) args.get(2)).getValue() : -1 ;

        if (0 == value && (Expression.EQUAL_TO == parent.getType() || Expression.LESS_THAN_EQUAL_TO == parent.getType())) {
            classIds.addAll(classesWithoutSessions(ctx));
        } else if (0 == value && Expression.GREATER_THAN_EQUAL_TO == parent.getType()) {
            classIds.addAll(ObjectSelect.columnQuery(CourseClass.class, CourseClass.ID).select(ctx.getContext()));
        } else {
            var havingQualifier = convertParentExpression(parent, args);

            var classesIds = ObjectSelect.columnQuery(CourseClass.class, CourseClass.ID)
                    .where(CourseClass.SESSIONS.isNotNull())
                    .having(havingQualifier)
                    .select(ctx.getContext());

            classIds.addAll(classesIds);

        }

        var idProperty = prefix.isEmpty()
                ? CourseClass.ID
                : Property.create(prefix, Persistent.class).dot(CourseClass.ID);
        return (SimpleNode) idProperty.in(classIds);
    }

    protected SimpleNode convertParentExpression(SimpleNode parent, List<SimpleNode> args) {
        parent.jjtAddChild((SimpleNode) CourseClass.SESSIONS.count().getExpression(), 0);
        for (var i = 2; i < args.size(); i++) {
            parent.jjtAddChild(args.get(i), i - 1);
        }
        return parent;
    }

    private List<Long> classesWithoutSessions(CompilationContext ctx) {
        var classesWithSessions = ObjectSelect.columnQuery(CourseClass.class, CourseClass.ID)
                .where(CourseClass.SESSIONS.isNotNull())
                .select(ctx.getContext());

        return ObjectSelect.columnQuery(CourseClass.class, CourseClass.ID)
                .where(CourseClass.ID.nin(classesWithSessions))
                .select(ctx.getContext());
    }

    protected String getAttributeName() {
        return "sessionsCount";
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
