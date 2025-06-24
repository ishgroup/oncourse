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

import ish.common.types.CourseClassType;
import ish.common.types.EnrolmentStatus;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.api.v1.function.EnrolmentFunctions;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Enrolment;
import org.apache.cayenne.exp.parser.*;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;

import java.time.LocalDateTime;
import java.util.*;

/**
 *
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
        var index = path.indexOf("." + "isClassCompleted");
        var prefix = index <= 0 ? "" : path.substring(0, index);

        Boolean value;
        if (args.get(2) instanceof ASTScalar && ((ASTScalar) args.get(2)).getValue() instanceof Boolean) {
            value = (Boolean) ((ASTScalar) args.get(2)).getValue();
        } else {
            throw new IllegalArgumentException("Invalid search expression, can not convert " + args.get(2) + " to Boolean value");
        }

        var isClassCompletedNode = new ASTAnd();

        prefix = prefix.isEmpty() ? "" : prefix + ".";
        var statusCheckNode = new ASTEqual();
        statusCheckNode.jjtAddChild(new ASTObjPath(prefix + "status"), 0);
        statusCheckNode.jjtAddChild(new ASTScalar(EnrolmentStatus.SUCCESS.getDatabaseValue()), 1);


        var endDateNullCheck = new ASTNotEqual();
        endDateNullCheck.jjtAddChild(new ASTObjPath(prefix + "courseClass.endDateTime"), 0);
        endDateNullCheck.jjtAddChild(new ASTScalar(null), 1);

        var endDateCheck = new ASTLess();
        endDateCheck.jjtAddChild(new ASTObjPath(prefix + "courseClass.endDateTime"), 0);
        endDateCheck.jjtAddChild(new ASTScalar(LocalDateTime.now()), 1);

        var hybridMainCheck = buildHybridCheck(ctx, prefix);
        var selfPacedMainCheck = buildSelfPacedCheckNode(ctx, prefix);

        isClassCompletedNode.jjtAddChild(statusCheckNode, 0);

        var selfPacedOrCheck = new ASTOr();
        var notSelfPacedCheck = new ASTAnd();
        notSelfPacedCheck.jjtAddChild(notSelfPacedWithMaximumDaysCheck(prefix), 0);
        notSelfPacedCheck.jjtAddChild(endDateNullCheck, 1);
        notSelfPacedCheck.jjtAddChild(endDateCheck, 2);
        notSelfPacedCheck.jjtAddChild(hybridMainCheck, 3);

        selfPacedOrCheck.jjtAddChild(notSelfPacedCheck, 0);
        selfPacedOrCheck.jjtAddChild(selfPacedMainCheck, 1);

        isClassCompletedNode.jjtAddChild(selfPacedOrCheck, 1);

        parent.jjtAddChild(isClassCompletedNode, 0);
        parent.jjtAddChild(args.get(2), 1);
        return parent;
    }

    //!(courseclass.type = distant_learning && courseclass.maximumDays != null)
    private Node notSelfPacedWithMaximumDaysCheck(String prefix) {
        var notSelfPacedWithMaximumDaysCheck = new ASTNot();
        var notSelfPacedWithMaximumDays = new ASTAnd();

        var selfPacedTypeCheck = new ASTEqual();
        selfPacedTypeCheck.jjtAddChild(new ASTObjPath(prefix + "courseClass.type"), 0);
        selfPacedTypeCheck.jjtAddChild(new ASTScalar(CourseClassType.DISTANT_LEARNING.getDatabaseValue()), 1);

        var maximumDaysCheck = new ASTNotEqual();
        maximumDaysCheck.jjtAddChild(new ASTObjPath(prefix + "courseClass.maximumDays"), 0);
        maximumDaysCheck.jjtAddChild(new ASTScalar(null), 1);

        notSelfPacedWithMaximumDays.jjtAddChild(selfPacedTypeCheck, 0);
        notSelfPacedWithMaximumDays.jjtAddChild(maximumDaysCheck, 1);

        notSelfPacedWithMaximumDaysCheck.jjtAddChild(notSelfPacedWithMaximumDays, 0);
        return notSelfPacedWithMaximumDaysCheck;
    }


    // is not hybrid or class is completed with hybrid rules
    private Node buildHybridCheck(CompilationContext ctx, String prefix) {
        var hybridMainCheck = new ASTOr();
        var notHybridCheck = new ASTNotEqual();
        notHybridCheck.jjtAddChild(new ASTObjPath(prefix + "courseClass.type"), 0);
        notHybridCheck.jjtAddChild(new ASTScalar(CourseClassType.HYBRID.getDatabaseValue()), 1);

        long basicEnrolmentId = -1L;
        List<Enrolment> enrolments;
        Set<Long> enrolmentIds = new HashSet<>();

        do {
            enrolments = ObjectSelect.query(Enrolment.class)
                    .where(Enrolment.ID.gt(basicEnrolmentId).andExp(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS)).andExp(
                                    Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).isNotNull())
                            .andExp(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).lt(new Date()))
                            .andExp(Enrolment.COURSE_CLASS.dot(CourseClass.TYPE).eq(CourseClassType.HYBRID))
                    )
                    .limit(200)
                    .orderBy(Enrolment.ID.getName())
                    .select(ctx.getContext());

            enrolmentIds.addAll(EnrolmentFunctions.filterEnrolmentsWithCompletedClasses(enrolments));
            basicEnrolmentId = !enrolments.isEmpty() ? enrolments.get(enrolments.size() - 1).getId() : basicEnrolmentId;
        } while (!enrolments.isEmpty());

        Node hybridCheck = buildInNodeForList(enrolmentIds, prefix);

        hybridMainCheck.jjtAddChild(notHybridCheck, 0);
        hybridMainCheck.jjtAddChild(hybridCheck, 1);
        return hybridMainCheck;
    }


    // ids in (createdOn + courseClass.maximumDays < today)
    private Node buildSelfPacedCheckNode(CompilationContext ctx, String prefix) {
        var selfPacedIds = SQLSelect.scalarQuery(Long.class, "select e.id FROM Enrolment e join CourseClass cc ON e.courseClass_id = cc.id" +
                        " WHERE e.status = 3 and cc.type = 1 and cc.maximumDays is not null" +
                        " and DATE_ADD(e.createdOn, INTERVAL cc.maximumDays DAY) < CURRENT_DATE")
                .select(ctx.getContext());

        return buildInNodeForList(selfPacedIds, prefix);
    }

    private Node buildInNodeForList(Collection<Long> ids, String prefix) {
        Node result;
        if (!ids.isEmpty()) {
            result = new ASTIn();
            ASTObjPath astObjPath = new ASTObjPath(prefix + "." + "id");
            result.jjtAddChild(astObjPath, 0);
            result.jjtAddChild(new ASTList(ids), 1);
        } else {
            result = new ASTFalse();
        }

        return result;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }
}
