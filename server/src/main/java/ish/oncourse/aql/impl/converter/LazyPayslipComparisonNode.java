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
import org.apache.cayenne.exp.parser.ASTAnd;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public class LazyPayslipComparisonNode extends LazyEntityComparisonNode {

    LazyPayslipComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());
        var firstNameNode = createComparisionNode(pathString + "contact.firstName", value.getFirstName());
        var lastNameNode = createComparisionNode(pathString + "contact.lastName", value.getLastName());

        if(firstNameNode != null && lastNameNode != null) {
            var and = new ASTAnd();
            ExpressionUtil.addChild(and, firstNameNode, 0);
            ExpressionUtil.addChild(and, lastNameNode, 1);
            return and;
        }

        if(firstNameNode != null) {
            return firstNameNode;
        }

        return lastNameNode;
    }

    private static class NameValue {

        private final Op op;
        private String firstName;
        private String lastName;

        private NameValue(String nameString, Op op) {
            this.op = op;

            /*
             * 'Jo Smi' ->
             *      first name like 'Jo%' and lastName like 'Smi%'
             * 'Smi, Jo' ->
             *      lastName like 'Smi%' and first name like 'Jo%'
             */
            nameString = nameString.replaceAll("%", "");

            if (nameString.trim().equals("")) {
                firstName = null;
                lastName = "";
            } else {
                var separator = nameString.indexOf(',');
                if (separator >= 0) {
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
    }
}
