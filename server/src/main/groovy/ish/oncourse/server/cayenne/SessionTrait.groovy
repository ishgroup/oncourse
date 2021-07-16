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


import ish.util.DurationFormatter

import java.time.ZoneId

trait SessionTrait {
    abstract TimeZone getTimeZone()
    abstract Date getStartDatetime()
    abstract Date getEndDatetime()
    abstract Room getRoom()
    abstract CourseClass getCourseClass()

    Date getDisplayStartDateTime() {
        Date date = getStartDatetime()
        if (date) {
            return Date.from(date.toInstant().atZone(getTimeZone().toZoneId()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant())
        }
        return null
    }

    Date getDisplayEndDateTime() {
        Date date = getEndDatetime()
        if (date) {
            return Date.from(date.toInstant().atZone(getTimeZone().toZoneId()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant())
        }
        return null
    }

    String getDisplayableLocation() {
        return  room != null ? "$room.site.name: ${room.site.street?:''} ${room.site.suburb?:''} ${room.site.postcode?:''}" : courseClass.displayableLocation
    }


    /**
     * @return session duration in minutes
     */
    Integer getDurationInMinutes() {
        return DurationFormatter.parseDurationInMinutes(getStartDatetime(), getEndDatetime())
    }

}
