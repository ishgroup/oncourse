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
import groovy.json.JsonSlurper
import ish.oncourse.server.api.service.ImportApiService
import ish.oncourse.server.api.v1.model.AutomationConfigsDTO
import ish.oncourse.server.api.v1.model.ExecuteImportRequestDTO
import ish.oncourse.server.api.v1.model.ImportModelDTO
import ish.oncourse.server.api.v1.service.ImportApi

class ImportApiImpl implements ImportApi {

    @Inject
    private ImportApiService service

    @Override
    void create(ImportModelDTO importDTO) {
        service.create(importDTO)
    }

    @Override
    String execute(String request, List<File> files) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        ExecuteImportRequestDTO requestDto = jsonSlurper.parseText(request) as ExecuteImportRequestDTO
        service.execute(requestDto, files)
    }

    @Override
    ImportModelDTO get(Long id) {
        service.get(id)
    }

    @Override
    String getConfigs(Long id) {
        return service.getConfigs(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, ImportModelDTO importDTO) {
        service.update(id, importDTO)
    }

    @Override
    void updateConfigs(Long id, AutomationConfigsDTO importConfigs) {
        service.updateConfigs(id, importConfigs.config)
    }

    @Override
    void updateInternal(ImportModelDTO importDTO) {
        service.updateInternal(importDTO)
    }
}
