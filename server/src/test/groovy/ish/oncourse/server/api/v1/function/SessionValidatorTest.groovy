/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.api.v1.model.ClashTypeDTO
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.model.SessionWarningDTO
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import java.time.LocalDateTime
@CompileStatic
class SessionValidatorTest extends CayenneIshTestCase {

    private SessionValidator validator


    @Before
    void before(){
        wipeTables()
        InputStream st = SessionValidatorTest.classLoader.getResourceAsStream('ish/oncourse/server/api/v1/function/SessionValidatorTest.xml')
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)

        validator  = injector.getInstance(SessionValidator)

    }

    @Test
    void testTutor() {
        SessionDTO dto = new SessionDTO().with {it ->
                    it.temporaryId = 'tmp'
                    it.contactIds = [1l]
                    it.courseId = 200l
                    it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
                    it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
                    it
                }

        List<SessionWarningDTO> warnings =  validator.validate([dto], -1l)

        //single tutor clash
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n', warnings[0].message)

        //both tutors clash
        dto.contactIds << 2l

        warnings =  validator.validate([dto], -1l)

        assertEquals(2, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n', warnings[0].message)
        assertEquals(ClashTypeDTO.TUTOR, warnings[1].type)
        assertEquals(2l, warnings[1].referenceId)
        assertEquals('Jim Tramp is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n', warnings[1].message)


        //single tutor clash at other day
        dto.start = LocalDateTime.parse('2120-12-04T00:15:00') //UTC time
        dto.end = LocalDateTime.parse('2120-12-04T01:15:00') //UTC time

        warnings =  validator.validate([dto], -1l)

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)


        //partial time overlap - end date time
        // new session                       |--------------|
        // existed session           |--------------|
        dto.start = LocalDateTime.parse('2120-12-04T00:45:00') //UTC time
        dto.end = LocalDateTime.parse('2120-12-04T01:45:00') //UTC time

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)


        //partial time overlap - start date time
        // new session        |--------------|
        // existed session           |--------------|
        dto.start = LocalDateTime.parse('2120-12-04T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-12-04T01:00:00') //UTC time

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)


        //all time overlap
        // new session        |----------------------------|
        // existed session           |--------------|
        dto.start = LocalDateTime.parse('2120-12-04T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-12-04T02:00:00') //UTC time

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)

        //all time overlap
        // new session                    |-----|
        // existed session           |--------------|
        dto.start = LocalDateTime.parse('2120-12-04T00:30:00') //UTC time
        dto.end = LocalDateTime.parse('2120-12-04T01:00:00') //UTC time

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)

    }

    @Test
    void testRoom() {
        SessionDTO dto = new SessionDTO().with {it ->
            it.temporaryId = 'tmp'
            it.roomId = 200l
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }

        //simple room clash
        List<SessionWarningDTO> warnings =  validator.validate([dto], -1l)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        assertEquals(200l, warnings[0].referenceId)
        assertEquals('Room Test Room1 is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n', warnings[0].message)

        //new session overlap two existed session by room criteria
        dto.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
        dto.end = LocalDateTime.parse('2120-12-04T01:15:00') //UTC time

        warnings =  validator.validate([dto], -1l)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        assertEquals(200l, warnings[0].referenceId)
        assertEquals('Room Test Room1 is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n' +
                'ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)

        //class by room and tutors criteria

        dto.contactIds = [1l, 2l]
        warnings =  validator.validate([dto], -1l)
        assertEquals(3, warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals(1l, warnings[0].referenceId)
        assertEquals('John Smith is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n' +
                'ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[0].message)

        assertEquals(ClashTypeDTO.TUTOR, warnings[1].type)
        assertEquals(2l, warnings[1].referenceId)
        assertEquals('Jim Tramp is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n', warnings[1].message)

        assertEquals(ClashTypeDTO.ROOM, warnings[2].type)
        assertEquals(200l, warnings[2].referenceId)
        assertEquals('Room Test Room1 is already booked for ADOBE-1 at Tue 3 Dec 3:15(Europe/Minsk) \n' +
                'ADOBE-1 at Wed 4 Dec 3:15(Europe/Minsk) \n', warnings[2].message)

    }

    @Test
    void testSessionUpdate() {
        SessionDTO dto1 = new SessionDTO().with {it ->
            it.id = 199
            it.roomId = 200l
            it.courseId = 200l
            it.contactIds = [1l, 2l]
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }

        SessionDTO dto2 = new SessionDTO().with {it ->
            it.id = 200
            it.roomId = 200l
            it.courseId = 200l
            it.contactIds = [1l, 2l]
            it.start = LocalDateTime.parse('2120-12-04T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-04T01:15:00') //UTC time
            it
        }

        List<SessionWarningDTO>  warnings =  validator.validate([dto1, dto2], 200l)
        assertEquals(0, warnings.size())

        SessionDTO dto3 = new SessionDTO().with {it ->
            it.temporaryId = 'tmp'
            it.roomId = 200l
            it.courseId = 200l
            it.contactIds = [1l, 2l]
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }
        warnings =  validator.validate([dto1, dto2, dto3], 200l)

        assertEquals(2, warnings.size())
        assertEquals(ClashTypeDTO.SESSION, warnings[0].type)
        assertEquals(199, warnings[0].sessionId)

        assertEquals(ClashTypeDTO.SESSION, warnings[1].type)
        assertEquals('tmp', warnings[1].temporaryId)



        dto3.start = LocalDateTime.parse('2120-12-04T01:15:00') //UTC time
        dto3.end = LocalDateTime.parse('2120-12-04T02:15:00') //UTC time
        warnings =  validator.validate([dto1, dto2, dto3], 200l)

        assertEquals(2, warnings.size())
        assertEquals(ClashTypeDTO.SESSION, warnings[0].type)
        assertEquals(200, warnings[0].sessionId)

        assertEquals(ClashTypeDTO.SESSION, warnings[1].type)
        assertEquals('tmp', warnings[1].temporaryId)
    }



    @Test
    void testUnavailableRule() {
        SessionDTO dto = new SessionDTO().with {it ->
            it.temporaryId = 'tmp'
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-01-01T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-01-01T01:15:00') //UTC time
            it
        }

        List<SessionWarningDTO>  warnings =  validator.validate([dto], -1)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: new year \n', warnings[0].message)


        //try next year
        dto.start = LocalDateTime.parse('2121-01-01T00:15:00')
        dto.end = LocalDateTime.parse('2121-01-01T00:15:00') //UTC time
        warnings =  validator.validate([dto], -1)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: new year \n', warnings[0].message)

        //try date in past
        dto.start = LocalDateTime.parse('2019-01-01T00:15:00')
        dto.end = LocalDateTime.parse('2019-01-01T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)
        assertEquals(0, warnings.size())

        //dayly rule
        dto.start = LocalDateTime.parse('2100-01-01T00:15:00')
        dto.end = LocalDateTime.parse('2100-01-01T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: new year \n' +
                'lunch \n', warnings[0].message)


        //try next day
        dto.start = LocalDateTime.parse('2100-01-02T00:15:00')
        dto.end = LocalDateTime.parse('2100-01-02T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: lunch \n', warnings[0].message)

        //try after rule finished
        dto.start = LocalDateTime.parse('2101-01-02T00:15:00')
        dto.end = LocalDateTime.parse('2101-01-02T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(0, warnings.size())

        //weekly event repeat 3 times
        dto.start = LocalDateTime.parse('2120-03-01T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-01T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: weekly virus disinfection \n', warnings[0].message)

        //try next 2 weeks
        dto.start = LocalDateTime.parse('2120-03-08T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-08T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: weekly virus disinfection \n', warnings[0].message)

        dto.start = LocalDateTime.parse('2120-03-15T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-15T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Whole of business busy: weekly virus disinfection \n', warnings[0].message)

        //try intermidiate week day
        dto.start = LocalDateTime.parse('2120-03-14T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-14T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(0, warnings.size())


        //try after repetition is finished
        dto.start = LocalDateTime.parse('2120-03-16T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-16T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(0, warnings.size())

        dto.start = LocalDateTime.parse('2120-03-23T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-23T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)

        assertEquals(0, warnings.size())
    }

    @Test
    void testRelatedUnavailableRule() {
        SessionDTO dto = new SessionDTO().with {it ->
            it.temporaryId = 'tmp'
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-02-01T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-02-01T01:15:00') //UTC time
            it
        }
        //course unavailability
        List<SessionWarningDTO>  warnings =  validator.validate([dto], -1)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        assertEquals('Course Adobe flash player is busy: Course unavalible \n', warnings[0].message)


        dto.start = LocalDateTime.parse('2120-02-02T00:15:00') //UTC time
        dto.end = LocalDateTime.parse('2120-02-02T01:15:00') //UTC time
        warnings =  validator.validate([dto], -1)
        assertEquals(0, warnings.size())

        //tutor unavailability
        dto.contactIds = [3l]
        warnings =  validator.validate([dto], -1)
        assertEquals(1,  warnings.size())
        assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        assertEquals('Tom Davis is busy: free time \n', warnings[0].message)

        //push session after unavailability
        dto.start = LocalDateTime.parse('2120-02-02T01:15:00') //UTC time
        dto.end = LocalDateTime.parse('2120-02-02T02:15:00') //UTC time
        warnings =  validator.validate([dto], -1)
        assertEquals(0,  warnings.size())

        //room unavailability
        dto.start = LocalDateTime.parse('2120-07-01T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-07-01T02:00:00') //UTC time
        dto.roomId = 203
        warnings =  validator.validate([dto], -1)
        assertEquals(1,  warnings.size())
        assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        assertEquals('Room Test Room4 is busy: Room cleaning \n', warnings[0].message)

        //push session after room unavailability
        dto.start = LocalDateTime.parse('2120-02-02T02:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-02-02T03:00:00') //UTC time
        warnings =  validator.validate([dto], -1)
        assertEquals(0,  warnings.size())



    }

    @Test
    void testTwoDaysRule() {
        SessionDTO dto = new SessionDTO().with { it ->
            it.temporaryId = 'tmp'
            it.courseId = 200l
            it.roomId = 204
            it.start = LocalDateTime.parse('2120-01-06T00:00:00') //UTC time
            it.end = LocalDateTime.parse('2120-01-06T03:00:00')//UTC time
            it
        }
        //weekly site unavailability - weekends
        List<SessionWarningDTO> warnings = validator.validate([dto], -1)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.SITE, warnings[0].type)
        assertEquals('Site Default site3 is busy: Weekends \n', warnings[0].message)

        //Sunday beasy as well
        dto.start = LocalDateTime.parse('2120-01-07T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-01-07T03:00:00')
        warnings = validator.validate([dto], -1)
        assertEquals(1, warnings.size())
        assertEquals(ClashTypeDTO.SITE, warnings[0].type)
        assertEquals('Site Default site3 is busy: Weekends \n', warnings[0].message)

        //Mon free
        dto.start = LocalDateTime.parse('2120-01-08T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-01-08T03:00:00')
        warnings = validator.validate([dto], -1)
        assertEquals(0, warnings.size())
    }

}
