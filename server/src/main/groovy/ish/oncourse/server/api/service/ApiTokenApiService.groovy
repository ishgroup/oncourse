/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.ApiTokenDao
import ish.oncourse.server.api.dao.UserDao
import ish.oncourse.server.api.v1.model.ApiTokenDTO
import ish.oncourse.server.cayenne.ApiToken
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

class ApiTokenApiService extends EntityApiService<ApiTokenDTO, ApiToken, ApiTokenDao> {
    
    @Inject
    UserDao userDao
    
    @Override
    Class<ApiToken> getPersistentClass() {
        return ApiToken
    }

    @Override
    ApiTokenDTO toRestModel(ApiToken cayenneModel) {
        return new ApiTokenDTO().with {
            it.id = cayenneModel.id
            it.name = cayenneModel.name
            it.secret = '*******************'
            it.lastAccess = LocalDateUtils.dateToTimeValue(cayenneModel.lastAccess)
            it.userId = cayenneModel.systemUser.id

            it
        }
    }

    @Override
    ApiToken toCayenneModel(ApiTokenDTO dto, ApiToken cayenneModel) {
        cayenneModel.name = dto.name()
        cayenneModel.secret = dto.secret
        cayenneModel.systemUser = userDao.getById(cayenneModel.context, dto.userId)
        return cayenneModel
    }

    @Override
    void validateModelBeforeSave(ApiTokenDTO dto, ObjectContext context, Long id) {
        if (!dto.name) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        }
        if (!dto.secret) {
            validator.throwClientErrorException(id, 'secret', 'Secret is required.')
        }
        if (!dto.userId) {
            validator.throwClientErrorException(id, 'userId', 'System user is required.')
        }
    }

    @Override
    void validateModelBeforeRemove(ApiToken cayenneModel) {
        //no validation
    }
}
