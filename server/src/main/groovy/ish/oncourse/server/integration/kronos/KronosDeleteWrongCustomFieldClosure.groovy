/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.kronos

import groovy.transform.CompileStatic
import groovyx.net.http.HttpResponseDecorator
import ish.oncourse.API
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Delete TutorAttendance Custom Fields for deleted Kronos Schedules.
 *
 * ```
 * kronosDeleteWrongCustomFields {
 * }
 * ```
 *
 * Get all TutorAttendances with CustomFields 'shiftIdCustomField' and 'scheduleIdCustomField' then check if one of its is null
 * or do a GET request to understand if Schedule is exist.
 *
 */
@API
@CompileStatic
@ScriptClosure(key = "kronosDeleteWrongCustomFields", integration = KronosIntegration)
class KronosDeleteWrongCustomFieldClosure implements ScriptClosureTrait<KronosIntegration> {

    private static Logger logger = LogManager.logger

    @Override
    Object execute(KronosIntegration integration) {
        integration.initAuthHeader()

        DataContext context = integration.cayenneService.newContext
        List<TutorAttendance> tutorAttendancesWithCustomFields = ObjectSelect.query(TutorAttendance)
                .where(TutorAttendance.CUSTOM_FIELDS.dot(CustomField.CUSTOM_FIELD_TYPE).dot(CustomFieldType.NAME)
                        .in(integration.KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY, integration.KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY))
                .select(context)
        
        for (TutorAttendance tutorAttendance : tutorAttendancesWithCustomFields) {
            def shiftIdCustomField = tutorAttendance.getCustomFieldValue(integration.KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY)
            def scheduleIdCustomField = tutorAttendance.getCustomFieldValue(integration.KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY)
            // remove custom fields if one of its is null (shiftIdCustomField == null || scheduleIdCustomField == null)
            if (!(shiftIdCustomField && scheduleIdCustomField)) {
                context.deleteObjects(tutorAttendance.customFields.findAll {it.customFieldType.name == integration.KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY || it.customFieldType.name == integration.KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY})
                context.commitChanges()
                logger.warn("Custom field with Kronos Shift id '${shiftIdCustomField}' and custom field with Kronos Schedule id '${scheduleIdCustomField}' are removed for TutorAttendance with id '${tutorAttendance.id}'.")
                continue
            }
            def resultShift = integration.getShift(shiftIdCustomField, scheduleIdCustomField)
            def response = resultShift["response"] as HttpResponseDecorator
            // remove custom fields if Schedule was deleted from Kronos
            if (!response.isSuccess() && response.status == 404){
                context.deleteObjects(tutorAttendance.customFields.findAll {it.customFieldType.name == integration.KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY || it.customFieldType.name == integration.KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY})
                context.commitChanges()
                logger.warn("Custom field with Kronos Shift id '${shiftIdCustomField}' and custom field with Kronos Schedule id '${scheduleIdCustomField}' are removed for TutorAttendance with id '${tutorAttendance.id}' because Kronos Schedule was deleted from Kronos.")
            }
        }
        return null
    }
}
