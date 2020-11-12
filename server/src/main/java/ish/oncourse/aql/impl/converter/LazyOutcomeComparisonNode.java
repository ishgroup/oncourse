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
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.Outcome;
import ish.oncourse.server.cayenne.Student;
import org.apache.cayenne.exp.parser.ASTAnd;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTOr;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class LazyOutcomeComparisonNode extends LazyEntityComparisonNode {

    LazyOutcomeComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());
        var firstNameNode = createComparisionNode(
                pathString + Outcome.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.FIRST_NAME).getName(),
                value.getFirstName()
        );
        var lastNameNode = createComparisionNode(
                pathString + Outcome.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.LAST_NAME).getName(),
                value.getLastName()
        );
        var moduleNationalCodeNode = createComparisionNode(
                pathString + Outcome.MODULE.outer().dot(Module.NATIONAL_CODE).getName(),
                value.any
        );
        var moduleTitleNode = createComparisionNode(
                pathString + Outcome.MODULE.outer().dot(Module.TITLE).getName(),
                value.any
        );
        var courseName = createComparisionNode(
                pathString + Outcome.ENROLMENT.outer().dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.COURSE).dot(Course.NAME).getName(),
                value.any
        );
        var or = new ASTOr();
        var idx = 0;


        ExpressionUtil.addChild(or, moduleNationalCodeNode, idx++);
        ExpressionUtil.addChild(or, moduleTitleNode, idx++);
        ExpressionUtil.addChild(or, courseName, idx++);


        if(firstNameNode != null && lastNameNode != null) {
            var and = new ASTAnd();
            ExpressionUtil.addChild(and, firstNameNode, 0);
            ExpressionUtil.addChild(and, lastNameNode, 1);
            ExpressionUtil.addChild(or, and, idx++);
            return or;
        } else if(firstNameNode != null) {
            ExpressionUtil.addChild(or, firstNameNode, idx++);
            return or;
        } else if (lastNameNode != null) {
            ExpressionUtil.addChild(or, lastNameNode, idx++);
            return or;
        }
        return or.jjtGetNumChildren() > 0 ? or : null;
    }

    private static class NameValue {
        private final Op op;
        private String firstName;
        private String lastName;
        private String any;

        private NameValue(String nameString, Op op) {
            this.op = op;

            /*
             * 'Jo Smi' ->
             *      first name like 'Jo%' and lastName like 'Smi%'
             * 'Smi, Jo' ->
             *      lastName like 'Smi%' and first name like 'Jo%'
             */
            nameString = nameString.replaceAll("%", "");

            var separator = nameString.indexOf(',');
            if(separator >= 0) {
                firstName = trimToNull(nameString.substring(separator + 1));
                lastName = trimToNull(nameString.substring(0, separator));
                any = trimToNull(nameString.substring(0, separator));
            } else {
                separator = nameString.indexOf(' ');
                if (separator >= 0) {
                    firstName = trimToNull(nameString.substring(0, separator));
                    lastName = trimToNull(nameString.substring(separator + 1));
                } else {
                    firstName = null;
                    lastName = trimToNull(nameString);
                }
                any = trimToNull(nameString);
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

        private String getAny() {
            if(op == Op.EQ || op == Op.NE) {
                return any;
            }
            return any == null ? null : any + "%";
        }
    }
}
