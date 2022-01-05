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
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

trait StudentTrait {

    abstract Contact getContact();

    @Inject
    private ICayenneService cayenneService

    abstract Long getId()
    abstract ObjectContext getContext()

    boolean isEnrolled(CourseClass courseClass) {
        courseClass.successAndQueuedEnrolments.any { !it.newRecord && it.student.id == id}
    }

    boolean isEnrolled(Course course) {
        course.courseClasses.any { isEnrolled(it) }
    }

    long getActiveConcessionsCount(){
        return ObjectSelect.query(StudentConcession.class)
                .where(
                        StudentConcession.STUDENT.eq((Student)this)
                                .andExp(
                                        StudentConcession.EXPIRES_ON.isNull()
                                                .orExp(StudentConcession.EXPIRES_ON.gt(new Date()))
                                        )
                )
                .selectCount(cayenneService.newReadonlyContext)
    }

    String getPortalLink(def target, def timeout = null) {
        return getContact().getPortalLink(target, timeout)
    }
}
