package ish.oncourse.server.duplicate

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.duplicate.ClassDuplicationRequest
import ish.oncourse.entity.services.CourseClassService
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.cayenne.*
import ish.util.DateTimeUtil
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/duplicate/duplicateCourseClassTestDataSet.xml")
class DuplicateCourseClassTest extends TestWithDatabase {

    private CourseClassService courseClassService
    private CourseClassDao courseClassDao
    private int daysTo = 13
    
    @Test
    void testClassDuplicationGeneral() {
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        ClassDuplicationRequest request = new ClassDuplicationRequest()
        request.setDaysTo(daysTo)
        request.setCopyTutors(false)
        request.setCopyTrainingPlans(false)
        request.setApplyDiscounts(false)
        request.setCopyCosts(true)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        request.setCopyVetData(false)
        request.setCopyAssessments(false)

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        //at first new class always should be not cancelled and not shown on web
        Assertions.assertFalse(newClass.getIsCancelled())
        Assertions.assertFalse(newClass.getIsShownOnWeb())

        Assertions.assertEquals(courseClass.getCourse(), newClass.getCourse())
        Assertions.assertEquals("1235", newClass.getCode())
        Assertions.assertEquals(courseClass.getIncomeAccount(), newClass.getIncomeAccount())
        Assertions.assertEquals(courseClass.getTax(), newClass.getTax())
        int secondsInDay = 24 * 60 * 60 * 1000
        Assertions.assertEquals(courseClass.getStartDateTime().getTime() + daysTo * secondsInDay, newClass.getStartDateTime().getTime())
        Assertions.assertEquals(courseClass.getEndDateTime().getTime() + daysTo * secondsInDay, newClass.getEndDateTime().getTime())
        Assertions.assertEquals(courseClass.getAttendanceType(), newClass.getAttendanceType())
        Assertions.assertEquals(courseClass.getMaximumPlaces(), newClass.getMaximumPlaces())
        Assertions.assertEquals(courseClass.getMinimumPlaces(), newClass.getMinimumPlaces())
        Assertions.assertEquals(courseClass.getSessionsSkipWeekends(), newClass.getSessionsSkipWeekends())
        Assertions.assertEquals(courseClass.getSessionRepeatType(), newClass.getSessionRepeatType())
        Assertions.assertEquals(courseClass.getSessionsCount(), newClass.getSessionsCount())
        Assertions.assertEquals(courseClass.getWebDescription(), newClass.getWebDescription())
        Assertions.assertEquals(courseClass.getReportableHours(), newClass.getReportableHours())
        Assertions.assertEquals(courseClass.getFeeExGst(), newClass.getFeeExGst())
        Assertions.assertEquals(courseClass.getPaymentPlanLines().size(), newClass.getPaymentPlanLines().size())
        Assertions.assertEquals(courseClass.getTags().size(), newClass.getTags().size())
        Assertions.assertEquals(courseClass.getSessions().size(), newClass.getSessions().size())
    }

    
    @Test
    void testClassDuplicationTutors() {
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        ClassDuplicationRequest request = new ClassDuplicationRequest()
        request.setDaysTo(daysTo)
        request.setCopyTutors(true)
        request.setCopyTrainingPlans(false)
        request.setApplyDiscounts(false)
        request.setCopyCosts(false)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        request.setCopyVetData(false)
        request.setCopyAssessments(false)

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        //1 tutor role
        Assertions.assertEquals(courseClass.getTutorRoles().size(), newClass.getTutorRoles().size())
        Assertions.assertEquals(courseClass.getTutorRoles().get(0).getDefinedTutorRole(), newClass.getTutorRoles().get(0).getDefinedTutorRole())
        Assertions.assertEquals(courseClass.getTutorRoles().get(0).getTutor(), newClass.getTutorRoles().get(0).getTutor())
        Assertions.assertEquals(courseClass.getTutorRoles().get(0).getSessionsTutors().size(), newClass.getTutorRoles().get(0).getSessionsTutors().size())


        ClassDuplicationRequest allFalseRequest = new ClassDuplicationRequest()
        allFalseRequest.setDaysTo(daysTo)
        allFalseRequest.setCopyTutors(false)
        allFalseRequest.setCopyTrainingPlans(false)
        allFalseRequest.setApplyDiscounts(false)
        allFalseRequest.setCopyCosts(false)
        allFalseRequest.setCopySitesAndRooms(false)
        allFalseRequest.setCopyPayableTimeForSessions(false)
        allFalseRequest.setCopyNotes(true)
        allFalseRequest.setCopyVetData(false)
        allFalseRequest.setCopyAssessments(false)

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        Assertions.assertEquals(0, newClass2.getTutorRoles().size())
    }


    
    @Test
    void testClassDuplicationTrainingPlans() {
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        ClassDuplicationRequest request = new ClassDuplicationRequest()
        request.setDaysTo(daysTo)
        request.setCopyTutors(false)
        request.setCopyTrainingPlans(true)
        request.setApplyDiscounts(false)
        request.setCopyCosts(false)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        request.setCopyVetData(false)
        request.setCopyAssessments(false)

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        List<Session> oldSessions = courseClass.getSessions()
        List<Session> newSessions = newClass.getSessions()

        //3 sessions
        Assertions.assertEquals(oldSessions.size(), newSessions.size())

        Session.START_DATETIME.asc().orderList(oldSessions)
        Session.START_DATETIME.asc().orderList(newSessions)

        Assertions.assertEquals(oldSessions.get(0).getSessionModules().size(), newSessions.get(0).getSessionModules().size())
        Assertions.assertEquals(oldSessions.get(1).getSessionModules().size(), newSessions.get(1).getSessionModules().size())
        Assertions.assertEquals(oldSessions.get(2).getSessionModules().size(), newSessions.get(2).getSessionModules().size())


        ClassDuplicationRequest allFalseRequest = new ClassDuplicationRequest()
        allFalseRequest.setDaysTo(daysTo)
        allFalseRequest.setCopyTutors(false)
        allFalseRequest.setCopyTrainingPlans(false)
        allFalseRequest.setApplyDiscounts(false)
        allFalseRequest.setCopyCosts(false)
        allFalseRequest.setCopySitesAndRooms(false)
        allFalseRequest.setCopyPayableTimeForSessions(false)
        allFalseRequest.setCopyNotes(true)
        allFalseRequest.setCopyVetData(false)
        allFalseRequest.setCopyAssessments(false)

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        List<Session> newSessions2 = newClass2.getSessions()

        Assertions.assertEquals(oldSessions.size(), newSessions2.size())

        Session.START_DATETIME.asc().orderList(oldSessions)
        Session.START_DATETIME.asc().orderList(newSessions2)

        Assertions.assertEquals(0, newSessions2.get(0).getSessionModules().size())
        Assertions.assertEquals(0, newSessions2.get(1).getSessionModules().size())
        Assertions.assertEquals(0, newSessions2.get(2).getSessionModules().size())
    }


    
    @Test
    void testClassDuplicationDiscounts() {
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)
        ClassCost cost = cayenneContext.newObject(ClassCost.class)
        cost.setCourseClass(courseClass)
        courseClass.getDiscountCourseClasses().each { dcc -> dcc.setClassCost(cost) }
        cayenneContext.commitChanges()

