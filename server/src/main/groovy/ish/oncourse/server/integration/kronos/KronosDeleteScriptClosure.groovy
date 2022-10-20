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
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Delete TutorAttendances as Shift from Kronos automatically as it is deleted in onCourse.
 *
 * ```
 * kronos {
 *     session record
 * }
 * ```
 *
 * Get Schedule Id and Shift Id from TutorAttendance custom fields then call DELETE request with these ids,
 * that delete Shift from Kronos
 *
 */
@API
@CompileStatic
@ScriptClosure(key = "kronosDelete", integration = KronosIntegration)
class KronosDeleteScriptClosure implements ScriptClosureTrait<KronosIntegration> {

    private static Logger logger = LogManager.logger

    TutorAttendance tutorAttendance

    def tutorAttendance(TutorAttendance tutorAttendance) {
        this.tutorAttendance = tutorAttendance
    }

    @Override
    Object execute(KronosIntegration integration) {
        integration.initAuthHeader()
        Session session = tutorAttendance.session

        def shiftIdByCustomField = integration.getCustomFieldShiftId(tutorAttendance)
        def scheduleIdByCustomField = integration.getCustomFieldScheduleId(tutorAttendance)
        if (!shiftIdByCustomField) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have 'kronosShiftId' custom field. Can't delete TutorAttendance from Kronos.")
        }
        if (!scheduleIdByCustomField) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have 'kronosScheduleId' custom field. Can't delete TutorAttendance from Kronos.")
        }

        def shiftDeleted = integration.deleteShift(shiftIdByCustomField, scheduleIdByCustomField)
        def response = shiftDeleted["response"] as HttpResponseDecorator
        if (response.isSuccess()) {
            logger.warn("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' was successfully deleted from Kronos, Kronos Shift with id '${shiftIdByCustomField}'. Kronos Schedule id '${scheduleIdByCustomField}'.")
        } else {
            logger.warn("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' wasn't deleted from Kronos, Kronos Shift with id '${shiftIdByCustomField}'. Kronos Schedule id '${scheduleIdByCustomField}'.")
        }

        return null
    }
}
