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

package ish.oncourse.server.timetable

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.api.v1.function.SessionValidator
import ish.oncourse.server.api.v1.model.ClashTypeDTO
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.model.SessionWarningDTO
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.UnavailableRule
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.LocalDateTime

import static ish.oncourse.server.timetable.TimetableClashType.*

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/timetable/TimetableClashServiceTest.xml")
class TimetableClashServiceTest extends TestWithDatabase {

    private static final LocalDate DECEMBER_START = LocalDate.parse('2120-12-01')
    private static final LocalDate DECEMBER_END = LocalDate.parse('2120-12-31')

    private TimetableClashService service

    @BeforeEach
    void setupService() {
        service = injector.getInstance(TimetableClashService)
    }

    @Test
    void testAllClashesInRangeSortedByDate() {
        List<TimetableClash> clashes = service.check(DECEMBER_START, DECEMBER_END, null)

        Assertions.assertEquals([
                '2120-12-02 ROOM 101/201 Room S1',
                '2120-12-02 TUTOR 101/201 John Smith',
                '2120-12-03 SESSION 102/103 CLASH-1',
                '2120-12-05 TUTOR_UNAVAILABLE 105 Jane Doe: Annual leave',
                '2120-12-06 ROOM_UNAVAILABLE 106 Room S2: Maintenance',
                '2120-12-10 ROOM 110/210 Room S2',
                '2120-12-10 ROOM 110/211 Room S2',
                '2120-12-10 ROOM 110/212 Room S2',
                '2120-12-10 ROOM 110/213 Room S2',
                '2120-12-12 HOLIDAY 250 Staff meeting: Staff meeting',
                '2120-12-16 HOLIDAY 121 College closure: College closure',
                '2120-12-25 HOLIDAY 120 Christmas Day: Christmas Day',
                '2120-12-25 HOLIDAY 401 Christmas Day: Christmas Day',
                '2120-12-31 ROOM 254/404 Room P1',
        ], clashes.collect { describe(it) })
    }

    @Test
    void testRoomClashDetails() {
        TimetableClash clash = service.check(LocalDate.parse('2120-12-02'), LocalDate.parse('2120-12-02'), null)
                .find { it.type == ROOM }

        Assertions.assertEquals(LocalDate.parse('2120-12-02'), clash.date)
        Assertions.assertEquals(1L, clash.referenceId)
        Assertions.assertEquals('Room S1', clash.referenceName)
        Assertions.assertNull(clash.ruleId)

        Assertions.assertEquals(101L, clash.session.sessionId)
        Assertions.assertEquals(1L, clash.session.classId)
        Assertions.assertEquals('CLASH-1', clash.session.classCode)
        Assertions.assertEquals('Clash course', clash.session.courseName)
        Assertions.assertEquals(1L, clash.session.roomId)
        Assertions.assertEquals('Room S1', clash.session.roomName)
        Assertions.assertEquals(1L, clash.session.siteId)
        Assertions.assertEquals('Sydney campus', clash.session.siteName)
        Assertions.assertEquals('Australia/Sydney', clash.session.timeZone)
        Assertions.assertEquals(LocalDateTime.parse('2120-12-02T10:00'), clash.session.start)
        Assertions.assertEquals(LocalDateTime.parse('2120-12-02T12:00'), clash.session.end)

        Assertions.assertEquals(201L, clash.otherSession.sessionId)
        Assertions.assertEquals(2L, clash.otherSession.classId)
        Assertions.assertEquals('CLASH-2', clash.otherSession.classCode)
        Assertions.assertEquals(LocalDateTime.parse('2120-12-02T11:00'), clash.otherSession.start)
    }