        ClassDuplicationRequest request = new ClassDuplicationRequest()
        request.setDaysTo(daysTo)
        request.setCopyTutors(false)
        request.setCopyTrainingPlans(false)
        request.setApplyDiscounts(true)
        request.setCopyCosts(false)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        request.setCopyVetData(false)
        request.setCopyAssessments(false)

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        //2 discounts
        Assertions.assertEquals(courseClass.getDiscountCourseClasses().size(), newClass.getDiscountCourseClasses().size())

        DiscountCourseClass newDiscountCourseClass1 = newClass.getDiscountCourseClasses().get(0)
        DiscountCourseClass oldDiscountCourseClass1 = courseClass.getDiscountCourseClasses()
                .find { dcc -> newDiscountCourseClass1.getDiscount().equals(dcc.getDiscount()) }

        Assertions.assertEquals(oldDiscountCourseClass1.getPredictedStudentsPercentage(), newDiscountCourseClass1.getPredictedStudentsPercentage())
        Assertions.assertEquals(oldDiscountCourseClass1.getDiscountDollar(), newDiscountCourseClass1.getDiscountDollar())

        DiscountCourseClass newDiscountCourseClass2 = newClass.getDiscountCourseClasses().get(1)
        DiscountCourseClass oldDiscountCourseClass2 = courseClass.getDiscountCourseClasses()
                .find { dcc -> newDiscountCourseClass2.getDiscount().equals(dcc.getDiscount()) }

