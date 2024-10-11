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
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.service.MembershipProductApiService
import ish.oncourse.server.api.v1.function.EntityRelationFunctions
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.MembershipProductDTO
import ish.oncourse.server.api.v1.service.MembershipProductApi
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.Product

class MembershipProductApiImpl implements MembershipProductApi {

    @Inject
    private CayenneService cayenneService

    @Inject
    private MembershipProductApiService service


    @Override
    void create(MembershipProductDTO membershipProductDTO) {
        MembershipProduct dbModel = service.create(membershipProductDTO)
        EntityRelationFunctions.updateRelatedEntities(dbModel.context, dbModel.id, Product.simpleName, membershipProductDTO.relatedSellables)
    }

    @Override
    MembershipProductDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, MembershipProductDTO membershipProductDTO) {
        service.update(id, membershipProductDTO)
        EntityRelationFunctions.updateRelatedEntities(cayenneService.newContext, id, Product.simpleName, membershipProductDTO.relatedSellables)
    }
}
