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

package ish.oncourse.server.cayenne

import javax.inject.Inject
import ish.oncourse.API
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.CourseStatusDTO
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.api.v1.model.CourseStatusDTO.COURSE_DISABLED
import static ish.oncourse.server.api.v1.model.CourseStatusDTO.ENABLED
import static ish.oncourse.server.api.v1.model.CourseStatusDTO.ENABLED_AND_VISIBLE_ONLINE
import ish.persistence.CommonExpressionFactory
import org.apache.cayenne.PersistenceState

trait CourseTrait {

    abstract List<CourseClass> getCourseClasses()

    abstract Boolean getIsShownOnWeb()

    abstract Boolean getCurrentlyOffered()

    @Inject
    private ICayenneService cayenneService

    /**
     * @return count of current classes for the course
     */
    @API
    Integer getCurrentClassesCount() {
        Date now = new Date()
        Date tomorrow = CommonExpressionFactory.nextMidnight(now)
        Date today = CommonExpressionFactory.previousMidnight(now)
        getCourseClasses().findAll { it.startDateTime != null && it.endDateTime != null && it.startDateTime <  tomorrow && it.endDateTime >= today && !it.isCancelled }.size()
    }

    /**
     * @return number of all courseClasses, whether current, past or future
     */
    Integer getAllClassesCount() {
        return ObjectSelect.query(CourseClass.class)
                .where(CourseClass.COURSE.eq((Course)this))
                .selectCount(cayenneService.newReadonlyContext)
    }

    /**
     * @return count of future classes for the course
     */
    @API
    Integer getFutureClasseCount() {
        Date now = new Date()
        Date tomorrow = CommonExpressionFactory.nextMidnight(now)
        getCourseClasses().findAll { it.startDateTime != null && it.endDateTime != null && it.startDateTime >=  tomorrow && it.endDateTime >= tomorrow && !it.isCancelled }.size()
    }

    /**
     * @return count of self paced classes for the course
     */

    @API
    Integer getSelfPacedClassesCount() {
        getCourseClasses().findAll { it.isDistantLearningCourse && !it.isCancelled }.size()
    }

    /**
     * @return count of unscheduled classes for the course
     */

    @API
    Integer getUnscheduledClassesCount() {
        getCourseClasses().findAll { (it.startDateTime == null || it.endDateTime == null) && !it.isDistantLearningCourse && !it.isCancelled }.size()
    }

    /**
     * @return count of passed classes for the course
     */
    @API
    Integer getPassedClassesCount() {
        Date now = new Date()
        Date today = CommonExpressionFactory.previousMidnight(now)
        getCourseClasses().findAll { it.startDateTime != null && it.endDateTime != null && it.endDateTime < today && !it.isCancelled }.size()
    }

    /**
     * @return count of cancelled classes for the course
     */
    @API
    Integer getCancelledClassesCount() {
        getCourseClasses().findAll { it.isCancelled }.size()
    }

    /**
     * creates next logical code for a class. code is based on current code or code for the last available class.
     * @return new code
     */
    @API
    String getNextAvailableCode(CourseClass courseClass = null) {
        String oldCode = courseClass?.code
        if (oldCode == null) {
            CourseClass latestClass = getLatestSavedClass()
            if (latestClass != null) {
                return getNextAvailableCode(latestClass)
            }
            return '1'
        }

        int i = oldCode.length()
        while (i > 0 && Character.isDigit(oldCode.charAt(i - 1))) {
            i--
        }
        String staticPart = oldCode.substring(0, i)
        String oldNumericPart = oldCode.substring(i)

        int n = 0
        if (!oldNumericPart.empty) {
            n = Integer.valueOf(oldNumericPart)
        }

        String newCode = null

        Closure addNumber = {
            n++
            String newNumericPart = '' + n
            while (newNumericPart.length() < oldNumericPart.length()) {
                newNumericPart = 0 + newNumericPart
            }

            newCode = staticPart + newNumericPart
        }

        addNumber()
        while (timesClassCodeRepeatsWithinCourse(newCode, courseClass) > 0) {
            addNumber()
        }

        return newCode
    }

    private CourseClass getLatestSavedClass() {
        if (courseClasses.empty) {
            return null
        }
        return courseClasses.sort {it.createdOn}.reverse().find { it.persistenceState != PersistenceState.NEW }

    }


    private int timesClassCodeRepeatsWithinCourse(String aCode, CourseClass courseClass) {
        int result = 0
        for (CourseClass clazz : courseClasses) {
            if (clazz != courseClass && clazz.code  && clazz.code.equalsIgnoreCase(aCode)) {
                result++
            }
        }
        return result
    }

    @API
    CourseStatusDTO getStatus(){
        return !getIsShownOnWeb() && !getCurrentlyOffered() ? COURSE_DISABLED : !getIsShownOnWeb() ? ENABLED : ENABLED_AND_VISIBLE_ONLINE
    }

}
