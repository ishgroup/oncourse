/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util

import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.WaitingList
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class WaitingListUtils {
    public static boolean waitingListExists(Course course, Student student, ObjectContext context, Long idToExclude = null) {
        def waitingListsFilter = WaitingList.STUDENT.eq(student)
                .andExp(WaitingList.COURSE.eq(course))

        if(idToExclude)
            waitingListsFilter = waitingListsFilter.andExp(WaitingList.ID.ne(idToExclude))

        def sameWaitingList = ObjectSelect.query(WaitingList)
                .where(waitingListsFilter)
                .selectFirst(context)

        return sameWaitingList != null
    }
}
