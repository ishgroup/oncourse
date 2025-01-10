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

    private static final String START_WITH_NUMBER_REGEX = "^\\d+.*";
    private static final String START_WITH_PLUS_THEN_NUMBER_REGEX = "^\\+\\d+.*";
    private static final String ANY_CHARS_EXCEPT_NUMBERS = "[^\\d]";

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
        // '+.' - syntacsis to use LEFT JOIN instead of INNER JOIN by default. See: https://cayenne.apache.org/docs/3.0/qualifier-expressions.html
        var studentNumberNode = createComparisionNode(pathString + Contact.STUDENT.getName() + "+." + Student.STUDENT_NUMBER.getName(), value.getStudentNumber(), Op.EQ);
        var emailNode = createComparisionNode(pathString + Contact.EMAIL.getName(), value.getEmail());
        var homePhoneWithoutPlusNode = createComparisionNode(pathString + Contact.HOME_PHONE.getName(), value.getPhoneWithoutPlus());
        var mobilePhoneWithoutPlusNode = createComparisionNode(pathString + Contact.MOBILE_PHONE.getName(), value.getPhoneWithoutPlus());
        var workPhoneWithoutPlusNode = createComparisionNode(pathString + Contact.WORK_PHONE.getName(), value.getPhoneWithoutPlus());
        var homePhoneWithPlusNode = createComparisionNode(pathString + Contact.HOME_PHONE.getName(), value.getPhoneWithPlus());
        var mobilePhoneWithPlusNode = createComparisionNode(pathString + Contact.MOBILE_PHONE.getName(), value.getPhoneWithPlus());
        var workPhoneWithPlusNode = createComparisionNode(pathString + Contact.WORK_PHONE.getName(), value.getPhoneWithPlus());
        var isValidStudentNumber = tryParseLong(value.getStudentNumber());

        var idx = 0;
        var or = new ASTOr();
        if (isValidStudentNumber) {
            ExpressionUtil.addChild(or, studentNumberNode, idx++);
        }

        if (companyNameNode != null && !param.contains(",")) {
            ExpressionUtil.addChild(or, companyNameNode, idx++);
        }

        if (firstNameNode != null && lastNameNode != null && value.getMiddleName() != null) {
            var middleNameNode = createComparisionNode(pathString + Contact.MIDDLE_NAME.getName(), value.getMiddleName());
            var and = new ASTAnd();
            ExpressionUtil.addChild(and, firstNameNode, 0);
            ExpressionUtil.addChild(and, middleNameNode, 1);
            ExpressionUtil.addChild(and, lastNameNode, 2);
            ExpressionUtil.addChild(or, and, idx++);
        }
        else if (firstNameNode != null && lastNameNode != null) {
            var and = new ASTAnd();
            var subOr = new ASTOr();
            if (param.contains(",")) {
                var middleNameNode = createComparisionNode(pathString + Contact.MIDDLE_NAME.getName(), value.getFirstName());
                ExpressionUtil.addChild(subOr, firstNameNode, 0);
                ExpressionUtil.addChild(subOr, middleNameNode, 1);
                ExpressionUtil.addChild(and, subOr, 0);
                ExpressionUtil.addChild(and, lastNameNode, 1);
            } else {
                var middleNameNode = createComparisionNode(pathString + Contact.MIDDLE_NAME.getName(), value.getLastName());
                ExpressionUtil.addChild(subOr, lastNameNode, 0);
                ExpressionUtil.addChild(subOr, middleNameNode, 1);
                ExpressionUtil.addChild(and, subOr, 0);
                ExpressionUtil.addChild(and, firstNameNode, 1);
            }
            ExpressionUtil.addChild(or, and, idx++);
        } else if(firstNameNode != null) {
            ExpressionUtil.addChild(or, firstNameNode, idx++);
        } else if (lastNameNode != null) {
            ExpressionUtil.addChild(or, lastNameNode, idx++);
        }

        if (homePhoneWithPlusNode != null && mobilePhoneWithPlusNode != null && workPhoneWithPlusNode != null) {
            var subOrWithPlus = new ASTOr();
            ExpressionUtil.addChild(subOrWithPlus, homePhoneWithPlusNode, 0);
            ExpressionUtil.addChild(subOrWithPlus, mobilePhoneWithPlusNode, 1);
            ExpressionUtil.addChild(subOrWithPlus, workPhoneWithPlusNode, 2);
            ExpressionUtil.addChild(or, subOrWithPlus, idx++);
            var subOrWithoutPlus = new ASTOr();
            ExpressionUtil.addChild(subOrWithoutPlus, homePhoneWithoutPlusNode, 0);
            ExpressionUtil.addChild(subOrWithoutPlus, mobilePhoneWithoutPlusNode, 1);
            ExpressionUtil.addChild(subOrWithoutPlus, workPhoneWithoutPlusNode, 2);
            ExpressionUtil.addChild(or, subOrWithoutPlus, idx++);
        } else if (homePhoneWithoutPlusNode != null && mobilePhoneWithoutPlusNode != null && workPhoneWithoutPlusNode != null) {
            var subOr = new ASTOr();
            ExpressionUtil.addChild(subOr, homePhoneWithoutPlusNode, 0);
            ExpressionUtil.addChild(subOr, mobilePhoneWithoutPlusNode, 1);
            ExpressionUtil.addChild(subOr, workPhoneWithoutPlusNode, 2);
            ExpressionUtil.addChild(or, subOr, idx++);
        }

        if (emailNode != null) {
            ExpressionUtil.addChild(or, emailNode, idx++);
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
        private String middleName;
        private String lastName;
        private String studentNumber;
        private String companyName;
        private String email;
        private String phoneWithoutPlus;
        private String phoneWithPlus;

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
                lastName = trimToNull(nameString.substring(0, separator));
                String substring = nameString.substring(separator + 1);
                var substringSeparator = substring.indexOf(',');
                if (substringSeparator >= 0) {
                    firstName = trimToNull(substring.substring(0, substringSeparator));
                    middleName = trimToNull(substring.substring(substringSeparator + 1));
                    companyName = trimToNull(nameString.substring(0, separator));
                } else {
                    firstName = trimToNull(nameString.substring(separator + 1));
                    companyName = trimToNull(nameString.substring(0, separator));
                }
            } else {
                separator = nameString.indexOf(' ');
                if (separator >= 0) {
                    firstName = trimToNull(nameString.substring(0, separator));
                    String substring = nameString.substring(separator + 1);
//                    Remove spaces to correct parse with more than 1 space beetween firstName and MiddleName. E.g. "Flynn   Alexander Hill". Remove spaces to correct parse with more than 1 space after MiddleName/LastName. E.g. "Flynn Alexander  "
                    substring = substring.trim();
                    var substringSeparator = substring.indexOf(' ');
                    if (substringSeparator >= 0) {
                        middleName = trimToNull(substring.substring(0, substringSeparator));
                        lastName = trimToNull(substring.substring(substringSeparator + 1));
                    } else {
                        lastName = trimToNull(nameString.substring(separator + 1));
                    }
                } else {
                    firstName = null;
                    lastName = trimToNull(nameString);
                }
                companyName = trimToNull(nameString);
            }
            if (nameString.contains("@") && !nameString.contains(" ")) {
                email = trimToNull(nameString);
            }
            if (nameString.matches(START_WITH_NUMBER_REGEX)) {
                phoneWithoutPlus = nameString.replaceAll(ANY_CHARS_EXCEPT_NUMBERS, "");
            }
            if (nameString.matches(START_WITH_PLUS_THEN_NUMBER_REGEX)) {
                phoneWithPlus = nameString.replaceAll(ANY_CHARS_EXCEPT_NUMBERS, "");
                phoneWithoutPlus = nameString.replaceAll(ANY_CHARS_EXCEPT_NUMBERS, "");
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

        public String getMiddleName() {
            if(op == Op.EQ || op == Op.NE) {
                return middleName;
            }
            return middleName == null ? null : middleName + "%";
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

        public String getEmail() {
            return email == null ? null : email + "%";
        }

        public String getPhoneWithoutPlus() {
            return phoneWithoutPlus == null ? null : phoneWithoutPlus + "%";
        }

        public String getPhoneWithPlus() {
            return phoneWithPlus == null ? null : "+" + phoneWithPlus + "%";
        }
    }
}
