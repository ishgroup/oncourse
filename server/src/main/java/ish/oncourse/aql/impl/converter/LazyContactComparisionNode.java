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
import ish.oncourse.server.cayenne.Student;
import org.apache.cayenne.exp.parser.*;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Node that resolves Contact comparision
 *
 *     __Op__                ______and______
 *    /      \     -->      /               \
 *  path    arg          __op__         ____op____
 *                      /      \       /          \
 *                 path.name 'name' path.second  second
 *

 */
class LazyContactComparisionNode extends LazyEntityComparisonNode {

    LazyContactComparisionNode(Op op) {
        super(op);
    }

    public SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var param = ((ASTScalar) this.jjtGetChild(1)).getValue().toString();
        var value = new NameValue(param, getOp());
        var firstNameNode = createComparisionNode(pathString + Contact.FIRST_NAME.getName(), value.getFirstName());
        var lastNameNode = createComparisionNode(pathString + Contact.LAST_NAME.getName(), value.getLastName());
        var companyNameNode = createComparisionNode(pathString + Contact.LAST_NAME.getName(), value.getCompanyName());
        var studentNumberNode = createComparisionNode(pathString + Contact.STUDENT.dot(Student.STUDENT_NUMBER).getName(), value.getStudentNumber(), Op.EQ);
        var isValidStudentNumber = tryParseLong(value.getStudentNumber());

        var idx = 0;
        var or = new ASTOr();
        if (isValidStudentNumber) {
            ExpressionUtil.addChild(or, studentNumberNode, idx++);
        }

        if (companyNameNode != null && !param.contains(",")) {
            ExpressionUtil.addChild(or, companyNameNode, idx++);
        }

        if(firstNameNode != null && lastNameNode != null) {
            var and = new ASTAnd();
            ExpressionUtil.addChild(and, firstNameNode, 0);
            ExpressionUtil.addChild(and, lastNameNode, 1);
            ExpressionUtil.addChild(or, and, idx++);
        } else if(firstNameNode != null) {
            ExpressionUtil.addChild(or, firstNameNode, idx++);
        } else if (lastNameNode != null) {
            ExpressionUtil.addChild(or, lastNameNode, idx++);
        }

        if(idx == 1) {
            return (SimpleNode) or.jjtGetChild(0);
        }
        return idx > 0 ? or : null;
    }

    private boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static class NameValue {

        private final Op op;
        private String firstName;
        private String lastName;
        private String studentNumber;
        private String companyName;

        public NameValue(String nameString, Op op) {
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
                companyName = trimToNull(nameString.substring(0, separator));
            } else {
                separator = nameString.indexOf(' ');
                if (separator >= 0) {
                    firstName = trimToNull(nameString.substring(0, separator));
                    lastName = trimToNull(nameString.substring(separator + 1));
                } else {
                    firstName = null;
                    lastName = trimToNull(nameString);
                }
                companyName = trimToNull(nameString);
            }
        }

        public String getFirstName() {
            if(op == Op.EQ || op == Op.NE) {
                return firstName;
            }
            return firstName == null ? null : firstName + "%";
        }

        public String getLastName() {
            if(op == Op.EQ || op == Op.NE) {
                return lastName;
            }
            return lastName == null ? null : lastName + "%";
        }

        public String getStudentNumber() {
            return studentNumber;
        }

        public String getCompanyName() {
            if(companyName != null && companyName.equals(lastName)) {
                return null;
            }
            if(op == Op.EQ || op == Op.NE) {
                return companyName;
            }
            return companyName == null ? null : companyName + "%";
        }
    }
}
