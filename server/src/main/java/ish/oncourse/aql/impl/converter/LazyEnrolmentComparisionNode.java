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
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.exp.parser.*;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class LazyEnrolmentComparisionNode extends LazyEntityComparisonNode {

    public LazyEnrolmentComparisionNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());
        var firstNameNode = createComparisionNode(pathString + Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).getName(), value.getFirstName());
        var lastNameNode = createComparisionNode(pathString + Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).getName(), value.getLastName());
        var studentNumberNode = createComparisionNode(pathString + Enrolment.STUDENT.dot(Student.STUDENT_NUMBER).getName(), value.getStudentNumber(), Op.EQ);
        var courseCodeNode = createComparisionNode(pathString + Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.CODE).getName(), value.getCourseCode());
        var classCodeNode = createComparisionNode(pathString + Enrolment.COURSE_CLASS.dot(CourseClass.CODE).getName(), value.getClassCode());
        var isValidStudentNumber = tryParseLong(value.getStudentNumber());

        var idx = 0;
        var or = new ASTOr();
        if (isValidStudentNumber) {
            ExpressionUtil.addChild(or, studentNumberNode, idx++);
        }

        if(firstNameNode != null && lastNameNode != null) {
            var and = new ASTAnd();
            ExpressionUtil.addChild(and, firstNameNode, 0);
            ExpressionUtil.addChild(and, lastNameNode, 1);
            ExpressionUtil.addChild(or, and, idx++);
        } else if(firstNameNode != null) {
            ExpressionUtil.addChild(or, firstNameNode, idx++);

        } else if (lastNameNode != null){
            ExpressionUtil.addChild(or, lastNameNode, idx++);
        }

        if (courseCodeNode != null && classCodeNode != null) {
            var and = new ASTAnd();
            ExpressionUtil.addChild(and, courseCodeNode, 0);
            ExpressionUtil.addChild(and, classCodeNode, 1);
            ExpressionUtil.addChild(or, and, idx);
        } else if(courseCodeNode != null) {
            ExpressionUtil.addChild(or, courseCodeNode, idx);
        } else if (classCodeNode != null) {
            ExpressionUtil.addChild(or, classCodeNode, idx);
        }

        return or.jjtGetNumChildren() > 0 ? or : null;
    }

    private boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class NameValue {

        private final Op op;
        private String firstName;
        private String lastName;
        private String studentNumber;
        private String courseCode;
        private String classCode;

        private NameValue(String nameString, Op op) {
            this.op = op;

            /*
             * 'Jo Smi' ->
             *      first name like 'Jo%' and lastName like 'Smi%'
             * 'Smi, Jo' ->
             *      lastName like 'Smi%' and first name like 'Jo%'
             */
            nameString = nameString.replaceAll("%", "");
            studentNumber = nameString;

            var separator = nameString.indexOf(',');
            if(separator >= 0) {
                firstName = trimToNull(nameString.substring(separator + 1));
                lastName = trimToNull(nameString.substring(0, separator));
            } else {
                separator = nameString.indexOf(' ');
                if (separator >= 0) {
                    firstName = trimToNull(nameString.substring(0, separator));
                    lastName = trimToNull(nameString.substring(separator + 1));
                } else {
                    firstName = null;
                    lastName = trimToNull(nameString);
                }
            }

            separator = nameString.indexOf('-');
            if (separator >= 0) {
                courseCode = trimToNull(nameString.substring(0, separator));
                classCode = trimToNull(nameString.substring(separator + 1));
            } else {
                courseCode = null;
                classCode = trimToNull(nameString);
            }

        }

        private String getFirstName() {
            if(op == Op.EQ || op == Op.NE) {
                return firstName;
            }
            return firstName == null ? null : firstName + "%";
        }

        private String getLastName() {
            if(op == Op.EQ || op == Op.NE) {
                return lastName;
            }
            return lastName == null ? null : lastName + "%";
        }

        private String getStudentNumber() {
            return studentNumber;
        }

        private String getCourseCode() {
            return courseCode;
        }

        private String getClassCode() {
            return classCode;
        }
    }
}
