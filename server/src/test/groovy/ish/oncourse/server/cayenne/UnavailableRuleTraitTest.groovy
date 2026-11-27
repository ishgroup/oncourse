/*
 * Copyright ish group pty ltd 2026.
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
import groovy.transform.CompileStatic
import groovy.transform.ToString
import ish.common.types.SessionRepetitionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

import static ish.common.types.SessionRepetitionType.*

@CompileStatic
class UnavailableRuleTraitTest {

    private static final TimeZone SYDNEY = TimeZone.getTimeZone('Australia/Sydney')

    @Test
    void testOldHourlyRuleAgainstFarFutureSessionIsFast() {
        UnavailableRuleTraitStub rule = new UnavailableRuleTraitStub(startDateTime: date('2020-01-01T10:00', SYDNEY), endDateTime: date('2020-01-01T10:30', SYDNEY), recurrenceFrequency: HOUR_CHOICE)

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), {
            // about 7.7 million hourly repeats from the rule start
            Assertions.assertTrue(rule.isClashed(date('2900-06-01T10:15', SYDNEY), date('2900-06-01T10:45', SYDNEY), SYDNEY))
            Assertions.assertTrue(rule.isClashed(date('2900-06-01T11:20', SYDNEY), date('2900-06-01T11:25', SYDNEY), SYDNEY))
            Assertions.assertFalse(rule.isClashed(date('2900-06-01T10:40', SYDNEY), date('2900-06-01T10:50', SYDNEY), SYDNEY))
        } as Executable)
    }

    @Test
    void testOldDailyRuleAgainstFarFutureSessionIsFast() {
        UnavailableRuleTraitStub rule = new UnavailableRuleTraitStub(startDateTime: date('2020-03-31T09:00', SYDNEY), endDateTime: date('2020-03-31T10:00', SYDNEY), recurrenceFrequency: DAY_CHOICE)
        UnavailableRuleTraitStub allDayRule = new UnavailableRuleTraitStub(startDateTime: date('2020-03-31T00:00', SYDNEY), endDateTime: date('2020-03-31T00:00', SYDNEY),
                recurrenceFrequency: WEEK_CHOICE, allDay: true)

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), {
            Assertions.assertTrue(rule.isClashed(date('9000-12-01T09:30', SYDNEY), date('9000-12-01T11:00', SYDNEY), SYDNEY))
            Assertions.assertFalse(rule.isClashed(date('9000-12-01T10:30', SYDNEY), date('9000-12-01T11:00', SYDNEY), SYDNEY))
            // 31 Mar 2020 was a Tuesday, so is 3 Dec 2120
            Assertions.assertTrue(allDayRule.isClashed(date('2120-12-03T09:30', SYDNEY), date('2120-12-03T11:00', SYDNEY), SYDNEY))
            Assertions.assertFalse(allDayRule.isClashed(date('2120-12-04T09:30', SYDNEY), date('2120-12-04T11:00', SYDNEY), SYDNEY))
        } as Executable)
    }

    @Test
    void testMonthlyRuleKeepsDayCutByShortMonth() {
        // stepping a month at a time moves a rule on the 31st to the 28th after February, and it stays there
        UnavailableRuleTraitStub rule = new UnavailableRuleTraitStub(startDateTime: date('2027-01-31T10:00', SYDNEY), endDateTime: date('2027-01-31T11:00', SYDNEY), recurrenceFrequency: MONTH_CHOICE)

        Assertions.assertTrue(rule.isClashed(date('2047-07-28T10:30', SYDNEY), date('2047-07-28T10:45', SYDNEY), SYDNEY))
        Assertions.assertFalse(rule.isClashed(date('2047-07-31T10:30', SYDNEY), date('2047-07-31T10:45', SYDNEY), SYDNEY))
    }

    /**
     * Compares the results with checking every repeat from the rule start, around DST changes (including zones that
     * change at midnight) and month ends.
     */
    @Test
    void testSameResultsAsCheckingEveryRepeat() {
        Random random = new Random(265L)
        List<TimeZone> zones = ['Australia/Sydney', 'Australia/Perth', 'Australia/Lord_Howe', 'America/Santiago', 'Europe/London']
                .collect { TimeZone.getTimeZone(it) }
        List<SessionRepetitionType> frequencies = [HOUR_CHOICE, DAY_CHOICE, WEEK_CHOICE, MONTH_CHOICE, YEAR_CHOICE]
        Map<SessionRepetitionType, Integer> maxRepeats = [(HOUR_CHOICE): 24 * 200, (DAY_CHOICE): 4000, (WEEK_CHOICE): 1000,
                                                          (MONTH_CHOICE): 400, (YEAR_CHOICE): 40]
        int clashes = 0
        int cases = 2000

        cases.times {
            SessionRepetitionType frequency = frequencies[random.nextInt(frequencies.size())]
            TimeZone zone = zones[random.nextInt(zones.size())]
            boolean allDay = random.nextInt(3) == 0

            // rules are saved in the server time zone; times between 0:00 and 3:00 hit DST changes more often
            TimeZone serverZone = TimeZone.getDefault()
            int day = random.nextInt(4) == 0 ? 29 + random.nextInt(3) : 1 + random.nextInt(28)
            LocalDateTime ruleStart = LocalDateTime.of(2027 + random.nextInt(4), 1 + random.nextInt(12), 1, 0, 0)
            ruleStart = ruleStart.withDayOfMonth(Math.min(day, ruleStart.toLocalDate().lengthOfMonth()))
            if (!allDay) {
                ruleStart = ruleStart.withHour(random.nextBoolean() ? random.nextInt(3) : random.nextInt(24)).withMinute(30 * random.nextInt(2))
            }
            Date start = date(ruleStart, serverZone)
            Date end = allDay ? date(ruleStart.plusDays(random.nextInt(2)), serverZone) : new Date(start.time + 60_000L * random.nextInt(4 * 60))

            UnavailableRuleTraitStub rule = new UnavailableRuleTraitStub(startDateTime: start, endDateTime: end, recurrenceFrequency: frequency, allDay: allDay)
            switch (random.nextInt(4)) {
                case 0:
                    rule.untilDateTime = new Date(start.time + (long) (random.nextDouble() * 20 * 365 * 24 * 3600_000L))
                    break
                case 1:
                    rule.repetitionCount = random.nextInt(maxRepeats[frequency])
                    break
            }

            Calendar sessionStart = Calendar.getInstance(zone)
            sessionStart.setTime(start)
            int repeats = random.nextInt(maxRepeats[frequency])
            switch (frequency) {
                case HOUR_CHOICE: sessionStart.add(Calendar.HOUR_OF_DAY, repeats); break
                case DAY_CHOICE: sessionStart.add(Calendar.DAY_OF_YEAR, repeats); break
                case WEEK_CHOICE: sessionStart.add(Calendar.DAY_OF_YEAR, 7 * repeats); break
                case MONTH_CHOICE: sessionStart.add(Calendar.MONTH, repeats); break
                case YEAR_CHOICE: sessionStart.add(Calendar.YEAR, repeats); break
            }
            sessionStart.add(Calendar.MINUTE, random.nextInt(8 * 60) - 4 * 60)
            Date sessionFrom = sessionStart.time
            Date sessionTo = new Date(sessionFrom.time + 60_000L * random.nextInt(3 * 60))

            boolean expected = isClashedStepByStep(rule, sessionFrom, sessionTo, zone)
            Assertions.assertEquals(expected, rule.isClashed(sessionFrom, sessionTo, zone),
                    "${rule} ${zone.ID} session ${sessionFrom} - ${sessionTo}".toString())
            clashes += expected ? 1 : 0
        }

        // both outcomes are covered
        Assertions.assertTrue(clashes > cases / 10, "${clashes} clashes".toString())
        Assertions.assertTrue(clashes < cases * 9 / 10, "${clashes} clashes".toString())
    }

    private static Date date(String dateTime, TimeZone zone) {
        return date(LocalDateTime.parse(dateTime), zone)
    }

    private static Date date(LocalDateTime dateTime, TimeZone zone) {
        return Date.from(dateTime.atZone(ZoneId.of(zone.ID)).toInstant())
    }

    /**
     * {@link UnavailableRuleTrait#isClashed(Date, Date, TimeZone)} without skipping repeats.
     */
    private static boolean isClashedStepByStep(UnavailableRuleTraitStub rule, Date sessionStart, Date sessionEnd, TimeZone timeZone) {
        Date now = new Date()
        boolean allDay = rule.allDay
        SessionRepetitionType frequency = rule.recurrenceFrequency
        TimeZone ruleZone = allDay ? timeZone : TimeZone.getDefault()
        Date holidayStart = allDay ? startOfDay(rule.startDateTime, timeZone) : rule.startDateTime
        Date holidayEnd = allDay ? shift(startOfDay(rule.endDateTime, timeZone), timeZone, new TimeDuration(0, 23, 59, 0, 0)) : rule.endDateTime

        if (holidayStart > sessionEnd) {
            return false
        }
        Date until
        if (rule.untilDateTime != null) {
            until = allDay ? startOfDay(rule.untilDateTime, timeZone) : rule.untilDateTime
        } else if (rule.repetitionCount != null) {
            Date lastEnd = allDay ? startOfDay(rule.endDateTime, timeZone) : rule.endDateTime
            until = shift(lastEnd, ruleZone, repeatInterval(frequency, rule.repetitionCount))
        } else {
            until = sessionEnd
        }
        if (until < now || until < sessionStart) {
            return false
        }

        BaseDuration step = repeatInterval(frequency, 1)
        BaseDuration intervalDuration = TimeCategory.minus(holidayEnd, holidayStart)
        Date currentStart = holidayStart
        Date currentEnd = holidayEnd
        boolean clash = false
        while (currentStart <= until && !clash) {
            clash = (sessionStart >= currentStart && sessionStart <= currentEnd) ||
                    (sessionEnd >= currentStart && sessionEnd <= currentEnd) ||
                    (sessionStart <= currentStart && sessionEnd >= currentEnd) ||
                    (sessionStart >= currentStart && sessionEnd <= currentEnd)
            currentStart = shift(currentStart, ruleZone, step)
            currentEnd = shift(currentStart, ruleZone, intervalDuration)
        }
        return clash
    }

    private static BaseDuration repeatInterval(SessionRepetitionType frequency, Integer count) {
        switch (frequency) {
            case HOUR_CHOICE: return TimeCategory.getHours(count)
            case DAY_CHOICE: return TimeCategory.getDays(count)
            case WEEK_CHOICE: return TimeCategory.getWeeks(count)
            case MONTH_CHOICE: return TimeCategory.getMonths(count)
            default: return TimeCategory.getYears(count)
        }
    }

    private static Date shift(Date date, TimeZone timeZone, BaseDuration duration) {
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

    private static Date startOfDay(Date date, TimeZone timeZone) {
        Calendar saved = Calendar.getInstance()
        saved.setTime(date)
        Calendar day = Calendar.getInstance(timeZone)
        day.clear()
        day.set(saved.get(Calendar.YEAR), saved.get(Calendar.MONTH), saved.get(Calendar.DAY_OF_MONTH))
        return day.getTime()
    }
}

/**
 * A rule outside the database.
 */
@ToString(includeNames = true, ignoreNulls = true)
class UnavailableRuleTraitStub implements UnavailableRuleTrait {
    Date startDateTime
    Date endDateTime
    Boolean allDay = false
    SessionRepetitionType recurrenceFrequency
    Date untilDateTime
    Integer repetitionCount
}
