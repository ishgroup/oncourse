/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.ApiTokenDao
import ish.oncourse.server.api.service.ApiTokenApiService
import ish.oncourse.server.api.v1.model.ApiTokenDTO
import ish.oncourse.server.api.v1.service.TokenApi

class ApiTokenApiImpl implements TokenApi {
    
    @Inject
    ApiTokenApiService entityService
    @Inject
    CayenneService cayenneService
    @Inject
    ApiTokenDao entityDao
    
    @Override
    void create(List<ApiTokenDTO> tokens) {
        cayenneService.getServerRuntime().performInTransaction {
            tokens.each {entityService.create(it)}
        }
    }

    @Override
    void delete(Long tokenId) {
        entityService.remove(tokenId)
    }

    @Override
    List<ApiTokenDTO> get() {
        return entityDao.getList(cayenneService.newContext).collect {entityService.toRestModel(it)}
    }
}
