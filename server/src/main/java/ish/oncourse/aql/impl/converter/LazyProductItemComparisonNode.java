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
import ish.oncourse.server.cayenne.Product;
import ish.oncourse.server.cayenne.ProductItem;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.apache.cayenne.exp.parser.ASTOr;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

public class LazyProductItemComparisonNode extends LazyEntityComparisonNode {

    LazyProductItemComparisonNode(Op op) {
        super(op);
    }

    @Override
    protected SimpleNode createNode() {
        var pathString = ((ASTObjPath)this.jjtGetChild(0)).getPath();
        if(!pathString.isEmpty()) {
            pathString += '.';
        }
        var value = new NameValue(((ASTScalar)this.jjtGetChild(1)).getValue().toString(), getOp());
        var productNameNode = createComparisionNode(pathString + ProductItem.PRODUCT.dot(Product.NAME).getName(), value.getProductName());
        var productSkuNode = createComparisionNode(pathString + ProductItem.PRODUCT.dot(Product.SKU).getName(), value.getProductName());

        var or = new ASTOr();

        ExpressionUtil.addChild(or, productNameNode, 0);
        ExpressionUtil.addChild(or, productSkuNode, 1);

        return or;
    }

    private static class NameValue {

        private final Op op;
        private String productName;
        private String productSku;

        private NameValue(String nameString, Op op) {
            this.op = op;
            productName = nameString.replaceAll("%", "");
            productSku = nameString.replaceAll("%", "");
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
    }
}
