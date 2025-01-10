/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.api.v1.model.ClashTypeDTO
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.model.SessionWarningDTO
import ish.oncourse.server.api.v1.model.TutorAttendanceDTO
import ish.oncourse.server.cayenne.TutorAttendance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDateTime

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/api/v1/function/SessionValidatorTest.xml")
class SessionValidatorTest extends TestWithDatabase {

    private SessionValidator validator

    @BeforeEach
    void students() {
        validator = injector.getInstance(SessionValidator)
    }
    
    private static TutorAttendanceDTO createAttendance(String start, String end, Long contactId) {
         new TutorAttendanceDTO().with {it ->
            it.start = LocalDateTime.parse(start)
            it.end = LocalDateTime.parse(end)
            it.contactId = contactId
            it
        }
    }
    
    private static void changeSessionTime(SessionDTO dto, String start, String end) {
        LocalDateTime startDate = LocalDateTime.parse(start)
        LocalDateTime endDate = LocalDateTime.parse(end)
        dto.start = startDate
        dto.end = endDate
        dto.tutorAttendances.each {
            it.start = startDate
            it.end = endDate
        }
    }
    
    @Test
    void testTutor() {
        SessionDTO dto = new SessionDTO().with { it ->
            it.temporaryId = 'tmp'
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }

        dto.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-03T01:15:00', 1l)
        
        
        List<SessionWarningDTO> warnings = validator.validate([dto], -1l)

        //single tutor clash
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)

        //both tutors clash
        dto.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-03T01:15:00', 2l)

        warnings = validator.validate([dto], -1l)

        Assertions.assertEquals(2, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[1].type)
        Assertions.assertEquals(2l, warnings[1].referenceId)
        Assertions.assertEquals('Clash for Jim Tramp with class ADOBE-1 \n', warnings[1].message)


        //single tutor clash at other day
        changeSessionTime(dto, '2120-12-04T00:15:00', '2120-12-04T01:15:00')

