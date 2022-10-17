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

package ish.oncourse.server.api.service;

import com.google.inject.Inject;
import ish.oncourse.server.api.dao.ReportDao;
import ish.oncourse.server.api.dao.ReportOverlayDao;
import ish.oncourse.server.api.v1.model.BindingDTO;
import ish.oncourse.server.api.v1.model.DataTypeDTO;
import ish.oncourse.server.api.v1.model.ReportDTO;
import ish.oncourse.server.cayenne.Report;
import ish.oncourse.server.report.ReportBuilder;
import ish.oncourse.server.upgrades.DataPopulationUtils;
import ish.print.PrintTransformationsFactory;
import ish.util.LocalDateUtils;
import org.apache.cayenne.query.ObjectSelect;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ReportApiService extends AutomationApiService<ReportDTO, Report, ReportDao> {

    @Inject
    ReportOverlayDao overlayDao;

    @Override
    public Class<Report> getPersistentClass() {
        return Report.class;
    }

    @Override
    public ReportDTO toRestModel(Report dbReport) {
        var dto = super.toRestModel(dbReport);

        if ( dbReport.getBackground() != null) {
            dto.setBackgroundId(dbReport.getBackground().getId());
        }
        dto.setSubreport(dbReport.getIsVisible());
        dto.setSortOn(dbReport.getSortOn());
        dto.setPreview(ish.util.ImageHelper.scaleImageToPreviewSize(dbReport.getPreview()));
        return dto;
    }

    private ReportDTO toRestModel(Report dbReport, String entityName) {
        var dto = toRestModel(dbReport);

        var transformation = PrintTransformationsFactory.getPrintTransformationFor(entityName, dbReport.getEntity(), dbReport.getKeyCode());
        if (transformation != null && !transformation.getFields().isEmpty()) {
            transformation.getFields().forEach( f -> {

                var bindingDTO = new BindingDTO();
                bindingDTO.setSystem(true);
                bindingDTO.setName(f.getFieldCode());
                bindingDTO.setLabel(f.getFieldDescription());

                var clazz = f.getFieldClass();

                if (Boolean.class == clazz) {
                    bindingDTO.setType(DataTypeDTO.CHECKBOX);
                    bindingDTO.setValueDefault(f.getDefaultValue() != null ? ((Boolean) f.getDefaultValue()).toString() : null);
                } else if (Date.class == clazz) {
                    bindingDTO.setType(DataTypeDTO.DATE_TIME);
                    bindingDTO.setValueDefault(f.getDefaultValue() != null ? LocalDateUtils.timeValueToString(LocalDateUtils.dateToTimeValue((Date) f.getDefaultValue())) : null);
                 } else if (LocalDate.class == clazz) {
                    bindingDTO.setType(DataTypeDTO.DATE);
                    bindingDTO.setValueDefault(f.getDefaultValue() != null ? LocalDateUtils.valueToString((LocalDate) f.getDefaultValue()) : null);
                } else {
                    throw new IllegalArgumentException("Unsupported report field: " + f.getFieldCode());
                }

                dto.getVariables().add(bindingDTO);
            });
        }

        return dto;
    }

    @Override
    public Report toCayenneModel(ReportDTO dto, Report cayenneModel) {
        cayenneModel = super.toCayenneModel(dto, cayenneModel);
        cayenneModel.setIsVisible(dto.isVisible());
        cayenneModel.setSortOn(dto.getSortOn());
        if (dto.getBackgroundId() != null) {
            cayenneModel.setBackground(overlayDao.getById(cayenneModel.getContext(), dto.getBackgroundId()));
        } else {
            cayenneModel.setBackground(null);
        }

        if (dto.getPreview() == null) {
            cayenneModel.setPreview(null);
        }
        return cayenneModel;
    }

    public List<ReportDTO> getAutomationFor(String entityName) {
        return entityDao.getForEntity(entityName, cayenneService.getNewContext()).stream().map(it -> toRestModel(it, entityName)).collect(Collectors.toList());
    }

    public Report updateInternal(ReportDTO dto) {
        var report = super.updateInternal(dto);
        if (dto.getPreview() == null) {
            report.setPreview(null);
        }
        if (dto.getBackgroundId() != null) {
            report.setBackground(overlayDao.getById(report.getContext(), dto.getBackgroundId()));
        } else {
            report.setBackground(null);
        }
        report.getContext().commitChanges();
        return report;
    }

    public byte[] getPreview(Long id){
        return ObjectSelect.columnQuery(Report.class, Report.PREVIEW).where(Report.ID.eq(id)).selectOne(cayenneService.getNewContext());
    }

    protected ReportDTO createDto() {
        return new ReportDTO();
    }

    @Override
    protected BiConsumer<Report, Map<String, Object>> getFillPropertiesFunction() {
        return DataPopulationUtils::fillReportWithCommonFields;
    }

    @Override
    protected Object getConfigs() {
        return null;
    }
}
