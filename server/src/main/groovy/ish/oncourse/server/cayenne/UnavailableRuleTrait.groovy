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

import groovy.time.BaseDuration
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import ish.common.types.SessionRepetitionType

import java.time.zone.ZoneOffsetTransition
import java.util.concurrent.TimeUnit

import static ish.common.types.SessionRepetitionType.*


trait UnavailableRuleTrait {

    abstract Date getStartDateTime()

    abstract Date getEndDateTime()

    abstract Boolean getAllDay()

    abstract SessionRepetitionType getRecurrenceFrequency()

    abstract Date getUntilDateTime()

    abstract Integer getRepetitionCount()

    /**
     * Checks the rule against a session in the server time zone.
     */
    boolean isClashed(Date sessionStart, Date sessionEnd) {
        return isClashed(sessionStart, sessionEnd, TimeZone.getDefault())
    }

    /**
     * Checks whether a session overlaps the rule or one of its repeats.
     *
     * All-day rules are saved as server time zone midnights. They are calendar days, so here they cover
     * the same days in {@code timeZone} (the session's site time zone) and repeat on that zone's calendar.
     * Timed rules are fixed moments and repeat on the server time zone calendar.
     */
    boolean isClashed(Date sessionStart, Date sessionEnd, TimeZone timeZone) {
        Date now = new Date()
        boolean allDay = getAllDay() as boolean
        TimeZone ruleZone = allDay ? timeZone : TimeZone.getDefault()
        Date holidayStart = allDay ? startOfDay(getStartDateTime(), timeZone) : getStartDateTime()
        Date holidayEnd = allDay ? shift(startOfDay(getEndDateTime(), timeZone), timeZone, new TimeDuration(0, 23, 59, 0, 0)) : getEndDateTime()

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
                until = allDay ? startOfDay(getUntilDateTime(), timeZone) : getUntilDateTime()
            } else if (getRepetitionCount() != null ) {
                Date lastEnd = allDay ? startOfDay(getEndDateTime(), timeZone) : getEndDateTime()
                until = shift(lastEnd, ruleZone, repeatInterval(getRecurrenceFrequency(), getRepetitionCount()))
            } else {
                //limit infinitely repited holudays with session end date
                until = sessionEnd
            }

            if (until < now || until < sessionStart) {
                // holiday in past or finish before session start
                return false
            }

            BaseDuration step = repeatInterval(getRecurrenceFrequency(), 1)
            BaseDuration intervalDuration = TimeCategory.minus(holidayEnd, holidayStart)
            Date currentRepeateIntervalStart = holidayStart
            Date currentRepeateIntervalEnd = holidayEnd
            if (step != null) {
                // repeats which end a day before the session can't overlap it, so skip them instead of checking each
                Date sessionFrom = sessionStart < sessionEnd ? sessionStart : sessionEnd
                long intervalLength = Math.abs(holidayEnd.time - holidayStart.time)
                Date skipBefore = new Date(sessionFrom.time - intervalLength - TimeUnit.DAYS.toMillis(1))
                currentRepeateIntervalStart = lastRepeatBefore(holidayStart, skipBefore, getRecurrenceFrequency(), ruleZone)
                if (!currentRepeateIntervalStart.is(holidayStart)) {
                    currentRepeateIntervalEnd = shift(currentRepeateIntervalStart, ruleZone, intervalDuration)
                }
            }
            boolean clash = false
            while (currentRepeateIntervalStart <= until && !clash) {
                clash = isOverlap(sessionStart, sessionEnd, currentRepeateIntervalStart, currentRepeateIntervalEnd)
                if (step == null) {
                    break
                }
                currentRepeateIntervalStart = shift(currentRepeateIntervalStart, ruleZone, step)
                currentRepeateIntervalEnd = shift(currentRepeateIntervalStart, ruleZone, intervalDuration)
            }

