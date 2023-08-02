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
import ish.oncourse.server.api.v1.model.AutomationConfigsDTO
import ish.oncourse.server.api.v1.model.ReportDTO
import ish.oncourse.server.api.v1.service.PdfTemplateApi
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.preference.UserPreferenceService
import ish.util.ImageHelper

import java.util.function.Function

class PdfTemplateApiImpl implements PdfTemplateApi {

    @Inject
    private ReportApiService apiService

    @Inject
    private UserPreferenceService userPreferenceService

    @Override
    void create(ReportDTO report) {
        apiService.create(report)
    }

    @Override
    List<ReportDTO> get(String entityName) {
        apiService.getAutomationFor(entityName, new Function<Report, ReportDTO>() {
            @Override
            ReportDTO apply(Report report) {
                apiService.toRestWithoutBodyAndPreviewModel(report, entityName)
            }
        })
    }

    @Override
    ReportDTO getById(Long id) {
        apiService.get(id)
    }

    @Override
    String getConfigs(Long id) {
        return apiService.getConfigs(id)
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
    void updateConfigs(Long id, AutomationConfigsDTO configs) {
        apiService.updateConfigs(id, configs.config)
    }

    @Override
    void updateInternal(ReportDTO report) {
        apiService.updateInternal(report)
    }

    @Override
    byte[] getPreview(Long id, Boolean compressed = false) {
        def preview = apiService.getPreview(id)
        if(!preview)
            return preview
        return compressed ? ImageHelper.generatePdfPreview(preview) : ImageHelper.generateHighQualityPdfPreview(
                preview,
                ImageHelper.getBackgroundQualityScale(userPreferenceService)
        )
    }

    @Override
    void deletePreview(Long id){
        apiService.deletePreview(id)
    }
}
