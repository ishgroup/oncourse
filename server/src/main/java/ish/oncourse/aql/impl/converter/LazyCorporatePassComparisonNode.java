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
import ish.oncourse.aql.impl.converter.LazyContactComparisionNode.NameValue;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.CorporatePass;
import org.apache.cayenne.exp.parser.*;

public class LazyCorporatePassComparisonNode extends LazyEntityComparisonNode {

    LazyCorporatePassComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }

        var param = ((ASTScalar) this.jjtGetChild(1)).getValue().toString();
        var value = new NameValue(param, getOp());
        var firstNameNode = createComparisionNode(pathString + CorporatePass.CONTACT.dot(Contact.FIRST_NAME).getName(), value.getFirstName());
        var lastNameNode = createComparisionNode(pathString + CorporatePass.CONTACT.dot(Contact.LAST_NAME).getName(), value.getLastName());
        var companyNameNode = createComparisionNode(pathString + CorporatePass.CONTACT.dot(Contact.LAST_NAME).getName(), value.getCompanyName());

        var idx = 0;
        var or = new ASTOr();

        if (companyNameNode != null && !param.contains(",")) {
            ExpressionUtil.addChild(or, companyNameNode, idx++);
        }

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
}
