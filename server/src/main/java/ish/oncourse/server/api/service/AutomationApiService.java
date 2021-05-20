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

import ish.oncourse.server.api.dao.AutomationDao;
import ish.oncourse.server.api.function.BindingFunctions;
import ish.oncourse.server.api.traits.AutomationDTOTrait;
import ish.oncourse.server.api.validation.EntityValidator;
import ish.oncourse.server.cayenne.AutomationTrait;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly;
import static ish.oncourse.server.api.v1.function.export.ExportFunctions.DEFAULT_TEMPLATE_PREFIX;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.http.util.TextUtils.isBlank;

abstract class AutomationApiService<T extends AutomationDTOTrait , K extends AutomationTrait, M extends AutomationDao<K>> extends EntityApiService<T,K,M> {


    protected abstract T createDto();

    @Override
    public T toRestModel(K cayenneModel)  {
        var automationDto =  createDto();

        automationDto.setId(cayenneModel.getId());
        automationDto.setName(cayenneModel.getName());
        automationDto.setKeyCode(cayenneModel.getKeyCode());
        automationDto.setEntity(cayenneModel.getEntity());
        automationDto.setBody(cayenneModel.getBody());
        automationDto.setEnabled(cayenneModel.getEnabled());
        automationDto.setCreatedOnDate(cayenneModel.getCreatedOn());
        automationDto.setModifiedOnDate(cayenneModel.getModifiedOn());
        automationDto.setDescription(cayenneModel.getDescription());
        BindingFunctions.populateAutomationBindings(automationDto, cayenneModel);

        return automationDto;
    }

    public K toCayenneModel(T dto, K cayenneModel) {
        cayenneModel.setName(trimToNull(dto.getName()));
        cayenneModel.setKeyCode(trimToNull(dto.getKeyCode()));
        cayenneModel.setEntity(trimToNull(dto.getEntity()));
        cayenneModel.setBody(trimToNull(dto.getBody()));
        cayenneModel.setEnabled(dto.isEnabled() == null ? false : dto.isEnabled());
        cayenneModel.setDescription(dto.getDescription());
        BindingFunctions.updateAutomationBindings(cayenneModel, dto);
        return cayenneModel;
    }

    @Override
    public void validateModelBeforeSave(T dto, ObjectContext context, Long id) {
        validateModelBeforeSave(dto, context, id, true, true);
    }

    public void validateModelBeforeSave(T dto, ObjectContext context, Long id, boolean isEntityMandatory, boolean isBodyMandatory) {
        K automation = null;

        if (id != null) {
            automation =  entityDao.getById(context, id);
        }

        if (automation != null && automation.getKeyCode() != null) {
            if (automation.getKeyCode().startsWith(DEFAULT_TEMPLATE_PREFIX)) {
                EntityValidator.throwClientErrorException(id, "id", "This is a internal template which cannot be edited.");
            } else if (!automation.getKeyCode().equals(dto.getKeyCode())) {
                EntityValidator.throwClientErrorException(id, "keyCode", "Key code is read only");
            }
        }

        if (isBlank(dto.getName())) {
            EntityValidator.throwClientErrorException(id, "name", "Name is required.");
        } else if (trimToNull(dto.getName()).length() > 100) {
            EntityValidator.throwClientErrorException(id, "name", "Name cannot be more than 100 chars.");
        }
        if (isBlank(dto.getKeyCode())) {
            EntityValidator.throwClientErrorException(id, "keyCode", "keyCode is required.");
        } else if (trimToNull(dto.getKeyCode()).length() > 80) {
            EntityValidator.throwClientErrorException(id, "keyCode", "keyCode cannot be more than 80 chars.");
        } else if (trimToNull(dto.getKeyCode()).startsWith(DEFAULT_TEMPLATE_PREFIX)) {
            EntityValidator.throwClientErrorException(id, "keyCode", "User template code must not start with " + DEFAULT_TEMPLATE_PREFIX);
        } else {
            AutomationTrait duplicate = entityDao.getByKeyCode(context, trimToNull(dto.getKeyCode()));
            if (duplicate != null && !duplicate.getId().equals(id)) {
                EntityValidator.throwClientErrorException(id, "keyCode", "Code must be unique.");
            }
        }
        if (isEntityMandatory) {
            if (isBlank(dto.getEntity())) {
                EntityValidator.throwClientErrorException(id, "entity", "Entity is required.");
            } else if (trimToNull(dto.getEntity()).length() > 40) {
                EntityValidator.throwClientErrorException(id, "entity", "Entity cannot be more than 40 chars.");
            }
        }
        if (isBodyMandatory) {
            if (isBlank(dto.getBody())) {
                EntityValidator.throwClientErrorException(id, "body", "Body is required.");
            }
        }
        BindingFunctions.validate(dto, validator);
    }

    @Override
    public void validateModelBeforeRemove(K cayenneModel) {
        if (cayenneModel.getKeyCode() != null && cayenneModel.getKeyCode().startsWith(DEFAULT_TEMPLATE_PREFIX)) {
            EntityValidator.throwClientErrorException(cayenneModel.getId(), "id", "Template provided by ish cannot be deleted.");
        }
    }

    public K updateInternal(T dto) {
        ObjectContext context = cayenneService.getNewContext();
        var entity = getEntityAndValidateExistence(context, dto.getId());
        entity.setEnabled(dto.isEnabled() == null ? false : dto.isEnabled());
        BindingFunctions.updateBuiltInOptions(entity, dto.getOptions());
        return entity;
    }

    public List<T> getAutomationFor(String entityName) {
        return entityDao.getForEntity(entityName, cayenneService.getNewContext()).stream().map(this::toRestModel).collect(Collectors.toList());
    }

    public Map<String, Object> getVariablesMap(Map<String, String> variables, K template) {
       return getVariablesMap(variables, template, false);
    }

    public Map<String, Object> getVariablesMap(Map<String, String> variables, K template, boolean html ) {

        Map<String, Object> newVariables = new HashMap<>();
        if (template.getVariables().size() != variables.size()) {
            EntityValidator.throwClientErrorException("variables", "Variables list is wrong");
        }

        template.getVariables().forEach(var -> {
                String value = variables.get(var.getName());
                Object objValue = var.parseValue(value);
                if (objValue != null || validateOnly.get()) {

                    if (html && objValue instanceof String){
                        String valueString  = (String) objValue;
                        valueString = StringEscapeUtils.escapeHtml(valueString).replaceAll("\\r|\\n", "<br/>");
                        newVariables.put(var.getName(), valueString);
                    } else {
                        newVariables.put(var.getName(), objValue);
                    }
                } else {
                    EntityValidator.throwClientErrorException(null, String.format("%s value is not valid for %s variable", value, var.getName()));
                }
        });

        return newVariables;
    }
}
