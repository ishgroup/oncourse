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

package ish.oncourse.aql.model.attribute;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Enrolment;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**

 */
class SyntheticCourseClassPlacesLeftNode extends LazyExpressionNode {

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
        var index = path.indexOf("." +  "placesLeft");
        var prefix = index <= 0 ? "" : path.substring(0, index);

        var value =  args.get(2) instanceof ASTScalar &&  ((ASTScalar) args.get(2)).getValue() instanceof  Integer ? (Integer) ((ASTScalar) args.get(2)).getValue() : -1 ;

        Set<Long> classIds = new HashSet<>();
        if(value == 0 && parent instanceof ASTEqual ||
                parent instanceof ASTGreaterOrEqual && value <= 0 ||
                parent instanceof ASTGreater && value < 0 ||
                parent instanceof ASTLessOrEqual && value >= 0 ||
                parent instanceof ASTLess && value > 0) {
            classIds.addAll(ObjectSelect.columnQuery(CourseClass.class, CourseClass.ID)
                    .where(CourseClass.MAXIMUM_PLACES.isNull())
                    .select(ctx.getContext()));
        }


        var havingQualifier = convertParentExpression(parent, args);
        classIds.addAll(ObjectSelect.columnQuery(CourseClass.class, CourseClass.ID, CourseClass.MAXIMUM_PLACES)
                .where(CourseClass.MAXIMUM_PLACES.isNotNull()
                        .andExp(CourseClass.ENROLMENTS.outer().dot(Enrolment.STATUS).in(EnrolmentStatus.STATUSES_LEGIT)))
                .having(havingQualifier)
                .select(ctx.getContext()).stream().mapToLong(obj -> ((Long)obj[0]))
                .boxed()
                .collect(Collectors.toList()));

        var idProperty = prefix.isEmpty()
                ? CourseClass.ID
                : Property.create(prefix, Persistent.class).dot(CourseClass.ID);
        return (SimpleNode) idProperty.in(classIds);
    }


    private SimpleNode convertParentExpression(SimpleNode parent, List<SimpleNode> args) {
        var subtructNode = new ASTSubtract();
        subtructNode.jjtAddChild((SimpleNode) CourseClass.MAXIMUM_PLACES.getExpression(), 0);
        subtructNode.jjtAddChild((SimpleNode) CourseClass.ENROLMENTS.outer().count().getExpression(), 1);
        parent.jjtAddChild(subtructNode, 0);
        for (var i = 2; i < args.size(); i++) {
            parent.jjtAddChild(args.get(i), i - 1);
        }
        return parent;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
