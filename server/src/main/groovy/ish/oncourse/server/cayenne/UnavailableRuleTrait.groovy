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

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.SessionRepetitionType
import static ish.common.types.SessionRepetitionType.*


trait UnavailableRuleTrait {

    abstract Date getStartDateTime()

    abstract Date getEndDateTime()

    abstract Boolean getAllDay()

    abstract SessionRepetitionType getRecurrenceFrequency()

    abstract Date getUntilDateTime()

    abstract Integer getRepetitionCount()

    @CompileStatic(TypeCheckingMode.SKIP)
    boolean isClashed( Date sessionStart, Date sessionEnd) {
        use(TimeCategory) {
            Date now = new Date()
            Date holidayStart = getAllDay() ? getStartDateTime().clearTime() : getStartDateTime()
            Date holidayEnd = getAllDay() ? getEndDateTime().clearTime() + 23.hour + 59.minute : getEndDateTime()

            if (getRecurrenceFrequency() == null || getRecurrenceFrequency() == NONE_CHOICE ) {
                //chek one time repeat holidays
                if (holidayEnd < now) {
                    return false
                } else {
                    return isOverlap(sessionStart, sessionEnd, holidayStart, holidayEnd)
                }
            } else {
                // session finish before holiday start
                if (holidayStart > sessionEnd) {
                    return false
                }

                Date until
                if (getUntilDateTime() != null) {
                    // filter out holudays which has certain end repetition date
                    until = getUntilDateTime()
                } else if (getRepetitionCount() != null ) {
                    until = addRepeatInterval(getEndDateTime(), getRecurrenceFrequency(), getRepetitionCount())
                } else {
                    //limit infinitely repited holudays with session end date
                    until = sessionEnd
                }

                if (until < now || until < sessionStart) {
                    // holiday in past or finish before session start
                    return false
                }

                Date currentRepeateIntervalStart = holidayStart
                Date currentRepeateIntervalEnd = holidayEnd
                TimeDuration intervalDuration = holidayEnd - holidayStart
                boolean clash = false
                while (currentRepeateIntervalStart <= until && !clash) {
                    clash = isOverlap(sessionStart, sessionEnd, currentRepeateIntervalStart, currentRepeateIntervalEnd)
                    currentRepeateIntervalStart = addRepeatInterval(currentRepeateIntervalStart, getRecurrenceFrequency(), 1)
                    currentRepeateIntervalEnd = currentRepeateIntervalStart + intervalDuration
                }

                return clash

            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private Date addRepeatInterval(Date initialDate, SessionRepetitionType retiatType, Integer count) {
        use(TimeCategory) {
            switch (retiatType) {
                case HOUR_CHOICE:
                    return initialDate + count.hour
                case DAY_CHOICE:
                    return initialDate + count.day
                case WEEK_CHOICE:
                    return initialDate + count.week
                case MONTH_CHOICE:
                    return initialDate + count.month
                case YEAR_CHOICE:
                    return initialDate + count.year
                case CUSTOM_CHOICE:
                case NONE_CHOICE:
                    return initialDate
            }
        }
    }

    private boolean isOverlap(Date sessionStart, Date sessionEnd, Date holidayStart, Date holidayEnd) {
        (sessionStart >= holidayStart && sessionStart <= holidayEnd) ||
                (sessionEnd >= holidayStart && sessionEnd <= holidayEnd) ||
                (sessionStart <= holidayStart && sessionEnd >= holidayEnd) ||
                (sessionStart >= holidayStart && sessionEnd <= holidayEnd)
    }


}