    @Test
    void testSessionTimesAndRangeInSiteTimeZone() {
        List<TimetableClash> clashes = service.check(DECEMBER_START, DECEMBER_END, 2L)

        // Perth sessions on 30 Nov (1 Dec in Sydney) are out of range, on 31 Dec (1 Jan in Sydney) are in range,
        // and Christmas Day is the 25th in Perth, not in Sydney
        Assertions.assertEquals([
                '2120-12-25 HOLIDAY 401 Christmas Day: Christmas Day',
                '2120-12-31 ROOM 254/404 Room P1',
        ], clashes.collect { describe(it) })

        TimetableClashSession session = clashes[0].session
        Assertions.assertEquals('Perth campus', session.siteName)
        Assertions.assertEquals('Australia/Perth', session.timeZone)
        Assertions.assertEquals(LocalDateTime.parse('2120-12-25T22:00'), session.start)
        Assertions.assertEquals(LocalDateTime.parse('2120-12-25T23:00'), session.end)
        Assertions.assertEquals(1L, clashes[0].ruleId)
        Assertions.assertEquals('Christmas Day', clashes[0].ruleDescription)

        Assertions.assertEquals([
                '2120-11-30 ROOM 253/403 Room P1',
        ], service.check(LocalDate.parse('2120-11-30'), LocalDate.parse('2120-11-30'), 2L).collect { describe(it) })
    }

    @Test
    void testHolidayDaysInSiteTimeZone() {
        ObjectContext context = cayenneService.newContext
        UnavailableRule christmas = SelectById.query(UnavailableRule, 1L).selectOne(context)
        Session perthChristmasEvening = SelectById.query(Session, 401L).selectOne(context)
        Session perthChristmasEve = SelectById.query(Session, 402L).selectOne(context)
        TimeZone perth = TimeZone.getTimeZone('Australia/Perth')

        // server time zone (Sydney): what the class timetable checks
        Assertions.assertFalse(christmas.isClashed(perthChristmasEvening.startDatetime, perthChristmasEvening.endDatetime))
        Assertions.assertTrue(christmas.isClashed(perthChristmasEve.startDatetime, perthChristmasEve.endDatetime))

        // site time zone
        Assertions.assertTrue(christmas.isClashed(perthChristmasEvening.startDatetime, perthChristmasEvening.endDatetime, perth))
        Assertions.assertFalse(christmas.isClashed(perthChristmasEve.startDatetime, perthChristmasEve.endDatetime, perth))
    }

    @Test
    void testTimetableValidationKeepsItsLimit() {
        SessionDTO dto = new SessionDTO().with { it ->
            it.id = 110L
            it.roomId = 2L
            it.courseId = 1L
            it.start = LocalDateTime.parse('2120-12-09T22:00:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-10T06:00:00') //UTC time
            it
        }

        List<SessionWarningDTO> warnings = injector.getInstance(SessionValidator).validate([dto], 1L)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        Assertions.assertEquals('Clash for Room Room S2 with class CLASH-2 \nCLASH-2 \nCLASH-2 \n', warnings[0].message)

        Assertions.assertEquals(4, service.check(LocalDate.parse('2120-12-10'), LocalDate.parse('2120-12-10'), null).size())
    }

    @Test
    void testFinishedSessionsAreNotChecked() {
        Assertions.assertEquals([], service.check(LocalDate.parse('2019-04-01'), LocalDate.parse('2019-04-30'), null))
    }

    @Test
    void testInvalidRange() {
        Assertions.assertThrows(IllegalArgumentException) {
            service.check(DECEMBER_END, DECEMBER_START, null)
        }
        Assertions.assertThrows(IllegalArgumentException) {
            service.check(null, DECEMBER_END, null)
        }
    }

    private static String describe(TimetableClash clash) {
        String sessions = clash.otherSession ? "${clash.session.sessionId}/${clash.otherSession.sessionId}" : "${clash.session.sessionId}"
        String rule = clash.ruleDescription ? ": ${clash.ruleDescription}" : ''
        return "${clash.date} ${clash.type} ${sessions} ${clash.referenceName}${rule}".toString()
    }
}
