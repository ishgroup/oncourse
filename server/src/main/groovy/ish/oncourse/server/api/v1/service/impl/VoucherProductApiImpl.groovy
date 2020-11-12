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
import ish.oncourse.server.api.service.VoucherProductApiService
import ish.oncourse.server.api.v1.model.VoucherProductDTO
import ish.oncourse.server.api.v1.service.VoucherProductApi

class VoucherProductApiImpl implements VoucherProductApi {

    @Inject
    private VoucherProductApiService service

    @Override
    void create(VoucherProductDTO voucherProduct) {
        service.create(voucherProduct)
    }

    @Override
    VoucherProductDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, VoucherProductDTO voucherProduct) {
        service.update(id, voucherProduct)
    }
}
