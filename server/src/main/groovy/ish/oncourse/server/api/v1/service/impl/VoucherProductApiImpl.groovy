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

import javax.inject.Inject
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.service.VoucherProductApiService
import ish.oncourse.server.api.v1.function.EntityRelationFunctions
import ish.oncourse.server.api.v1.model.VoucherProductDTO
import ish.oncourse.server.api.v1.service.VoucherProductApi
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.VoucherProduct

class VoucherProductApiImpl implements VoucherProductApi {

    @Inject
    private CayenneService cayenneService

    @Inject
    private VoucherProductApiService service

    @Override
    void create(VoucherProductDTO voucherProductDTO) {
        VoucherProduct dbModel = service.create(voucherProductDTO)
        EntityRelationFunctions.updateRelatedEntities(dbModel.context, dbModel.id, Product.simpleName, voucherProductDTO.relatedSellables)
    }

    @Override
    VoucherProductDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, VoucherProductDTO voucherProductDTO) {
        service.update(id, voucherProductDTO)
        EntityRelationFunctions.updateRelatedEntities(cayenneService.newContext, id, Product.simpleName, voucherProductDTO.relatedSellables)
    }
}
