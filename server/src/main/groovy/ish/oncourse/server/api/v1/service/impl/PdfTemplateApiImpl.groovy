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
import ish.oncourse.server.api.service.ReportApiService
import ish.oncourse.server.api.v1.model.ReportDTO
import ish.oncourse.server.api.v1.service.PdfTemplateApi
import ish.util.ImageHelper

class PdfTemplateApiImpl implements PdfTemplateApi {

    @Inject
    private ReportApiService apiService

    @Override
    void create(ReportDTO report) {
        apiService.create(report)
    }

    @Override
    List<ReportDTO> get(String entityName) {
        apiService.getAutomationFor(entityName)
    }

    @Override
    ReportDTO getById(Long id) {
        apiService.get(id)
    }

    @Override
    void remove(Long id) {
        apiService.remove(id)
    }

    @Override
    void update(Long id, ReportDTO report) {
        apiService.update(id, report)
    }

    @Override
    void updateInternal(ReportDTO report) {
        apiService.updateInternal(report)
    }

    @Override
    byte[] getHighQualityPreview(Long id) {
        return ImageHelper.generateQualityPreview(apiService.getPreview(id), 1, true)
    }
}