        Assertions.assertEquals(oldDiscountCourseClass2.getPredictedStudentsPercentage(), newDiscountCourseClass2.getPredictedStudentsPercentage())
        Assertions.assertEquals(oldDiscountCourseClass2.getDiscountDollar(), newDiscountCourseClass2.getDiscountDollar())


        ClassDuplicationRequest allFalseRequest = new ClassDuplicationRequest()
        allFalseRequest.setDaysTo(daysTo)
        allFalseRequest.setCopyTutors(false)
        allFalseRequest.setCopyTrainingPlans(false)
        allFalseRequest.setApplyDiscounts(false)
        allFalseRequest.setCopyCosts(false)
        allFalseRequest.setCopySitesAndRooms(false)
        allFalseRequest.setCopyPayableTimeForSessions(false)
        allFalseRequest.setCopyNotes(true)
        allFalseRequest.setCopyVetData(false)
        allFalseRequest.setCopyAssessments(false)

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        Assertions.assertEquals(0, newClass2.getDiscountCourseClasses().size())
    }

    
    @Test
    void testClassDuplicationAssessments() {
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        ClassDuplicationRequest request = new ClassDuplicationRequest()
        request.setDaysTo(daysTo)
        request.setCopyTutors(true)  //it needs to copy AssessmentClassTutors
        request.setCopyTrainingPlans(false)
        request.setApplyDiscounts(false)
        request.setCopyCosts(false)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        request.setCopyVetData(false)
        request.setCopyAssessments(true)

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        //2 assessmentClasses
        Assertions.assertEquals(courseClass.getAssessmentClasses().size(), newClass.getAssessmentClasses().size())

        AssessmentClass newAssessmentClass = newClass.getAssessmentClasses().get(0)
        AssessmentClass oldAssessmentClass = courseClass.getAssessmentClasses()
                .find { ac -> newAssessmentClass.getAssessment().equals(ac.getAssessment()) }

        Assertions.assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass.getReleaseDate(), daysTo), newAssessmentClass.getReleaseDate())
        Assertions.assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass.getDueDate(), daysTo), newAssessmentClass.getDueDate())
        Assertions.assertEquals(oldAssessmentClass.getAssessmentClassTutors().size(), newAssessmentClass.getAssessmentClassTutors().size())
        Assertions.assertEquals(oldAssessmentClass.getAssessmentClassModules().size(), newAssessmentClass.getAssessmentClassModules().size())

        AssessmentClass newAssessmentClass2 = newClass.getAssessmentClasses().get(1)
        AssessmentClass oldAssessmentClass2 = courseClass.getAssessmentClasses()
                .find { ac -> newAssessmentClass2.getAssessment().equals(ac.getAssessment()) }

        Assertions.assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass2.getReleaseDate(), daysTo), newAssessmentClass2.getReleaseDate())
        Assertions.assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass2.getDueDate(), daysTo), newAssessmentClass2.getDueDate())
        Assertions.assertEquals(oldAssessmentClass2.getAssessmentClassTutors().size(), newAssessmentClass2.getAssessmentClassTutors().size())
        Assertions.assertEquals(oldAssessmentClass2.getAssessmentClassModules().size(), newAssessmentClass2.getAssessmentClassModules().size())


        ClassDuplicationRequest allFalseRequest = new ClassDuplicationRequest()
        allFalseRequest.setDaysTo(daysTo)
        allFalseRequest.setCopyTutors(false)
        allFalseRequest.setCopyTrainingPlans(false)
        allFalseRequest.setApplyDiscounts(false)
        allFalseRequest.setCopyCosts(false)
        allFalseRequest.setCopySitesAndRooms(false)
        allFalseRequest.setCopyPayableTimeForSessions(false)
        allFalseRequest.setCopyNotes(true)
        allFalseRequest.setCopyVetData(false)
        allFalseRequest.setCopyAssessments(false)

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, cayenneContext, courseClassDao, null).duplicate()

        Assertions.assertEquals(0, newClass2.getAssessmentClasses().size())
    }
}
