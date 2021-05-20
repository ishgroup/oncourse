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
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.getFieldTypes
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.getFormById
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.getRuleById
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.toDbForm
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.toDbRule
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.toRestForm
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.toRestRule
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.validateForm
import static ish.oncourse.server.api.v1.function.DataCollectionFunctions.validateRule
import ish.oncourse.server.api.v1.model.DataCollectionFormDTO
import ish.oncourse.server.api.v1.model.DataCollectionRuleDTO
import static ish.oncourse.server.api.v1.model.DataCollectionTypeDTO.fromValue
import ish.oncourse.server.api.v1.model.FieldTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.DataCollectionApi
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.FieldConfigurationLink
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.FieldHeading
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.ServerErrorException
import javax.ws.rs.core.Response

class DataCollectionApiImpl implements DataCollectionApi {

    private static Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService

    void setCayenneService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    @Override
    List<FieldTypeDTO> getFieldTypes(String formType) {
        if (fromValue(formType)) {
            getFieldTypes(formType, cayenneService.newContext)
        } else {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'type', "Data collection type $formType is not exist")).build())
        }
    }

    @Override
    List<DataCollectionFormDTO> getForms() {
        ObjectSelect.query(FieldConfiguration)
                .prefetch(FieldConfiguration.FIELDS.joint())
                .prefetch(FieldConfiguration.FIELD_HEADINGS.joint())
                .prefetch(FieldConfiguration.FIELD_HEADINGS.dot(FieldHeading.FIELDS).joint())
                .select(cayenneService.newContext)
                .collect { toRestForm(it) }
    }


    @Override
    List<DataCollectionRuleDTO> getRules() {
        ObjectSelect.query(FieldConfigurationScheme)
                .prefetch(FieldConfigurationScheme.FIELD_CONFIGURATION_LINKS.joint())
                .prefetch(FieldConfigurationScheme.FIELD_CONFIGURATION_LINKS.dot(FieldConfigurationLink.FIELD_CONFIGURATION).joint())
                .select(cayenneService.newContext)
                .collect { toRestRule(it) }
    }

    @Override
    void createForm(DataCollectionFormDTO form) {
        ObjectContext context = cayenneService.newContext
        ValidationErrorDTO error = validateForm(context, form)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        toDbForm(context, form)
        context.commitChanges()
    }

    @Override
    void createRule(DataCollectionRuleDTO rule) {
        ObjectContext context = cayenneService.newContext
        validateRule(context, rule)
        toDbRule(context, rule)
        context.commitChanges()
    }

    @Override
    void removeForm(String id) {
        try {
            Long.valueOf(id)
        } catch (NumberFormatException e) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "Form is not exist")).build())
        }

        ObjectContext context = cayenneService.newContext

        FieldConfiguration dbForm = getFormById(context, id)

        if (!dbForm) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(id, null, "The data collection form $id is not exist")).build())
        }

        ValidationResult result = new ValidationResult()
        dbForm.validateForDelete(result)
        if (result.hasFailures()) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(dbForm?.id?.toString(), null, result.failures[0].description)).build())
        }

        if (dbForm.fieldConfigurationLink.empty) {
            context.deleteObjects(dbForm.fields)
            context.deleteObjects(dbForm.fieldHeadings)
            context.deleteObject(dbForm)
            context.commitChanges()
        } else {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(id, null, 'The data collection form can not be deleted, used for data collection rule')).build())
        }
    }

    @Override
    void removeRule(String id) {
        if (!StringUtils.isNumeric(id)) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'id', "The data collection rule id '$id' is incorrect. It must contain of only numbers")).build())
        }

        ObjectContext context = cayenneService.newContext
        FieldConfigurationScheme dbRule = getRuleById(context, id)

        if (!dbRule) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(id, null, "The data collection rule $id is not exist")).build())
        }

        if (dbRule.courses.empty) {
            context.deleteObjects(dbRule.fieldConfigurationLinks)
            context.deleteObject(dbRule)
            context.commitChanges()
        } else {
            String[] courses = dbRule.courses*.name.flatten().toArray()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(id, null, "The data collection form rule not be deleted, used for courses: ${courses.join(', ')}")).build())
        }
    }

    @Override
    void updateForm(String id, DataCollectionFormDTO form) {
        ObjectContext context = cayenneService.newContext
        FieldConfiguration dbForm = getFormById(context, id)

        if (!dbForm) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(id, null, "The data collection form $id is not exist")).build())
        }

        ValidationErrorDTO error = validateForm(context, form, dbForm)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        } else {
            try {
                cayenneService.serverRuntime.performInTransaction {
                    context.deleteObjects(dbForm.fields)
                    context.deleteObjects(dbForm.fieldHeadings)
                    context.commitChanges()
                    toDbForm(context, form, dbForm)
                    context.commitChanges()
                }
            } catch (Exception e) {
                logger.catching(e)
                context.rollbackChanges()
                throw new ServerErrorException('Unexpected error', Response.Status.INTERNAL_SERVER_ERROR)
            }
        }
    }

    @Override
    void updateRule(String id, DataCollectionRuleDTO rule) {
        ObjectContext context = cayenneService.newContext
        FieldConfigurationScheme dbRule = getRuleById(context, id)

        if (!dbRule) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(id, null, "The data collection rule $id is not exist")).build())
        }

        validateRule(context, rule)
        try {
            cayenneService.serverRuntime.performInTransaction {
                context.deleteObjects(dbRule.fieldConfigurationLinks)
                context.commitChanges()
                toDbRule(context, rule, dbRule)
                context.commitChanges()
            }
        } catch (Exception e) {
            logger.catching(e)
            context.rollbackChanges()
            throw new ServerErrorException('Unexpected error', Response.Status.INTERNAL_SERVER_ERROR)
        }
    }
}
