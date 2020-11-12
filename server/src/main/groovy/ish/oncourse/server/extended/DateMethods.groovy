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

package ish.oncourse.server.extended

import ish.util.LocalDateUtils

import java.time.LocalDate
import java.time.Period

/**
 * Created by anarut on 11/3/16.
 */
class DateMethods {

    //it's used to support old versions (before 9.2) of scripts where Invoice.dueDate was as java.util.Date
    @Deprecated
    static int minus(Date self, LocalDate then) {
        return Period.between(then, LocalDateUtils.dateToValue(self)).days
    }
}
