package ish.oncourse.server.api

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.SearchRequestDTO
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.service.impl.TimetableApiImpl
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDateTime

@CompileStatic
class TimetableApiTest extends CayenneIshTestCase {

    TimetableApiImpl api

    @BeforeEach
    void before() {
        wipeTables()
        InputStream st = TimetableApiTest.classLoader.getResourceAsStream('ish/oncourse/server/api/TimetableApiTest.xml')
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)

        api = new TimetableApiImpl()
        api.cayenneService = injector.getInstance(ICayenneService)
        api.aql = injector.getInstance(AqlService)
    }


    
    @Test
    void timetableTest() {

        LocalDateTime from
        LocalDateTime to
        //april's sessions
        from = LocalDateTime.parse("2019-04-01T00:00:00")
        to = LocalDateTime.parse("2019-05-01T00:00:00")


        List<SessionDTO> sessions = api.find(new SearchRequestDTO(from: from, to: to))

        assertArrayEquals(((201..209l) + [211l, 210l, 212l, 213l]).toArray(), sessions.collect { it.id }.toArray())

        Assertions.assertEquals("2019-04-01T01:30", sessions[0].start.toString())
        Assertions.assertEquals("2019-04-01T02:30:59", sessions[0].end.toString())
        Assertions.assertEquals("2019-04-11T07:30", sessions[12].start.toString())
        Assertions.assertEquals("2019-04-11T08:30:59", sessions[12].end.toString())


        //March sessions
        from = LocalDateTime.parse("2019-03-01T00:00:00")
        to = LocalDateTime.parse("2019-04-01T00:00:00")

        sessions = api.find(new SearchRequestDTO(from: from, to: to))

        assertArrayEquals([199l, 200l].toArray(), sessions.collect { it.id }.toArray())

        Assertions.assertEquals("2019-03-30T11:30", sessions[0].start.toString())
        Assertions.assertEquals("2019-03-30T12:59:59", sessions[0].end.toString())
        Assertions.assertEquals("2019-03-31T11:30", sessions[1].start.toString())
        Assertions.assertEquals("2019-03-31T12:59:59", sessions[1].end.toString())

        //random date range (60 days)

        from = LocalDateTime.parse("2019-03-10T00:00:00")
        to = LocalDateTime.parse("2019-05-10T00:00:00")
        sessions = api.find(new SearchRequestDTO(from: from, to: to))

        assertArrayEquals([199l, 200l, 201l, 202l, 203l, 204l, 205l, 206l,
                           207l, 208l, 209l, 211l, 210l, 212l, 213l,
                           224l, 225l, 226l, 227l, 228l, 229l, 230l].toArray(), sessions.collect { it.id }.toArray())


        Assertions.assertEquals("2019-05-09T12:30", sessions[21].start.toString())
        Assertions.assertEquals("2019-05-09T13:59:59", sessions[21].end.toString())
    }

    
    @Test
    void filterTest() {

        LocalDateTime from = LocalDateTime.parse("2019-03-10T00:00:00")
        LocalDateTime to = LocalDateTime.parse("2019-05-10T00:00:00")
        List<SessionDTO> sessions = api.find(new SearchRequestDTO(from: from, to: to, search: 'room.id=200'))
        assertArrayEquals([199l].toArray(), sessions.collect { it.id }.toArray())

        sessions = api.find(new SearchRequestDTO(from: from, to: to, search: 'room.id=201'))
        assertArrayEquals([200l].toArray(), sessions.collect { it.id }.toArray())

        sessions = api.find(new SearchRequestDTO(from: from, to: to, search: 'room.site.id=200'))
        assertArrayEquals([199l, 200l].toArray(), sessions.collect { it.id }.toArray())

        sessions = api.find(new SearchRequestDTO(from: from, to: to, search: 'room.site.id=201'))
        assertArrayEquals([201l, 202l].toArray(), sessions.collect { it.id }.toArray())
    }


    
    @Test
    void getByIdTest() {

        List<SessionDTO> sessions = api.get('199,213')

        Assertions.assertEquals(199l, sessions[0].id)
        Assertions.assertEquals(LocalDateTime.parse("2019-03-30T11:30:00"), sessions[0].start)
        Assertions.assertEquals(LocalDateTime.parse("2019-03-30T12:59:59"), sessions[0].end)
        Assertions.assertEquals("Adobe flash player", sessions[0].name)
        Assertions.assertEquals("ADOBE-1", sessions[0].code)
        Assertions.assertEquals("Test Room1", sessions[0].room)
        Assertions.assertEquals("Default site", sessions[0].site)
        assertArrayEquals(["John Smith", "Jim Tramp"].toArray(), sessions[0].tutors.toArray())

        Assertions.assertEquals(213l, sessions[1].id)
        Assertions.assertEquals(LocalDateTime.parse("2019-04-11T07:30:00"), sessions[1].start)
        Assertions.assertEquals(LocalDateTime.parse("2019-04-11T08:30:59"), sessions[1].end)
        Assertions.assertEquals("Adobe flash player", sessions[1].name)
        Assertions.assertEquals("ADOBE-2", sessions[1].code)
        Assertions.assertEquals("Test Room5", sessions[1].room)
        Assertions.assertEquals("Default site3", sessions[1].site)
        assertArrayEquals(["Tom Davis", "Kim Chen"].toArray(), sessions[1].tutors.toArray())

    }


    
    @Test
    void getDateTest() {

        assertArrayEquals([30, 31].toArray(), api.getDates(2, 2019, null).toArray())
        assertArrayEquals([30].toArray(), api.getDates(2, 2019, 'room.id=200').toArray())
        assertArrayEquals([30, 31].toArray(), api.getDates(2, 2019, 'room.site.id=200').toArray())

        assertArrayEquals((1..11).toArray(), api.getDates(3, 2019, null).toArray())
        assertArrayEquals([11].toArray(), api.getDates(3, 2019, 'room.id=204').toArray())
        assertArrayEquals([11].toArray(), api.getDates(3, 2019, 'room.site.id=203').toArray())

        assertArrayEquals([1, 2, 3, 4, 5, 8, 9, 10, 30].toArray(), api.getDates(4, 2019, null).toArray())

        assertArrayEquals([].toArray(), api.getDates(5, 2019, null).toArray())

    }
}
