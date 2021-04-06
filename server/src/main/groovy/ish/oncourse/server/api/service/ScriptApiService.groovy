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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.types.AuditAction
import ish.common.types.EntityEvent
import ish.common.types.SystemEventType
import ish.common.types.TriggerType
import ish.oncourse.aql.AqlService
import ish.oncourse.server.api.dao.ScriptDao
import static ish.oncourse.server.api.v1.function.ScriptFunctions.validateQueries
import ish.oncourse.server.api.v1.function.export.ExportFunctions
import ish.oncourse.server.api.v1.model.ExecuteScriptRequestDTO
import ish.oncourse.server.api.v1.model.OutputTypeDTO
import ish.oncourse.server.api.v1.model.ScheduleDTO
import ish.oncourse.server.api.v1.model.ScheduleTypeDTO
import ish.oncourse.server.api.v1.model.ScriptDTO
import ish.oncourse.server.api.v1.model.ScriptTriggerDTO
import static ish.oncourse.server.api.v1.model.TriggerTypeDTO.*
import ish.oncourse.server.cayenne.Audit
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.ScriptParameters
import ish.oncourse.server.scripting.validation.ScriptValidator
import ish.oncourse.server.users.SystemUserService
import ish.scripting.CronExpressionType
import ish.scripting.ScriptResult
import static ish.scripting.ScriptResult.ResultType.FAILURE
import ish.util.DateFormatter
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang.StringUtils.isBlank
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.ServerErrorException
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import java.util.concurrent.Callable

class ScriptApiService extends AutomationApiService<ScriptDTO, Script, ScriptDao> {

    private static final int LAST_RUN_FETCH_LIMIT = 7
    private static final Logger logger = LogManager.logger

    @Inject
    private GroovyScriptService groovyScriptService

    @Inject
    private SystemUserService systemUserService

    @Inject
    private ExecutorManager executorManager

    @Context
    private HttpServletResponse response

    @Inject
    private AqlService aql


    @Override
    Class<Script> getPersistentClass() {
        Script
    }

    @Override
    protected ScriptDTO createDto() {
        new ScriptDTO()
    }

    @Override
    ScriptDTO toRestModel(Script dbScript) {
        ScriptDTO scriptDTO = super.toRestModel(dbScript) as ScriptDTO
        scriptDTO.entity = dbScript.entity
        scriptDTO.outputType = OutputTypeDTO.values()[0].fromDbType(dbScript.outputType)
        scriptDTO.trigger = new ScriptTriggerDTO().with { st ->
            switch (dbScript.triggerType) {
                case TriggerType.CRON:
                    st.type = SCHEDULE
                    st.cron = new ScheduleDTO().with { schedule ->
                        switch (CronExpressionType.values().find { it.databaseValue == dbScript.cronSchedule }) {
                            case CronExpressionType.DAILY_EVENING:
                                schedule.scheduleType = ScheduleTypeDTO.DAILY_EVENING_ABOUT_8PM_
                                break
                            case CronExpressionType.DAILY_MORNING:
                                schedule.scheduleType = ScheduleTypeDTO.DAILY_MORNING_ABOUT_6AM_
                                break
                            case CronExpressionType.WEEKLY_MONDAY:
                                schedule.scheduleType = ScheduleTypeDTO.WEEKLY_MONDAY_ABOUT_7AM_
                                break
                            case CronExpressionType.HOURLY:
                                schedule.scheduleType = ScheduleTypeDTO.HOURLY
                                break
                            default:
                                schedule.scheduleType = ScheduleTypeDTO.CUSTOM
                                schedule.custom = dbScript.cronSchedule
                                break
                        }
                        schedule
                    }
                    break
                case TriggerType.ENTITY_EVENT:
                    st.entityName = dbScript.entityClass
                    st.entityAttribute = dbScript.entityAttribute
                    switch (dbScript.entityEventType) {
                        case EntityEvent.CREATE:
                            st.type = ON_CREATE
                            break
                        case EntityEvent.UPDATE:
                            st.type = ON_EDIT
                            break
                        case EntityEvent.CREATE_OR_UPDATE:
                            st.type = ON_CREATE_AND_EDIT
                            break
                        case EntityEvent.REMOVE:
                            st.type = ON_DELETE
                            break
                        default:
                            throw new ServerErrorException("Unexpected error: unknown entity event trigger type '$dbScript.entityEventType' in script '$dbScript.name'", Response.Status.INTERNAL_SERVER_ERROR)
                    }
                    break
                case TriggerType.ON_DEMAND:
                    st.type = ON_DEMAND
                    break
                case TriggerType.ONCOURSE_EVENT:
                    switch (dbScript.systemEventType) {
                        case SystemEventType.ENROLMENT_CANCELLED:
                            st.type = ENROLMENT_CANCELLED
                            break
                        case SystemEventType.ENROLMENT_SUCCESSFUL:
                            st.type = ENROLMENT_SUCCESSFUL
                            break
                        case SystemEventType.CLASS_CANCELLED:
                            st.type = CLASS_CANCELLED
                            break
                        case SystemEventType.CLASS_PUBLISHED:
                            st.type = CLASS_PUBLISHED_ON_WEB
                            break
                        case SystemEventType.PAYSLIP_APPROVED:
                            st.type = PAYSLIP_APPROVED
                            break
                        case SystemEventType.PAYSLIP_PAID:
                            st.type = PAYSLIP_PAID
                            break
                        default:
                            throw new ServerErrorException("Unexpected error: unknown system event trigger type '$dbScript.systemEventType' in script '$dbScript.name'", Response.Status.INTERNAL_SERVER_ERROR)
                    }
                    break
                default:
                    throw new ServerErrorException("Unexpected error: unknown trigger type '$dbScript.triggerType' in script '$dbScript.name'", Response.Status.INTERNAL_SERVER_ERROR)
            }
            st
        }

        List<String> lastRunList = ObjectSelect.columnQuery(Audit, Audit.CREATED)
                .where(Audit.ENTITY_IDENTIFIER.eq(dbScript.class.simpleName))
                .and(Audit.ENTITY_ID.eq(dbScript.id))
                .and(Audit.ACTION.in(AuditAction.SCRIPT_EXECUTED, AuditAction.SCRIPT_FAILED))
                .orderBy(Audit.CREATED.desc())
                .limit(LAST_RUN_FETCH_LIMIT)
                .select(cayenneService.newContext)
                .collect { DateFormatter.formatDateISO8601(it) }

        scriptDTO.lastRun = lastRunList
        scriptDTO
    }

