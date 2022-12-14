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

import groovy.transform.CompileStatic;
import groovy.transform.TypeCheckingMode;
import ish.oncourse.server.api.dao.ExportTemplateDao;
import ish.oncourse.server.api.function.BindingFunctions;
import ish.oncourse.server.api.v1.model.ExportTemplateDTO;
import ish.oncourse.server.api.v1.model.OutputTypeDTO;
import ish.oncourse.server.api.validation.EntityValidator;
import ish.oncourse.server.cayenne.ExportTemplate;
import org.apache.cayenne.ObjectContext;
import ish.oncourse.server.cayenne.Report;
import org.apache.cayenne.query.ObjectSelect;

public class ExportTemplateApiService extends AutomationApiService<ExportTemplateDTO, ExportTemplate, ExportTemplateDao> {

    @Override
    public Class<ExportTemplate> getPersistentClass() {
        return ExportTemplate.class;
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    public ExportTemplateDTO toRestModel(ExportTemplate exportTemplate) {
        var dto = super.toRestModel(exportTemplate);
        dto.setOutputType(OutputTypeDTO.values()[0].fromDbType(exportTemplate.getOutputType()));
        dto.setPreview(exportTemplate.getPreview());
        return dto;
    }

    @Override
    public ExportTemplate toCayenneModel(ExportTemplateDTO dto, ExportTemplate cayenneModel) {
        cayenneModel = super.toCayenneModel(dto, cayenneModel);
        cayenneModel.setOutputType(dto.getOutputType().getDbType());
        BindingFunctions.updateAutomationBindings(cayenneModel, dto);
        return cayenneModel;
    }

    @Override
    public void validateModelBeforeSave(ExportTemplateDTO exportTemplateDTO, ObjectContext context, Long id) {
        super.validateModelBeforeSave(exportTemplateDTO, context, id);
        if (exportTemplateDTO.getOutputType() == null) {
            EntityValidator.throwClientErrorException(id, "outputType", "Output type is required.");
        }
    }

    public ExportTemplate updateInternal(ExportTemplateDTO dto) {
        var exportTemplate = super.updateInternal(dto);
        exportTemplate.getContext().commitChanges();
        return exportTemplate;
    }


    public byte[] exportOnDisk(Long id) {
        return getEntityAndValidateExistence(cayenneService.getNewContext(), id).getScript().getBytes();
    }

    public byte[] getPreview(Long id){
        return ObjectSelect.columnQuery(ExportTemplate.class, Report.PREVIEW).where(Report.ID.eq(id)).selectOne(cayenneService.getNewContext());
    }

    protected ExportTemplateDTO createDto() {
        return new ExportTemplateDTO();
    }
}
