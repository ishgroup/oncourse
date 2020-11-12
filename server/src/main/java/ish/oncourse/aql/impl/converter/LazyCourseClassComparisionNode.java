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
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.CourseClass;
import org.apache.cayenne.exp.parser.*;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class LazyCourseClassComparisionNode extends LazyEntityComparisonNode {

    LazyCourseClassComparisionNode(Op op) {
       super(op);
    }


    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());

        var courseCodeNode = createComparisionNode(pathString +
                CourseClass.COURSE.dot(Course.CODE).getName(), value.getCourseCode());
        var classCodeNode = createComparisionNode(pathString +
                CourseClass.CODE.getName(), value.getClassCode());
        var courseNameNode = createComparisionNode(pathString +
                CourseClass.COURSE.dot(Course.NAME).getName(), value.getCourseName());
        var compoundCourseNameNode = createComparisionNode(pathString +
                    CourseClass.COURSE.dot(Course.NAME).getName(), value.getCompoundName());


        var idx = 0;
        if (value.isHasHyphen()) {
            var and = new ASTAnd();
            if (courseCodeNode != null) {
                ExpressionUtil.addChild(and, courseCodeNode, idx++);
            }
            if (classCodeNode != null) {
                ExpressionUtil.addChild(and, classCodeNode, idx);
            }
            if(compoundCourseNameNode != null) {
                return (SimpleNode) and.orExp(compoundCourseNameNode);
            }
            return and;
        } else {
            var or = new ASTOr();
            if (courseCodeNode != null) {
                ExpressionUtil.addChild(or, courseCodeNode, idx++);
            }
            if (classCodeNode != null) {
                ExpressionUtil.addChild(or, classCodeNode, idx++);
            }
            if (courseNameNode != null) {
                ExpressionUtil.addChild(or, courseNameNode, idx);
            }
            return or;
        }
    }

    private static class NameValue {

        private final Op op;
        private String courseCode;
        private String classCode;
        private String courseName;
        private String compoundName;

        private boolean hasHyphen;

        private NameValue(String nameString, Op op) {
            this.op = op;

            var str = nameString.replaceAll("%", "");

            var separator = str.indexOf('-');
            hasHyphen = separator >= 0;
            if (hasHyphen) {
                compoundName = trimToNull(str);
                courseCode = trimToNull(str.substring(0, separator));
                classCode = trimToNull(str.substring(separator + 1));
            } else {
                courseCode = trimToNull(str);
                classCode = trimToNull(str);
                courseName = trimToNull(str);
            }
        }

        private String getCourseCode() {
            if(op == Op.EQ || op == Op.NE) {
                return courseCode;
            } else {
                return courseCode == null ? null : "%" + courseCode + "%";
            }

        }

        private String getCourseName() {
            if(op == Op.EQ || op == Op.NE) {
                return courseName;
            } else {
                return courseName == null ? null : "%" + courseName + "%";
            }

        }
        private String getClassCode() {
            if(op == Op.EQ || op == Op.NE) {
                return classCode;
            } else {
                return classCode == null ? null : "%" + classCode + "%";
            }
        }

        private String getCompoundName() {
            if(op == Op.EQ || op == Op.NE) {
                return compoundName;
            } else {
                return compoundName == null ? null : "%" + compoundName + "%";
            }
        }

        boolean isHasHyphen() {
            return hasHyphen;
        }
    }
}
