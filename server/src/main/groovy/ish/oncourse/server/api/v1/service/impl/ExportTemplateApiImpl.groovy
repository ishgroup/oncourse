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
import ish.oncourse.server.api.service.ExportTemplateApiService
import ish.oncourse.server.api.v1.model.AutomationConfigsDTO
import ish.oncourse.server.api.v1.model.ExportTemplateDTO
import ish.oncourse.server.api.v1.service.ExportTemplateApi
import ish.util.ThumbnailGenerator

class ExportTemplateApiImpl implements ExportTemplateApi {

    @Inject
    private ExportTemplateApiService service

    @Override
    void create(ExportTemplateDTO exportTemplate) {
        service.create(exportTemplate)
    }

    @Override
    ExportTemplateDTO get(Long id) {
        service.get(id)
    }

    @Override
    String getConfigs(Long id) {
        return service.getConfigs(id)
    }

    byte[] getHighQualityPreview(Long id) {
        def content = service.getPreview(id)
        return content
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    List<ExportTemplateDTO> templates(String entityName) {
        service.getAutomationFor(entityName).collect {dto ->
            if(dto.preview)
                dto.preview = ThumbnailGenerator.generateForImg(dto.preview, "image/png")
            dto
        }
    }

    @Override
    void update(Long id, ExportTemplateDTO exportTemplate) {
        service.update(id, exportTemplate)
    }

    @Override
    void updateConfigs(Long id, AutomationConfigsDTO exportConfigs) {
        service.updateConfigs(id, exportConfigs.config)
    }

    @Override
    void updateInternal(ExportTemplateDTO exportTemplate) {
        service.updateInternal(exportTemplate)
    }

    @Override
    byte[] exportOnDisk(Long id) {
        service.exportOnDisk(id)
    }

    @Override
    void deletePreview(Long id) {
        service.deletePreview(id)
    }
}
