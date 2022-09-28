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
import ish.oncourse.server.api.v1.function.CustomFieldFunctions
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.text.SimpleDateFormat

/**
 * Add sessions (as TutorAttendances) to Kronos Shift automatically as they are created/editet in onCourse.
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
 * scheduleName: added Shifts to Kronos Schedules with name 'scheduleName'. Kronos should have a Schedule with this name
 * and period on the date from the session
 *
 */
@API
@CompileStatic
@ScriptClosure(key = "kronos", integration = KronosIntegration)
class KronosScriptClosure implements ScriptClosureTrait<KronosIntegration> {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static final String KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY = "kronosShiftId"
    private static final String KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY = "kronosScheduleId"

    private static Logger logger = LogManager.logger

    Session session
    String scheduleName

    def session(Session session) {
        this.session = session
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
        if (!session) {
            throw new IllegalArgumentException("Session can't be null. Please initialize the session in kronos script closure.")
        }
        if (!isValidSessionFields()) {
            throw new IllegalArgumentException("Session with id '${session.id}' has invalid values: startDateTime, endDatetime, accountCode, uniqueCode. Session will not be added to Kronos.")
        }

        Map scheduleSetting = integration.getScheduleSettingByName(scheduleName)
        String timeZone = integration.findTimeZone(scheduleSetting)

        String date = dateInTimeZoneAndFormat(session.startDatetime, DATE_FORMAT, timeZone)
        String shiftStart = dateInTimeZoneAndFormat(session.startDatetime, DATE_TIME_FORMAT, timeZone)
        String shiftEnd = dateInTimeZoneAndFormat(session.endDatetime, DATE_TIME_FORMAT, timeZone)

        String scheduleId = integration.findScheduleId(scheduleName, scheduleSetting["id"], date)

        String costCenter0 = firstThreeDigitsAccountCode(session.courseClass.incomeAccount.accountCode)
        def costCenter0Id = integration.findCostCenterIndex0Id(costCenter0)
        if (!costCenter0Id) {
            throw new IllegalArgumentException("Session with id '${session.id}': Cost Center with name '${costCenter0}' (onCourse session.courseClass.incomeAccount.accountCode) doesn't exist in Kronos (Cost Centres tree_index:0). Session will not be added to Kronos.")
        }

        //TODO how add to create shift Kronos API request
        String shiftNote = session.courseClass.uniqueCode

        List<TutorAttendance> validatedTutors = validateSessionTutors(session.sessionTutors)
        if (!validatedTutors) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have any valid TutorAttendances(sessionTutors): doesn't have definedTutorRole's names or descriptions, payroll references. Session will not be added to Kronos.")
        }

