package ish.oncourse.entity.services

import groovy.transform.CompileStatic
import ish.common.types.AttendanceType
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.*
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class EnrolmentServiceTest {

    void testGetAttendancePercent() {
        Enrolment enrolment = mock(Enrolment.class)

        // 10/10(attended) + 10/10(with reason) + 2/10(partial) = 73%
        List<Attendance> iAttendances1 = prepareData1()
        when(enrolment.getAttendances()).thenReturn(iAttendances1)
        when(enrolment.getAttendancePercent()).thenCallRealMethod()

        Assertions.assertEquals(73, (int) enrolment.getAttendancePercent())

        // 0/0(unmarked) + 0/10(without reason) + 5/10(partial) = 25%
        List<Attendance> iAttendances2 = prepareData2()
        when(enrolment.getAttendances()).thenReturn(iAttendances2)

        Assertions.assertEquals(25, (int) enrolment.getAttendancePercent())


        // 0/0(unmarked) + 10/10(attended) + 0/0(without reason, in future) + 0/0(attended, in future) = 100%
        List<Attendance> iAttendances3 = prepareData3()
        when(enrolment.getAttendances()).thenReturn(iAttendances3)

        Assertions.assertEquals(100, (int) enrolment.getAttendancePercent())

        // 0/0(unmarked) + 0/0(unmarked, in future) = 0%
        List<Attendance> iAttendances4 = prepareData4()
        when(enrolment.getAttendances()).thenReturn(iAttendances4)
        Assertions.assertEquals(0, (int) enrolment.getAttendancePercent())

        // 0/0(unmarked) + 1/10(partial) + 0/0(partial, in future) + 0/0(with reason, in future) = 10%
        List<Attendance> iAttendances5 = prepareData5()
        when(enrolment.getAttendances()).thenReturn(iAttendances5)
        Assertions.assertEquals(10, (int) enrolment.getAttendancePercent())

    }

    /**
     * return empty list of attendances for enrolments which status not in EnrolmentStatus.STATUSES_LEGIT
     */
    void testGetAttendances() {

        Enrolment refundedEnrolment = mock(Enrolment.class)
        when(refundedEnrolment.getStatus()).thenReturn(EnrolmentStatus.REFUNDED)

        Assertions.assertEquals(0, refundedEnrolment.getAttendances().size())


        Student student = mock(Student.class)

        Attendance attendance = mock(Attendance.class)
        when(attendance.getStudent()).thenReturn(student)
        List attendances = Arrays.asList(attendance)

        Session session = mock(Session.class)
        when(session.getAttendance()).thenReturn(attendances)
        List sessions = Arrays.asList(session)

        CourseClass courseClass = mock(CourseClass.class)
        when(courseClass.getSessions()).thenReturn(sessions)

        Enrolment successEnrolment = mock(Enrolment.class)
        when(successEnrolment.getStatus()).thenReturn(EnrolmentStatus.SUCCESS)
        when(successEnrolment.getStudent()).thenReturn(student)
        when(successEnrolment.getCourseClass()).thenReturn(courseClass)
        when(successEnrolment.getAttendances()).thenCallRealMethod()

        Assertions.assertEquals(1, successEnrolment.getAttendances().size())
    }

    private static List<Attendance> prepareData1() {
        ArrayList<Attendance> attendances = new ArrayList<>()
        attendances.add(getAttendance(AttendanceType.ATTENDED, 10))
        attendances.add(getAttendance(AttendanceType.DID_NOT_ATTEND_WITH_REASON, 10))
        attendances.add(getAttendance(AttendanceType.PARTIAL, 10, 2))
        return attendances
    }

    private static List<Attendance> prepareData2() {
        ArrayList<Attendance> attendances = new ArrayList<>()
        attendances.add(getAttendance(AttendanceType.UNMARKED, 10))
        attendances.add(getAttendance(AttendanceType.PARTIAL, 10, 5))
        attendances.add(getAttendance(AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON, 10))
        return attendances
    }

    private static List<Attendance> prepareData3() {
        ArrayList<Attendance> attendances = new ArrayList<>()
        attendances.add(getAttendance(AttendanceType.UNMARKED, 10))
        attendances.add(getAttendance(AttendanceType.ATTENDED, 10))
        attendances.add(getAttendance(AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON, 10, false))
        attendances.add(getAttendance(AttendanceType.ATTENDED, 10, false))
        return attendances
    }

    private static List<Attendance> prepareData4() {
        ArrayList<Attendance> attendances = new ArrayList<>()
        attendances.add(getAttendance(AttendanceType.UNMARKED, 10))
        attendances.add(getAttendance(AttendanceType.UNMARKED, 10, false))
        return attendances
    }

    private static List<Attendance> prepareData5() {
        ArrayList<Attendance> attendances = new ArrayList<>()
        attendances.add(getAttendance(AttendanceType.UNMARKED, 10))
        attendances.add(getAttendance(AttendanceType.PARTIAL, 10, 1))
        attendances.add(getAttendance(AttendanceType.PARTIAL, 10, 5, false))
        attendances.add(getAttendance(AttendanceType.DID_NOT_ATTEND_WITH_REASON, 10, false))
        return attendances
    }

    private static Attendance getAttendance(AttendanceType type, Integer fullTime) {
        return getAttendance(type, fullTime, null, true)
    }

    private static Attendance getAttendance(AttendanceType type, Integer fullTime, boolean beforeNow) {
        return getAttendance(type, fullTime, null, beforeNow)
    }

    private static Attendance getAttendance(AttendanceType type, Integer fullTime, Integer partialTime) {
        return getAttendance(type, fullTime, partialTime, true)
    }

    private static Attendance getAttendance(AttendanceType type, Integer fullTime, Integer partialTime, boolean beforeNow) {
        Attendance attendance = mock(Attendance.class)
        when(attendance.getAttendanceType()).thenReturn(type)
        when(attendance.getDurationMinutes()).thenReturn(partialTime)

        Session session = mock(Session.class)

        when(attendance.getSession()).thenReturn(session)
        Date startDate = DateUtils.addDays(new Date(), beforeNow ? -5 : 5)
        Date endDate = DateUtils.addMinutes(startDate, fullTime)
        when(session.getStartDatetime()).thenReturn(startDate)
        when(session.getEndDatetime()).thenReturn(endDate)
        when(session.getDurationInMinutes()).thenCallRealMethod()


        return attendance
    }

}
