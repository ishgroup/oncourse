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
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.access.translator.ParameterBinding
import org.apache.cayenne.log.JdbcEventLogger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate


@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/timetable/TimetableClashServiceQueriesTest.xml")
class TimetableClashServiceQueriesTest extends TestWithDatabase {

    private TimetableClashService service

    @BeforeEach
    void setupService() {
        service = injector.getInstance(TimetableClashService)
    }

    @Test
    void testQueriesDoNotGrowWithSessions() {
        List<TimetableClash> oneDay = []
        int oneDayQueries = countQueries {
            oneDay = service.check(LocalDate.parse('2121-02-01'), LocalDate.parse('2121-02-01'), null)
        }
        List<TimetableClash> twentyDays = []
        int twentyDaysQueries = countQueries {
            twentyDays = service.check(LocalDate.parse('2121-02-01'), LocalDate.parse('2121-02-20'), null)
        }

        Assertions.assertEquals([
                '2121-02-01 ROOM 1001/2001 Room S1',
                '2121-02-01 TUTOR 1001/2001 John Smith',
                '2121-02-01 TUTOR_UNAVAILABLE 1001 Jane Doe: Tutor break',
                '2121-02-01 ROOM_UNAVAILABLE 1001 Room S1: Room cleaning',
                '2121-02-01 SITE_UNAVAILABLE 1001 Sydney campus: Campus drill',
                '2121-02-01 COURSE_UNAVAILABLE 1001 Clash course: Course review',
                '2121-02-01 ROOM_UNAVAILABLE 2001 Room S1: Room cleaning',
                '2121-02-01 SITE_UNAVAILABLE 2001 Sydney campus: Campus drill',
                '2121-02-01 COURSE_UNAVAILABLE 2001 Clash course: Course review',
        ], oneDay.collect { describe(it) })
        Assertions.assertEquals(20 * oneDay.size(), twentyDays.size())
        Assertions.assertEquals('2121-02-20 COURSE_UNAVAILABLE 2020 Clash course: Course review', describe(twentyDays.last()))

        // 40 sessions with 60 rosters need as many queries as 2 sessions with 3 rosters
        Assertions.assertEquals(oneDayQueries, twentyDaysQueries)
    }

    @Test
    void testSameClassSessionsWithTheSameTutorAreOneClash() {
        // 612 starts first although its id is higher
        Assertions.assertEquals([
                '2121-03-01 SESSION 612/611 CLASH-1',
        ], service.check(LocalDate.parse('2121-03-01'), LocalDate.parse('2121-03-01'), null).collect { describe(it) })
    }

    @Test
    void testSessionClashIsListedForTheSessionThatStartsFirst() {
        Assertions.assertEquals([
                '2121-03-02 SESSION 622/621 CLASH-1',
        ], service.check(LocalDate.parse('2121-03-02'), LocalDate.parse('2121-03-03'), null).collect { describe(it) })

        // when the other session is outside the range, the clash is listed for the checked one
        Assertions.assertEquals([
                '2121-03-03 SESSION 621/622 CLASH-1',
        ], service.check(LocalDate.parse('2121-03-03'), LocalDate.parse('2121-03-03'), null).collect { describe(it) })
    }

    private int countQueries(Closure action) {
        DataNode node = getCayenneService().serverRuntime.dataDomain.defaultNode
        JdbcEventLogger logger = node.jdbcEventLogger
        QueryCountingLogger counter = new QueryCountingLogger(logger)
        node.jdbcEventLogger = counter
        try {
            action.call()
        } finally {
            node.jdbcEventLogger = logger
        }
        return counter.queries
    }

    private static String describe(TimetableClash clash) {
        String sessions = clash.otherSession ? "${clash.session.sessionId}/${clash.otherSession.sessionId}" : "${clash.session.sessionId}"
        String rule = clash.ruleDescription ? ": ${clash.ruleDescription}" : ''
        return "${clash.date} ${clash.type} ${sessions} ${clash.referenceName}${rule}".toString()
    }
}

/**
 * Counts the SQL queries Cayenne runs.
 */
@CompileStatic
class QueryCountingLogger implements JdbcEventLogger {

    @Delegate
    private final JdbcEventLogger delegate

    int queries

    QueryCountingLogger(JdbcEventLogger delegate) {
        this.delegate = delegate
    }

    @Override
    void logQuery(String sql, ParameterBinding[] bindings) {
        queries++
        delegate.logQuery(sql, bindings)
    }
}
