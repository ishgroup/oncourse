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
import ish.oncourse.server.cayenne.Product;
import ish.oncourse.server.cayenne.ProductItem;
import org.apache.cayenne.exp.parser.*;

public class LazyProductItemComparisonNode extends LazyEntityComparisonNode {

    LazyProductItemComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath) this.jjtGetChild(0)).getPath();
        if (!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar) this.jjtGetChild(1)).getValue().toString(), getOp());
        var contactNameValue = new LazyContactComparisionNode.NameValue(((ASTScalar) this.jjtGetChild(1)).getValue().toString(), getOp());

        var productNameNode = createComparisionNode(pathString + ProductItem.PRODUCT.dot(Product.NAME).getName(), value.getProductName());
        var productSkuNode = createComparisionNode(pathString + ProductItem.PRODUCT.dot(Product.SKU).getName(), value.getProductName());
        var voucherCodeNode = createComparisionNode(pathString + ProductItem.CODE.getName(), value.getCode());

        var contactPath = pathString + ProductItem.CONTACT.getName() + "+.";
        var firstNameNode = createComparisionNode(contactPath + Contact.FIRST_NAME.getName(), contactNameValue.getFirstName());
        var lastNameNode = createComparisionNode(contactPath + Contact.LAST_NAME.getName(), contactNameValue.getLastName());
        var middleNameNode = createComparisionNode(contactPath + Contact.MIDDLE_NAME.getName(), contactNameValue.getMiddleName());

        var or = new ASTOr();

        int idx = 0;
        ExpressionUtil.addChild(or, productNameNode, idx++);
        ExpressionUtil.addChild(or, productSkuNode, idx++);
        ExpressionUtil.addChild(or, voucherCodeNode, idx++);

        if(firstNameNode != null)
            ExpressionUtil.addChild(or, firstNameNode, idx++);
        if(lastNameNode != null)
            ExpressionUtil.addChild(or, lastNameNode, idx++);
        if(middleNameNode != null)
            ExpressionUtil.addChild(or, middleNameNode, idx);

        return or;
    }


    private static class NameValue {

        private final Op op;
        private String productName;
        private String productSku;
        private String voucherCode;

        private NameValue(String nameString, Op op) {
            this.op = op;
            productName = nameString.replaceAll("%", "");
            productSku = nameString.replaceAll("%", "");
            voucherCode = nameString.replaceAll("%", "");
        }

        private String getProductName() {
            if ((op == Op.EQ) || (op == Op.NE)) {
                return productName;
            }
            return productName == null ? null : productName + "%";
        }

        private String getProductSku() {
            if ((op == Op.EQ) || (op == Op.NE)) {
                return productSku;
            }
            return productSku == null ? null : productSku + "%";
        }

        public String getCode() {
            if (op == Op.EQ || op == Op.NE) {
                return voucherCode;
            }
            return voucherCode == null ? null : voucherCode + "%";
        }
    }
}
