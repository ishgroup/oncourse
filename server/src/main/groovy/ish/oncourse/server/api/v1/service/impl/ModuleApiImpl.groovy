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
import groovy.transform.CompileStatic
import ish.oncourse.server.api.service.ModuleApiService
import ish.oncourse.server.api.v1.model.ModuleDTO
import ish.oncourse.server.api.v1.service.ModuleApi

@CompileStatic
class ModuleApiImpl implements ModuleApi {

    @Inject
    private ModuleApiService moduleApiService

    @Override
    void create(ModuleDTO module) {
        moduleApiService.create(module)
    }

    @Override
    ModuleDTO get(Long id) {
        moduleApiService.get(id)
    }

    @Override
    void update(Long id, ModuleDTO module) {
        moduleApiService.update(id, module)
    }

    @Override
    void remove(Long id) {
        moduleApiService.remove(id)
    }
}
