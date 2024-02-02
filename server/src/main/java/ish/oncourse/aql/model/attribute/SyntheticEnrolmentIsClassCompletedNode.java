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

import ish.common.types.AttendanceType;
import ish.common.types.EnrolmentStatus;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.glue._Enrolment;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**

 */
class SyntheticEnrolmentIsClassCompletedNode extends LazyExpressionNode {

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
        var index = path.indexOf("." +  "isClassCompleted");
        var prefix = index <= 0 ? "" : path.substring(0, index);

        Boolean value;
        if (args.get(2) instanceof ASTScalar &&  ((ASTScalar) args.get(2)).getValue() instanceof Boolean) {
            value = (Boolean) ((ASTScalar) args.get(2)).getValue();
        } else {
            throw new IllegalArgumentException("Invalid search expression, can not convert " + args.get(2) + " to Boolean value");
        }

        if (!value) {
            parent = (parent instanceof ASTEqual) ? new ASTNotIn() : new ASTIn();
        } else {
            parent = (parent instanceof ASTEqual) ? new ASTIn() : new ASTNotIn();
        }

        var enrolments = ObjectSelect.query(Enrolment.class)
                .where(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS).andExp(
                        Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).isNotNull()
                                .andExp(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).lt(new Date()))
                ))
                .select(ctx.getContext());

        Set<Long> enrolmentIds = enrolments.stream().filter(enrolment -> !enrolment.getCourseClass().getIsHybrid() ||
                enrolment.getCourseClass().getIsHybrid() && enrolment.getAttendances().stream()
                        .filter(attendance -> attendance.getAttendanceType().equals(AttendanceType.ATTENDED))
                        .count() >= enrolment.getCourseClass().getMinimumSessionsToComplete() )
                .map(_Enrolment::getId)
                .collect(Collectors.toSet());

        //List<Enrolment> foundEnrolments = enrolmentIds.stream().filter(enrolment -> !enrolment.getDisplayStatus().equals("Complete")).collect(Collectors.toList());
        ASTObjPath astObjPath = prefix.isBlank() ? new ASTObjPath("id") : new ASTObjPath(prefix + "." + "id");
        parent.jjtAddChild(astObjPath, 0);
        parent.jjtAddChild(new ASTList(enrolmentIds), 1);
        return parent;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
