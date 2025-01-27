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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.api.service.ProductItemApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.ProductItemCancelDTO
import ish.oncourse.server.api.v1.model.ProductItemDTO
import ish.oncourse.server.api.v1.service.ProductItemApi

class ProductItemApiImpl implements ProductItemApi {

    @Inject
    ProductItemApiService entityService



    @Override
    void cancel(ProductItemCancelDTO productItemCancel) {
        entityService.cancel(productItemCancel)
    }

    @Override
    ProductItemDTO get(Long id) {
        entityService.get(id)
    }

    @Override
    void update(Long id, ProductItemDTO productItem) {
        entityService.update(id, productItem)
    }
}
