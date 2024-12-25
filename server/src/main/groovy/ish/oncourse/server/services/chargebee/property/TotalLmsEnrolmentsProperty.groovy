/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.Enrolment
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class TotalLmsEnrolmentsProperty extends ChargebeePropertyProcessor {
    private ObjectContext context

    TotalLmsEnrolmentsProperty(Date startDate, Date endDate, ObjectContext context) {
        super(startDate, endDate)
        this.context = context
    }

    BigDecimal getValue() {
        def records = ObjectSelect.query(Enrolment)
                .where(Enrolment.CREATED_ON.gte(startDate).andExp(Enrolment.CREATED_ON.lt(endDate))
                        .andExp(Enrolment.STATUS.in(EnrolmentStatus.STATUSES_LEGIT)))
                .select(context)

        def recordsToFetch = records.findAll {
            it.courseClass.room?.virtualRoomUrl?.contains("doublealabs.com")
        }

        return (recordsToFetch.sum() { it.createdOn.after(it.courseClass.startDateTime) ? 2 : 1 } as Integer).toBigDecimal()
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_LMS_ENROLMENTS
    }
}
