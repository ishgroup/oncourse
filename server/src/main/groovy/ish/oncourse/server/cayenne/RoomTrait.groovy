/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import com.google.inject.Inject
import ish.common.types.CourseClassType
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.query.ObjectSelect

trait RoomTrait {

    @Inject
    private ICayenneService cayenneService

    Long getActiveClassesCount() {
        return ObjectSelect.query(CourseClass.class)
                .where(CourseClass.ROOM.eq((Room) this)
                        .andExp(CourseClass.IS_ACTIVE.isTrue())
                        .andExp(CourseClass.IS_CANCELLED.isFalse())
                        .andExp(
                                CourseClass.END_DATE_TIME.gt(new Date())
                                        .orExp(CourseClass.TYPE.eq(CourseClassType.DISTANT_LEARNING))
                        )
                )
                .selectCount(cayenneService.newReadonlyContext)
    }

    Long getFutureClassesCount() {
        return ObjectSelect.query(CourseClass.class)
                .where(
                        CourseClass.ROOM
                                .eq((Room) this)
                                .andExp(
                                        CourseClass.START_DATE_TIME
                                                .isNotNull()
                                                .andExp(CourseClass.START_DATE_TIME.gt(new Date()))
                                )
                )
                .selectCount(cayenneService.newReadonlyContext)
    }

}