            return clash
        }
    }

    /**
     * Each repeat is the previous one shifted by a step, so it keeps what DST or a short month did to an earlier
     * repeat (a monthly rule on the 31st moves to the 28th after February). n steps at once give the same date
     * while the zone offset doesn't change and no month can cut the day short, so those repeats are skipped in
     * one shift, and the steps across an offset change or a month end are taken one by one.
     *
     * @return the last repeat, as found step by step from {@code first}, that starts before {@code before};
     * {@code first} itself when the next repeat doesn't
     */
    private Date lastRepeatBefore(Date first, Date before, SessionRepetitionType frequency, TimeZone timeZone) {
        BaseDuration step = repeatInterval(frequency, 1)
        Date current = first
        while (true) {
            Date next = shift(current, timeZone, step)
            if (!next.before(before)) {
                return current
            }
            if (!mayCutDayOfMonth(current, timeZone, frequency)) {
                Date limit = before
                // hours are a fixed number of milliseconds, so DST doesn't change them
                if (frequency != HOUR_CHOICE) {
                    ZoneOffsetTransition transition = timeZone.toZoneId().rules.nextTransition(current.toInstant())
                    if (transition != null && transition.instant.toEpochMilli() < limit.time) {
                        limit = Date.from(transition.instant)
                    }
                }
                int count = repeatsBefore(current, limit, frequency, timeZone)
                if (count > 1) {
                    current = shift(current, timeZone, repeatInterval(frequency, count))
                    continue
                }
            }
            current = next
        }
    }

    /**
     * @return whether a monthly or yearly step from the date can land on a shorter month and move to its last day
     */
    private boolean mayCutDayOfMonth(Date date, TimeZone timeZone, SessionRepetitionType frequency) {
        if (frequency != MONTH_CHOICE && frequency != YEAR_CHOICE) {
            return false
        }
        Calendar calendar = Calendar.getInstance(timeZone)
        calendar.setTime(date)
        return calendar.get(Calendar.DAY_OF_MONTH) > 28 &&
                (frequency == MONTH_CHOICE || calendar.get(Calendar.MONTH) == Calendar.FEBRUARY)
    }

    /**
     * @return the largest number of repeats from {@code from} that still start before {@code limit}
     */
    private int repeatsBefore(Date from, Date limit, SessionRepetitionType frequency, TimeZone timeZone) {
        long count
        switch (frequency) {
            case MONTH_CHOICE:
            case YEAR_CHOICE:
                Calendar start = Calendar.getInstance(timeZone)
                start.setTime(from)
                Calendar end = Calendar.getInstance(timeZone)
                end.setTime(limit)
                long months = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12L + end.get(Calendar.MONTH) - start.get(Calendar.MONTH)
                count = frequency == MONTH_CHOICE ? months : months.intdiv(12L) as long
                break
            default:
                count = (limit.time - from.time).intdiv(repeatInterval(frequency, 1).toMilliseconds()) as long
        }
        // the estimate can be a repeat or two off, as days change length with DST and months differ
        int result = Math.max(0L, Math.min(count, Integer.MAX_VALUE - 1L)) as int
        while (result > 0 && !shift(from, timeZone, repeatInterval(frequency, result)).before(limit)) {
            result--
        }
        while (shift(from, timeZone, repeatInterval(frequency, result + 1)).before(limit)) {
            result++
        }
        return result
    }

    /**
     * @return the duration of {@code count} repeats, or null when the rule doesn't repeat on a fixed interval
     */
    private BaseDuration repeatInterval(SessionRepetitionType repeatType, Integer count) {
        switch (repeatType) {
            case HOUR_CHOICE:
                return TimeCategory.getHours(count)
            case DAY_CHOICE:
                return TimeCategory.getDays(count)
            case WEEK_CHOICE:
                return TimeCategory.getWeeks(count)
            case MONTH_CHOICE:
                return TimeCategory.getMonths(count)
            case YEAR_CHOICE:
                return TimeCategory.getYears(count)
            default:
                return null
        }
    }

    /**
     * Same as {@code date + duration} under TimeCategory, but on the calendar of the given time zone.
     */
    private Date shift(Date date, TimeZone timeZone, BaseDuration duration) {
        if (duration == null) {
            return date
        }
        Calendar calendar = Calendar.getInstance(timeZone)
        calendar.setTime(date)
        calendar.add(Calendar.YEAR, duration.years)
        calendar.add(Calendar.MONTH, duration.months)
        calendar.add(Calendar.DAY_OF_YEAR, duration.days)
        calendar.add(Calendar.HOUR_OF_DAY, duration.hours)
        calendar.add(Calendar.MINUTE, duration.minutes)
        calendar.add(Calendar.SECOND, duration.seconds)
        calendar.add(Calendar.MILLISECOND, duration.millis)
        return calendar.getTime()
    }

    /**
     * All-day dates are saved as midnights of the server time zone (see LocalDateUtils.valueToDate).
     * @return the start of the same calendar day in the given time zone
     */
    private Date startOfDay(Date date, TimeZone timeZone) {
        Calendar saved = Calendar.getInstance()
        saved.setTime(date)
        Calendar day = Calendar.getInstance(timeZone)
        day.clear()
        day.set(saved.get(Calendar.YEAR), saved.get(Calendar.MONTH), saved.get(Calendar.DAY_OF_MONTH))
        return day.getTime()
    }

    private boolean isOverlap(Date sessionStart, Date sessionEnd, Date holidayStart, Date holidayEnd) {
        (sessionStart >= holidayStart && sessionStart <= holidayEnd) ||
                (sessionEnd >= holidayStart && sessionEnd <= holidayEnd) ||
                (sessionStart <= holidayStart && sessionEnd >= holidayEnd) ||
                (sessionStart >= holidayStart && sessionEnd <= holidayEnd)
    }


}
