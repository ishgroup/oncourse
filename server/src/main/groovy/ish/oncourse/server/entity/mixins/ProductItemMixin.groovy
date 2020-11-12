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

import static ish.common.types.ProductStatus.*
import ish.common.types.ProductType
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.ProductItem
import static ish.persistence.CommonExpressionFactory.previousMidnight
import org.apache.commons.lang3.StringUtils

class ProductItemMixin {

    static String getTypeString(ProductItem self) {
        for (ProductType productType : ProductType.values()) {
            if (self.getType().equals(productType.getDatabaseValue())) {
                return productType.getDisplayName()
            }
        }
        return StringUtils.EMPTY
    }
}
