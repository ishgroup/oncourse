/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.kronos

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.text.SimpleDateFormat

/**
 * Add TutorAttendances to Kronos Shift automatically as it is created/edited in onCourse.
 *
 * Fields mapping from onCourse to Kronos:
 * Schedule name >> "Ish Schedules"
 * Date >> session date
 * Start Time >> session tutor rostered start time
 * End Time >> session tutor rostered end time
 * Employee ID >> tutor.payrollRef
 * Cost Center 0 >> First 3 digits of courseClass.incomeAccount
 * Cost Center 2 >> courseClassTutor.definedTutorRole.description
 * Skill >> courseClassTutor.definedTutorRole.name
 * Shift Note >> courseClasscode
 *
 *
 * ```
 * kronos {
 *     scheduleName "Weekly Test Schedule"
 *     session record
 * }
 * ```
 *
 * scheduleName: add Shifts to Kronos Schedules with name 'scheduleName'. Kronos should have a Schedule with this name
 * and period on the date from the TutorAttendance
 *
 */
@API
@CompileStatic
@ScriptClosure(key = "kronosCreateEdit", integration = KronosIntegration)
class KronosCreateEditScriptClosure implements ScriptClosureTrait<KronosIntegration> {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static Logger logger = LogManager.logger

    TutorAttendance tutorAttendance
    String scheduleName

    def tutorAttendance(TutorAttendance tutorAttendance) {
        this.tutorAttendance = tutorAttendance
    }

    def scheduleName(String scheduleName) {
        this.scheduleName = scheduleName
    }