        List resultShiftIds = new ArrayList()
        for (TutorAttendance tutorAttendance : validatedTutors) {
            String costCenter2 = tutorAttendance.courseClassTutor.definedTutorRole.description
            def costCenter2Id = integration.findCostCenterIndex2Id(costCenter2)
            if (!costCenter2Id) {
                throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance(sessionTutor) with id '${tutorAttendance.id}': Cost Center with name '${costCenter2}' (onCourse courseClassTutor.definedTutorRole.description) doesn't exist in Kronos (Cost Centres tree_index:2). Tutor will not be added to Kronos.")
            }
            String skill = tutorAttendance.courseClassTutor.definedTutorRole.name
            def skillId = integration.findSkillId(skill)
            if (!skillId) {
                throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance(sessionTutor) with id '${tutorAttendance.id}': Skill with name '${skill}' (onCourse courseClassTutor.definedTutorRole.name) doesn't exist in Kronos. Tutor will not be added to Kronos.")
            }
            String employeeId = tutorAttendance.courseClassTutor.tutor.payrollRef
            def accountId = integration.findAccountId(employeeId)
            if (!accountId) {
                throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance(sessionTutor) with id '${tutorAttendance.id}': Employee with employee_id '${employeeId}' (onCourse courseClassTutor.courseClassTutor.payrollRef) doesn't exist in Kronos. Tutor will not be added to Kronos.")
            }
            def resultKronosShift
            def shiftIdByCustomField = getCustomFieldValue(tutorAttendance, KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY)
            def scheduleIdByCustomField = getCustomFieldValue(tutorAttendance, KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY)
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
                logger.info("Kronos Shift was successfully created. Kronos Schedule id '${scheduleId}'")
                def kronosShiftId = integration.getKronosShiftIdBySessionFields(scheduleId, accountId, shiftStart, shiftEnd, skillId, costCenter0Id, costCenter2Id, tutorAttendance.id)
                saveCustomFields(tutorAttendance, kronosShiftId.toString(), scheduleId)
                logger.info("TutorAttendance(sessionTutor) with Kronos Shift id '${kronosShiftId}' was saved custom fields. Kronos Schedule id '${scheduleId}'.")
                resultShiftIds.add(kronosShiftId)
            } else if (resultKronosShift["updated"]) {
                logger.info("Kronos Shift with id '${shiftIdByCustomField}' was successfully updated. Kronos Schedule id '${scheduleIdByCustomField}'.")
                resultShiftIds.add(shiftIdByCustomField)
            }
        }
        logger.warn("Session with id '${session.id}' successfully created/updated with all of its TutorAttedances (ids) - (${validatedTutors.id}), Shifts (ids) - (${resultShiftIds}). Kronos Schedule id '${scheduleId}'.")

        return null
    }

    private boolean isValidSessionFields() {
        if (!session.startDatetime) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have startDateTime, it's null. Session will not be added to Kronos.")
        }
        if (!session.endDatetime) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have endDatetime, it's null. Session will not be added to Kronos.")
        }
        if (!session.courseClass.incomeAccount.accountCode) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have accountCode in incomeAccount in courseClass, it's null or empty. Session will not be added to Kronos.")
        }
        if (!session.courseClass.getUniqueCode()) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have uniqueCode, it's null or empty. Session will not be added to Kronos.")
        }
        return true
    }

    private List<TutorAttendance> validateSessionTutors(List<TutorAttendance> tutorAttendances) {
        if (!tutorAttendances) {
            throw new IllegalArgumentException("Session with id '${session.id}' doesn't have any TutorAttendances(sessionTutors). Session will not be added to Kronos.")
        }
        List<TutorAttendance> validatedTutors = new ArrayList<>()
        for (TutorAttendance tutorAttendance : tutorAttendances) {
            if (!tutorAttendance.courseClassTutor.tutor.payrollRef) {
                throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance(sessionTutor) with id '${tutorAttendance.id}' doesn't have a payroll reference. Tutor will not be added to Kronos.")
            }
            if (!tutorAttendance.courseClassTutor.definedTutorRole.name) {
                throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance(sessionTutor) with id '${tutorAttendance.id}' doesn't have a definedTutorRole name. Tutor will not be added to Kronos.")
            }
            if (!tutorAttendance.courseClassTutor.definedTutorRole.description) {
                throw new IllegalArgumentException("Session's with id '${session.id}' TutorAttendance(sessionTutor) with id '${tutorAttendance.id}' doesn't have a definedTutorRole description. Tutor will not be added to Kronos.")
            }
            validatedTutors.add(tutorAttendance)
        }
        return validatedTutors
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

    private static getCustomFieldValue(TutorAttendance tutorAttendance, String customFieldKey) {
        try {
            tutorAttendance.getCustomFieldValue(customFieldKey)
        } catch (MissingPropertyException ex) {
            logger.warn("TutorAttendanceCustomField '${customFieldKey}' doesn't exist yet. Will be added the first time when will add a shift to Kronos and save TutorAttendanceCustomField with key '${customFieldKey}'. Exception message: ${ex.getMessage()}")
            return null
        }
    }

    private static void saveCustomFields(TutorAttendance tutorAttendance, String kronosShiftId, String kronosScheduleId) {
        CustomFieldFunctions.addCustomFieldWithoutCommit(KRONOS_SHIFT_ID_CUSTOM_FIELD_KEY, kronosShiftId, tutorAttendance, tutorAttendance.context)
        CustomFieldFunctions.addCustomFieldWithoutCommit(KRONOS_SCHEDULE_ID_CUSTOM_FIELD_KEY, kronosScheduleId, tutorAttendance, tutorAttendance.context)
        tutorAttendance.context.commitChanges()
    }
}
