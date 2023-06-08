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
import ish.oncourse.server.api.service.DiscountApiService
import ish.oncourse.server.api.v1.model.DiscountDTO
import ish.oncourse.server.api.v1.service.DiscountApi

class DiscountApiImpl implements DiscountApi {

    @Inject
    private DiscountApiService discountApiService

    @Override
    void create(DiscountDTO discount) {
        discountApiService.create(discount)
    }

    @Override
    DiscountDTO get(Long id) {
        discountApiService.get(id)
    }

    @Override
    void remove(Long id) {
        discountApiService.remove(id)
    }

    @Override
    void update(Long id, DiscountDTO discount) {
        discountApiService.update(id, discount)
    }
}