    @Override
    Script toCayenneModel(ScriptDTO scriptDTO, Script script) {
        super.toCayenneModel(scriptDTO, script)
        script.outputType = scriptDTO.outputType?.dbType
        updateTrigger(scriptDTO, script)
        script
    }

    Script updateTrigger(ScriptDTO scriptDTO, Script dbScript) {
        if (scriptDTO.trigger.type in [ON_CREATE, ON_EDIT, ON_CREATE_AND_EDIT, ON_DELETE] && scriptDTO.trigger.entityName == null) {
            validator.throwClientErrorException('trigger', "Entity name required")
        }
        
        switch (scriptDTO.trigger.type) {
            case ON_CREATE:
                dbScript.triggerType = TriggerType.ENTITY_EVENT
                dbScript.entityEventType = EntityEvent.CREATE
                dbScript.entityClass = scriptDTO.trigger.entityName
                dbScript.entityAttribute = scriptDTO.trigger.entityAttribute
                break
            case ON_EDIT:
                dbScript.triggerType = TriggerType.ENTITY_EVENT
                dbScript.entityEventType = EntityEvent.UPDATE
                dbScript.entityClass = scriptDTO.trigger.entityName
                dbScript.entityAttribute = scriptDTO.trigger.entityAttribute
                break
            case ON_CREATE_AND_EDIT:
                dbScript.triggerType = TriggerType.ENTITY_EVENT
                dbScript.entityEventType = EntityEvent.CREATE_OR_UPDATE
                dbScript.entityClass = scriptDTO.trigger.entityName
                dbScript.entityAttribute = scriptDTO.trigger.entityAttribute
                break
            case ON_DELETE:
                dbScript.triggerType = TriggerType.ENTITY_EVENT
                dbScript.entityEventType = EntityEvent.REMOVE
                dbScript.entityClass = scriptDTO.trigger.entityName
                dbScript.entityAttribute = scriptDTO.trigger.entityAttribute
                break
            case ENROLMENT_CANCELLED:
                dbScript.triggerType = TriggerType.ONCOURSE_EVENT
                dbScript.systemEventType = SystemEventType.ENROLMENT_CANCELLED
                break
            case ENROLMENT_SUCCESSFUL:
                dbScript.triggerType = TriggerType.ONCOURSE_EVENT
                dbScript.systemEventType = SystemEventType.ENROLMENT_SUCCESSFUL
                break
            case CLASS_CANCELLED:
                dbScript.triggerType = TriggerType.ONCOURSE_EVENT
                dbScript.systemEventType = SystemEventType.CLASS_CANCELLED
                break
            case CLASS_PUBLISHED_ON_WEB:
                dbScript.triggerType = TriggerType.ONCOURSE_EVENT
                dbScript.systemEventType = SystemEventType.CLASS_PUBLISHED
                break
            case PAYSLIP_APPROVED:
                dbScript.triggerType = TriggerType.ONCOURSE_EVENT
                dbScript.systemEventType = SystemEventType.PAYSLIP_APPROVED
                break
            case PAYSLIP_PAID:
                dbScript.triggerType = TriggerType.ONCOURSE_EVENT
                dbScript.systemEventType = SystemEventType.PAYSLIP_PAID
                break
            case SCHEDULE:
                dbScript.triggerType = TriggerType.CRON
                switch (scriptDTO.trigger.cron.scheduleType) {
                    case ScheduleTypeDTO.WEEKLY_MONDAY_ABOUT_7AM_:
                        dbScript.cronSchedule = CronExpressionType.WEEKLY_MONDAY.databaseValue
                        break
                    case ScheduleTypeDTO.DAILY_MORNING_ABOUT_6AM_:
                        dbScript.cronSchedule = CronExpressionType.DAILY_MORNING.databaseValue
                        break
                    case ScheduleTypeDTO.DAILY_EVENING_ABOUT_8PM_:
                        dbScript.cronSchedule = CronExpressionType.DAILY_EVENING.databaseValue
                        break
                    case ScheduleTypeDTO.HOURLY:
                        dbScript.cronSchedule = CronExpressionType.HOURLY.databaseValue
                        break
                    case ScheduleTypeDTO.CUSTOM:
                        dbScript.cronSchedule = scriptDTO.trigger.cron.custom
                        break
                }
                break
            case ON_DEMAND:
                dbScript.triggerType = TriggerType.ON_DEMAND
                break
        }

        dbScript
    }

