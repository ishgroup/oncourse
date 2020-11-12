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

package ish.oncourse.server.api.function

import java.util.concurrent.TimeUnit

class DateFunctions {

    static String getTimeAgo(long time) {
        long days = TimeUnit.MILLISECONDS.toDays(time)
        if (days > 0l) {
            return "$days days ago"
        } else {
            long hours = TimeUnit.MILLISECONDS.toHours(time)
            if (hours > 0l) {
                return "$hours hours ago"
            } else {
                "${TimeUnit.MILLISECONDS.toMinutes(time)} mins ago"
            }
        }
    }
}