        warnings = validator.validate([dto], -1l)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)


        //partial time overlap - end date time
        // new session                       |--------------|
        // existed session           |--------------|

        changeSessionTime(dto, '2120-12-04T00:45:00', '2120-12-04T01:45:00')

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)


        //partial time overlap - start date time
        // new session        |--------------|
        // existed session           |--------------|
        changeSessionTime(dto, '2120-12-04T00:00:00', '2120-12-04T01:00:00')

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)


        //all time overlap
        // new session        |----------------------------|
        // existed session           |--------------|
        changeSessionTime(dto, '2120-12-04T00:00:00', '2120-12-04T02:00:00')

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)

        //all time overlap
        // new session                    |-----|
        // existed session           |--------------|
        
        changeSessionTime(dto, '2120-12-04T00:30:00', '2120-12-04T01:00:00')

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n', warnings[0].message)

    }

    @Test
    void testRoom() {
        SessionDTO dto = new SessionDTO().with { it ->
            it.temporaryId = 'tmp'
            it.roomId = 200l
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }

        //simple room clash
        List<SessionWarningDTO> warnings = validator.validate([dto], -1l)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        Assertions.assertEquals(200l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for Room Test Room1 with class ADOBE-1 \n', warnings[0].message)
        
        //new session overlap two existed session by room criteria
        changeSessionTime(dto, '2120-12-03T00:15:00','2120-12-04T01:15:00')
        
        warnings = validator.validate([dto], -1l)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        Assertions.assertEquals(200l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for Room Test Room1 with class ADOBE-1 \n' +
                'ADOBE-1 \n', warnings[0].message)

        //class by room and tutors criteria
        dto.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-04T01:15:00', 1l)
        dto.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-04T01:15:00', 2l)
        
        warnings = validator.validate([dto], -1l)
        Assertions.assertEquals(3, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals(1l, warnings[0].referenceId)
        Assertions.assertEquals('Clash for John Smith with class ADOBE-1 \n' +
                'ADOBE-1 \n', warnings[0].message)

        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[1].type)
        Assertions.assertEquals(2l, warnings[1].referenceId)
        Assertions.assertEquals('Clash for Jim Tramp with class ADOBE-1 \n', warnings[1].message)

        Assertions.assertEquals(ClashTypeDTO.ROOM, warnings[2].type)
        Assertions.assertEquals(200l, warnings[2].referenceId)
        Assertions.assertEquals('Clash for Room Test Room1 with class ADOBE-1 \n' +
                'ADOBE-1 \n', warnings[2].message)

    }

    @Test
    void testSessionUpdate() {
        SessionDTO dto1 = new SessionDTO().with { it ->
            it.id = 199
            it.roomId = 200l
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }
        dto1.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-03T01:15:00', 1l)
        dto1.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-03T01:15:00', 2l)

        SessionDTO dto2 = new SessionDTO().with { it ->
            it.id = 200
            it.roomId = 200l
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-12-04T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-04T01:15:00') //UTC time
            it
        }
        dto2.tutorAttendances << createAttendance('2120-12-04T00:15:00', '2120-12-04T01:15:00', 1l)
        dto2.tutorAttendances << createAttendance('2120-12-04T00:15:00', '2120-12-04T01:15:00', 2l)

        List<SessionWarningDTO> warnings = validator.validate([dto1, dto2], 200l)
        Assertions.assertEquals(0, warnings.size())

        SessionDTO dto3 = new SessionDTO().with { it ->
            it.temporaryId = 'tmp'
            it.roomId = 200l
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-12-03T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-12-03T01:15:00') //UTC time
            it
        }
        dto3.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-03T01:15:00', 1l)
        dto3.tutorAttendances << createAttendance('2120-12-03T00:15:00', '2120-12-03T01:15:00', 2l)

        warnings = validator.validate([dto1, dto2, dto3], 200l)

        Assertions.assertEquals(6, warnings.size())
        
        Assertions.assertEquals(ClashTypeDTO.SESSION, warnings[0].type)
        Assertions.assertEquals(199, warnings[0].sessionId)
        Assertions.assertEquals('Class already has session for that time', warnings[0].message)

        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[1].type)
        Assertions.assertEquals(1, warnings[1].referenceId)
        Assertions.assertEquals(199, warnings[1].sessionId)
        Assertions.assertEquals('Tutor already has roster for that time', warnings[1].message)

        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[2].type)
        Assertions.assertEquals(2, warnings[2].referenceId)
        Assertions.assertEquals(199, warnings[2].sessionId)
        Assertions.assertEquals('Tutor already has roster for that time', warnings[2].message)

        Assertions.assertEquals(ClashTypeDTO.SESSION, warnings[3].type)
        Assertions.assertEquals('tmp', warnings[3].temporaryId)
        Assertions.assertEquals('Class already has session for that time', warnings[3].message)

        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[4].type)
        Assertions.assertEquals(1, warnings[4].referenceId)
        Assertions.assertEquals('tmp', warnings[4].temporaryId)
        Assertions.assertEquals('Tutor already has roster for that time', warnings[4].message)

        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[5].type)
        Assertions.assertEquals(2, warnings[5].referenceId)
        Assertions.assertEquals('tmp', warnings[5].temporaryId)
        Assertions.assertEquals('Tutor already has roster for that time', warnings[5].message)

        
        dto3.start = LocalDateTime.parse('2120-12-04T01:15:00') //UTC time
        dto3.end = LocalDateTime.parse('2120-12-04T02:15:00') //UTC time
        changeSessionTime(dto3,'2120-12-04T01:15:00','2120-12-04T02:15:00')
        warnings = validator.validate([dto1, dto2, dto3], 200l)

        // no wornings since we allow to sessions with no gap between them |00:15:00---01:15:00| no gap here |01:15:00---02:15:00| 
        Assertions.assertEquals(0, warnings.size())

    }


    @Test
    void testUnavailableRule() {
        SessionDTO dto = new SessionDTO().with { it ->
            it.temporaryId = 'tmp'
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-01-01T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-01-01T01:15:00') //UTC time
            it
        }

        List<SessionWarningDTO> warnings = validator.validate([dto], -1)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: new year \n', warnings[0].message)


        //try next year
        dto.start = LocalDateTime.parse('2121-01-01T00:15:00')
        dto.end = LocalDateTime.parse('2121-01-01T00:15:00') //UTC time
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: new year \n', warnings[0].message)

        //try date in past
        dto.start = LocalDateTime.parse('2019-01-01T00:15:00')
        dto.end = LocalDateTime.parse('2019-01-01T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(0, warnings.size())

        //dayly rule
        dto.start = LocalDateTime.parse('2100-01-01T00:15:00')
        dto.end = LocalDateTime.parse('2100-01-01T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: new year \n' +
                'lunch \n', warnings[0].message)


        //try next day
        dto.start = LocalDateTime.parse('2100-01-02T00:15:00')
        dto.end = LocalDateTime.parse('2100-01-02T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: lunch \n', warnings[0].message)

        //try after rule finished
        dto.start = LocalDateTime.parse('2101-01-02T00:15:00')
        dto.end = LocalDateTime.parse('2101-01-02T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(0, warnings.size())

        //weekly event repeat 3 times
        dto.start = LocalDateTime.parse('2120-03-01T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-01T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: weekly virus disinfection \n', warnings[0].message)

        //try next 2 weeks
        dto.start = LocalDateTime.parse('2120-03-08T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-08T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: weekly virus disinfection \n', warnings[0].message)

        dto.start = LocalDateTime.parse('2120-03-15T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-15T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Whole of business busy: weekly virus disinfection \n', warnings[0].message)

        //try intermidiate week day
        dto.start = LocalDateTime.parse('2120-03-14T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-14T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(0, warnings.size())


        //try after repetition is finished
        dto.start = LocalDateTime.parse('2120-03-16T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-16T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(0, warnings.size())

        dto.start = LocalDateTime.parse('2120-03-23T00:15:00')
        dto.end = LocalDateTime.parse('2120-03-23T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)

        Assertions.assertEquals(0, warnings.size())
    }

    @Test
    void testRelatedUnavailableRule() {
        SessionDTO dto = new SessionDTO().with { it ->
            it.temporaryId = 'tmp'
            it.courseId = 200l
            it.start = LocalDateTime.parse('2120-02-01T00:15:00') //UTC time
            it.end = LocalDateTime.parse('2120-02-01T01:15:00') //UTC time
            it
        }
        //course unavailability
        List<SessionWarningDTO> warnings = validator.validate([dto], -1)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.UNAVAILABLERULE, warnings[0].type)
        Assertions.assertEquals('Course Adobe flash player is busy: Course unavalible \n', warnings[0].message)


        dto.start = LocalDateTime.parse('2120-02-02T00:15:00') //UTC time
        dto.end = LocalDateTime.parse('2120-02-02T01:15:00') //UTC time
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(0, warnings.size())

        //tutor unavailability
        dto.tutorAttendances << createAttendance('2120-02-02T00:15:00', '2120-02-02T01:15:00', 3l)

        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.TUTOR, warnings[0].type)
        Assertions.assertEquals('Tom Davis is busy: free time \n', warnings[0].message)

        //push session after unavailability
        dto.start = LocalDateTime.parse('2120-02-02T01:15:00') //UTC time
        dto.end = LocalDateTime.parse('2120-02-02T02:15:00') //UTC time
        changeSessionTime(dto,'2120-02-02T01:15:00', '2120-02-02T02:15:00')
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(0, warnings.size())

        //room unavailability
        changeSessionTime(dto,'2120-07-01T00:00:00', '2120-07-01T02:00:00')

        dto.roomId = 203
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.ROOM, warnings[0].type)
        Assertions.assertEquals('Room Test Room4 is busy: Room cleaning \n', warnings[0].message)

        //push session after room unavailability
        changeSessionTime(dto,'2120-02-02T02:00:00', '2120-02-02T03:00:00')

        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(0, warnings.size())
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
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.SITE, warnings[0].type)
        Assertions.assertEquals('Site Default site3 is busy: Weekends \n', warnings[0].message)

        //Sunday beasy as well
        dto.start = LocalDateTime.parse('2120-01-07T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-01-07T03:00:00')
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(1, warnings.size())
        Assertions.assertEquals(ClashTypeDTO.SITE, warnings[0].type)
        Assertions.assertEquals('Site Default site3 is busy: Weekends \n', warnings[0].message)

        //Mon free
        dto.start = LocalDateTime.parse('2120-01-08T00:00:00') //UTC time
        dto.end = LocalDateTime.parse('2120-01-08T03:00:00')
        warnings = validator.validate([dto], -1)
        Assertions.assertEquals(0, warnings.size())
    }

}