    @Override
    Object execute(KronosIntegration integration) {
        integration.initAuthHeader()

        if (!scheduleName) {
            throw new IllegalArgumentException("Schedule name can't be null or empty. Please initialize the variable ScheduleName in kronos script closure.")
        }
        if (!tutorAttendance) {
            throw new IllegalArgumentException("TutorAttendance can't be null. Please initialize the session in kronos script closure.")
        }
        Session session = tutorAttendance.session
        if (!session) {
            throw new IllegalArgumentException("TutorAttendance doesn't have Session. TutorAttendance with id '${tutorAttendance.id}' will not be added to Kronos.")
        }

        validateSession(session)
        validateTutorAttendance(tutorAttendance)

        Map scheduleSetting = integration.getScheduleSettingByName(scheduleName)
        String timeZone = integration.findTimeZone(scheduleSetting)

        String date = dateInTimeZoneAndFormat(tutorAttendance.startDatetime, DATE_FORMAT, timeZone)
        String shiftStart = dateInTimeZoneAndFormat(tutorAttendance.startDatetime, DATE_TIME_FORMAT, timeZone)
        String shiftEnd = dateInTimeZoneAndFormat(tutorAttendance.endDatetime, DATE_TIME_FORMAT, timeZone)

        String scheduleId = integration.findScheduleId(scheduleName, scheduleSetting["id"], date)

        String costCenter0 = firstThreeDigitsAccountCode(session.courseClass.incomeAccount.accountCode)
        def costCenter0Id = integration.findCostCenterIndex0Id(costCenter0)
        if (!costCenter0Id) {
            throw new IllegalArgumentException("Session with id '${session.id}': Cost Center with name '${costCenter0}' (onCourse session.courseClass.incomeAccount.accountCode) doesn't exist in Kronos (Cost Centres tree_index:0). Session will not be added to Kronos.")
        }

        //TODO how add to create shift Kronos API request
        String shiftNote = session.courseClass.uniqueCode
        
        String costCenter2 = tutorAttendance.courseClassTutor.definedTutorRole.description
        def costCenter2Id = integration.findCostCenterIndex2Id(costCenter2)
        if (!costCenter2Id) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}': Cost Center with name '${costCenter2}' (onCourse courseClassTutor.definedTutorRole.description) doesn't exist in Kronos (Cost Centres tree_index:2). TutorAttendance will not be added to Kronos.")
        }
        String skill = tutorAttendance.courseClassTutor.definedTutorRole.name
        def skillId = integration.findSkillId(skill)
        if (!skillId) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}': Skill with name '${skill}' (onCourse courseClassTutor.definedTutorRole.name) doesn't exist in Kronos. TutorAttendance will not be added to Kronos.")
        }
        String employeeId = tutorAttendance.courseClassTutor.tutor.payrollRef
        def accountId = integration.findAccountId(employeeId)
        if (!accountId) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}': Employee with employee_id '${employeeId}' (onCourse courseClassTutor.courseClassTutor.payrollRef) doesn't exist in Kronos. TutorAttendance will not be added to Kronos.")
        }
        def resultKronosShift
        def shiftIdByCustomField = integration.getCustomFieldShiftId(tutorAttendance)
        def scheduleIdByCustomField = integration.getCustomFieldScheduleId(tutorAttendance)
        if (shiftIdByCustomField) {
            if (scheduleIdByCustomField != scheduleId) {
                throw new IllegalArgumentException("Schedule id '${scheduleId}' different from custom field schedule id '${scheduleIdByCustomField}'. Shift will not be updated in Kronos. Additional information: start date '${shiftStart}' and end date '${shiftEnd}'.")
            }
            resultKronosShift = integration.updateShift(shiftIdByCustomField, accountId, date, shiftStart, shiftEnd, costCenter0Id, costCenter2Id, skillId, scheduleId)
        }
        else {
            resultKronosShift = integration.createNewShift(accountId, date, shiftStart, shiftEnd, costCenter0Id, costCenter2Id, skillId, scheduleId)
        }
        if (resultKronosShift["created"]) {
            logger.warn("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' was successfully created in as Kronos Shift. Kronos Schedule id '${scheduleId}'")
            def kronosShiftId = integration.getKronosShiftIdBySessionFields(scheduleId, accountId, shiftStart, shiftEnd, skillId, costCenter0Id, costCenter2Id, tutorAttendance.id)
            integration.saveCustomFields(tutorAttendance, kronosShiftId.toString(), scheduleId)
            logger.warn("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' with Kronos Shift id '${kronosShiftId}' was successfully saved custom fields. Kronos Schedule id '${scheduleId}'.")
        } else if (resultKronosShift["updated"]) {
            logger.warn("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' was successfully updated in Kronos, Kronos Shift with id '${shiftIdByCustomField}'. Kronos Schedule id '${scheduleIdByCustomField}'.")
        }

        return null
    }

    private void validateSession(Session session) {
        if (!session.courseClass.incomeAccount.accountCode) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have accountCode in incomeAccount in courseClass, it's null or empty. The TutorAttendance with id '${tutorAttendance.id}' from Session with id '${session.id}' will not be added to Kronos.")
        }
        if (!session.courseClass.uniqueCode) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have uniqueCode, it's null or empty. The TutorAttendance with id '${tutorAttendance.id}' from Session with id '${session.id}' will not be added to Kronos.")
        }
    }

    private static void validateTutorAttendance(TutorAttendance tutorAttendance) {
        Session session = tutorAttendance.session
        if (!tutorAttendance.startDatetime) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have startDateTime, it's null. TutorAttendance will not be added to Kronos.")
        }
        if (!tutorAttendance.endDatetime) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have endDatetime, it's null. TutorAttendance will not be added to Kronos.")
        }
        if (!tutorAttendance.courseClassTutor.tutor.payrollRef) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have a payroll reference. TutorAttendance will not be added to Kronos.")
        }
        if (!tutorAttendance.courseClassTutor.definedTutorRole.name) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have a definedTutorRole name. TutorAttendance will not be added to Kronos.")
        }
        if (!tutorAttendance.courseClassTutor.definedTutorRole.description) {
            throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance with id '${tutorAttendance.id}' doesn't have a definedTutorRole description. TutorAttendance will not be added to Kronos.")
        }
    }

    private static String firstThreeDigitsAccountCode(String accountCode) {
        String digits = accountCode.replaceAll("[^\\d+]", "")
        return digits.substring(0, Math.min(digits.length(), 3))
    }

    private static String dateInTimeZoneAndFormat(Date date, String format, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format)
        TimeZone tZone = TimeZone.getTimeZone(timeZone)
        sdf.setTimeZone(tZone)
        return sdf.format(date)
    }
}