    @Override
    void validateModelBeforeSave(ScriptDTO dto, ObjectContext context, Long id) {
        super.validateModelBeforeSave(dto, context, id, false, true)
        ScriptValidator scriptValidator = new ScriptValidator(dto.content)
        validateQueries(cayenneService.newReadonlyContext, aqlService, dto)

        if (!scriptValidator.scriptHasCorrectDefinition()) {
            validator.throwClientErrorException('content', "Script body has invalid structure")
        }
        if (isBlank(dto.entity) && dto.trigger.type in [ON_CREATE, ON_EDIT, ON_CREATE_AND_EDIT, ON_DELETE]) {
            validator.throwClientErrorException('entity', "Script entity is required")
        }

        if (dto.trigger.type == SCHEDULE) {
            if ((!dto.trigger.cron) || (!dto.trigger.cron.scheduleType)) {
                validator.throwClientErrorException('trigger.cron.scheduleType', "ScheduleType should be specified for this trigger")
            }
            if (dto.trigger.cron.scheduleType == ScheduleTypeDTO.CUSTOM && (!dto.trigger.cron.custom)) {
                validator.throwClientErrorException('trigger.cron.custom', "Cron should be specified")
            }
        }

        if ((!dto.trigger) || (!dto.trigger.type)) {
            validator.throwClientErrorException('trigger', "Script trigger should be specified")
        }

        if ((entityDao as ScriptDao).hasTheSameName(context,dto.name, id)) {
            validator.throwClientErrorException('trigger', "Script name must be unique")
        }

    }

    @Override
    void validateModelBeforeRemove(Script script) {
        super.validateModelBeforeRemove(script)
    }

    String execute(ExecuteScriptRequestDTO request) {

        ObjectContext context = cayenneService.newContext
        Script script = getEntityAndValidateExistence(context, request.scriptId)
        Map<String, Object> variables = getVariablesMap(request.variables, script)

        if (script.entity) {
            List<CayenneDataObject> records = ExportFunctions.getSelectedRecords(script.entity, request.searchQuery.search, request.searchQuery.filter,request.searchQuery.tagGroups, [], aql, context)
            return executeScript(script, variables, records)
        } else {
            return executeScript(script, variables)

        }
    }

    ScriptResult getResult (String proccessId) {
        try {
            return executorManager.getResult(proccessId) as ScriptResult
        } catch (Exception e) {
            logger.catching(e)
            validator.throwClientErrorException("ScriptExecutionResultGetting", e.message)
        }
    }

    private String executeScript(Script script, Map<String, Object> variables, List<CayenneDataObject> records =[]) {
        logger.warn("Execute script id {}, user id: {}", script.id, systemUserService.currentUser?.id)

        ScriptParameters scriptParameters = ScriptParameters.empty()
        scriptParameters.add(GroovyScriptService.SYSTEM_USER, systemUserService.currentUser)
        variables.each { name, value ->
            scriptParameters.add(name, value)
        }

        return executorManager.submit(new Callable() {
            @Override
            ScriptResult call() throws Exception {
                ScriptResult result = null
                if (records.empty) {
                    result = groovyScriptService.runAndWait(script, scriptParameters)
                } else {
                    for (CayenneDataObject record : records) {
                        result = groovyScriptService.runAndWait(script, new ScriptParameters(scriptParameters).fillDefaultParameters(record))
                        if (result.type == FAILURE) {
                            return result
                        }
                    }
                }
                result.name = script.name
                result.resultOutputType = script.outputType
                return result
            }
        })
    }

    @Override
    Script updateInternal(ScriptDTO dto) {
        Script dbScript = super.updateInternal(dto) as Script
        updateTrigger(dto, dbScript)
        dbScript.getContext().commitChanges()
    }
}
