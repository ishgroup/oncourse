/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import javax.inject.Inject
import ish.common.types.ProductStatus
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.query.ObjectSelect

trait MembershipProductTrait {

    @Inject
    private ICayenneService cayenneService

    /**
     * @return count of active product items
     */
    Long getTotalActiveMembershipsCount(){
        return ObjectSelect.query(ProductItem.class)
                .where(
                        ProductItem.PRODUCT.eq((Product)this)
                        .andExp(ProductItem.STATUS.eq(ProductStatus.ACTIVE))
                        .andExp(
                                ProductItem.EXPIRY_DATE.isNull()
                                        .orExp(ProductItem.EXPIRY_DATE.gt(new Date()))
                        )
                )
                .selectCount(cayenneService.newReadonlyContext)
    }
    /**
     * @return count of all product items
     */
    Long getSoldProductsCount(){
        return ObjectSelect.query(ProductItem.class)
                .where(ProductItem.PRODUCT.eq((Product)this))
                .selectCount(cayenneService.newReadonlyContext)
    }
}