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

package ish.oncourse.server.entity.mixins

import ish.common.types.ProductStatus
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.ProductItem
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory

class ArticleProductMixin {

    static int getNumber_sold(ArticleProduct self) {
        Expression activeArticlesExpression = ExpressionFactory.matchExp(ProductItem.STATUS.getName(), ProductStatus.ACTIVE)
        List<ProductItem> list = activeArticlesExpression.filterObjects(self.getProductItems())
        if (list == null) {
            return 0
        }
        return list.size()
    }
}
