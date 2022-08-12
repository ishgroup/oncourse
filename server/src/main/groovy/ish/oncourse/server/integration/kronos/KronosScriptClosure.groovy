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
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.text.SimpleDateFormat

/**
 * Fields mapping from onCourse to Kronos:
 * Schedule name >> "Ish Schedules"
 * Date >> session date
 * Start Time >> session tutor rostered start time
 * End Time >> session tutor rostered end time
 * Employee ID >> tutor.payrollRef
 * Cost Center 1 >> First 3 digits of courseClass.incomeAccount
 * Cost Center 3 >> courseClassTutor.definedTutorRole.name
 * Skill >> courseClassTutor.definedTutorRole.description
 * Shift Note >> courseClasscode
 */
@API
@CompileStatic
@ScriptClosure(key = "kronos", integration = KronosIntegration)
class KronosScriptClosure implements ScriptClosureTrait<KronosIntegration> {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"
    private static final String DATE_FORMAT = "yyyy-MM-dd"

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
            logger.warn("Session with id ${session.id} has invalid values: startDateTime, endDatetime, accountCode, uniqueCode. Session will not be added to Kronos.")
            return null
        }

        //TODO rewrite storing schedule logic after agreement with James
        if (!KronosIntegration.kronosScheduleId || !scheduleName.equals(KronosIntegration.kronosScheduleName)) {
            integration.initSchedule(scheduleName)
        }

        String date = dateInTimeZoneAndFormat(session.startDatetime, DATE_FORMAT, KronosIntegration.kronosScheduleTimeZone)
        String shiftStart = dateInTimeZoneAndFormat(session.startDatetime, DATE_TIME_FORMAT, KronosIntegration.kronosScheduleTimeZone)
        String shiftEnd = dateInTimeZoneAndFormat(session.endDatetime, DATE_TIME_FORMAT, KronosIntegration.kronosScheduleTimeZone)
        String costCenter1 = firstThreeDigitsAccountCode(session.courseClass.incomeAccount.accountCode)
        def costCenter1Id = integration.findCostCenterId(costCenter1)
        if (!costCenter1Id) {
            logger.warn("Session with id ${session.id}: Cost Center with name '${costCenter1}' (onCourse session.courseClass.incomeAccount.accountCode) don't exist in Kronos. Session will not be added to Kronos.")
            return null
        }

        //TODO how add to create shift Kronos API request
        String shiftNote = session.courseClass.uniqueCode

        List<CourseClassTutor> validatedTutors = validateSessionTutors(session.tutorRoles)
        if (!validatedTutors) {
            logger.warn("Session with id ${session.id} doesn't have any validated tutors: doesn't have definedTutorRole's names or descriptions, payroll references. Session will not be added to Kronos.")
            return null
        }

        for (CourseClassTutor courseClassTutor : validatedTutors) {
            String costCenter3 = courseClassTutor.definedTutorRole.name
            def costCenter3Id = integration.findCostCenterId(costCenter3)
            if (!costCenter3Id) {
                logger.warn("Session's with id ${session.id} CourseClassTutor with id ${courseClassTutor.id}: Cost Center with name '${costCenter3}' (onCourse courseClassTutor.definedTutorRole.name) don't exist in Kronos. Tutor will not be added to Kronos.")
                continue
            }
            String skill = courseClassTutor.definedTutorRole.description
            def skillId = integration.findSkillId(skill)
            if (!skillId) {
                logger.warn("Session's with id ${session.id} CourseClassTutor with id ${courseClassTutor.id}: Skill with name '${skill}' (onCourse courseClassTutor.definedTutorRole.description) don't exist in Kronos. Tutor will not be added to Kronos.")
                continue
            }
            String employeeId = courseClassTutor.tutor.payrollRef
            def accountId = integration.findAccountId(employeeId)
            if (!accountId) {
                logger.warn("Session's with id ${session.id} CourseClassTutor with id ${courseClassTutor.id}: Employee with employee_id '${employeeId}' (onCourse courseClassTutor.courseClassTutor.payrollRef) don't exist in Kronos. Tutor will not be added to Kronos.")
                continue
            }
    //TODO Don't throw exception if shift didn't create? Maybe return ['success'/'failure', resp, result] and make check 'success' or 'failure' then write log (warn/info): 'Shift was successfully created in Kronos' or 'Failed: Shift wasn't created in Kronos: [resp, result]'
            def shift = integration.createNewShift(accountId, date, shiftStart, shiftEnd, costCenter1Id, costCenter3Id, skillId, KronosIntegration.kronosScheduleId)
        }

        return null
    }

    private boolean isValidSessionFields() {
        boolean isValid = true
        if (!session.startDatetime) {
            logger.warn("Session with id ${session.id} doesn't have startDateTime, it's null. Session will not be added to Kronos.")
            isValid = false
        }
        if (!session.endDatetime) {
            logger.warn("Session with id ${session.id} doesn't have endDatetime, it's null. Session will not be added to Kronos.")
            isValid = false
        }
        if (!session.courseClass.incomeAccount.accountCode) {
            logger.warn("Session with id ${session.id} doesn't have accountCode in incomeAccount in courseClass, it's null or empty. Session will not be added to Kronos.")
            isValid = false
        }
        if (!session.courseClass.getUniqueCode()) {
            logger.warn("Session with id ${session.id} doesn't have uniqueCode, it's null or empty. Session will not be added to Kronos.")
            isValid = false
        }
        return isValid
    }

    private List<CourseClassTutor> validateSessionTutors(List<CourseClassTutor> courseClassTutors) {
        if (!courseClassTutors) {
            logger.warn("Session with id ${session.id} doesn't have any tutors. Session will not be added to Kronos.")
            return null
        }
        List<CourseClassTutor> validatedTutors = new ArrayList<>()
        for (CourseClassTutor courseClassTutor : courseClassTutors) {
            if (!courseClassTutor.tutor.payrollRef) {
                logger.warn("Session's with id ${session.id} CourseClassTutor with id ${courseClassTutor.id} doesn't have a payroll reference. Tutor will not be added to Kronos.")
                continue
            }
            if (!courseClassTutor.definedTutorRole.name) {
                logger.warn("Session's with id ${session.id} CourseClassTutor with id ${courseClassTutor.id} doesn't have a definedTutorRole's name. Tutor will not be added to Kronos.")
                continue
            }
            if (!courseClassTutor.definedTutorRole.description) {
                logger.warn("Session's with id ${session.id} CourseClassTutor with id ${courseClassTutor.id} doesn't have a definedTutorRole's description. Tutor will not be added to Kronos.")
                continue
            }
            validatedTutors.add(courseClassTutor)
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
}
