package ish.oncourse.server.duplicate

import ish.CayenneIshTestCase
import ish.duplicate.ClassDuplicationRequest
import ish.oncourse.entity.services.CourseClassService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.SessionTest
import ish.util.DateTimeUtil
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertFalse
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test

/**
 * Created by anarut on 9/8/16.
 */
class DuplicateCourseClassTest extends CayenneIshTestCase {

    private CourseClassService courseClassService
    private CourseClassDao courseClassDao

    private DataContext context
    private int daysTo = 13


    @Before
    void setup() throws Exception {
        wipeTables()
        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/duplicate/duplicateCourseClassTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        executeDatabaseOperation(rDataSet)
    }

    @Test
    void testClassDuplicationGeneral() {
        context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(context)

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

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, context, courseClassDao, null).duplicate()

        //at first new class always should be not cancelled and not shown on web
        assertFalse(newClass.getIsCancelled())
        assertFalse(newClass.getIsShownOnWeb())

        assertEquals(courseClass.getCourse(), newClass.getCourse())
        assertEquals("1235", newClass.getCode())
        assertEquals(courseClass.getIncomeAccount(), newClass.getIncomeAccount())
        assertEquals(courseClass.getTax(), newClass.getTax())
        int secondsInDay = 24 * 60 * 60 * 1000
        assertEquals(courseClass.getStartDateTime().getTime() + daysTo * secondsInDay, newClass.getStartDateTime().getTime())
        assertEquals(courseClass.getEndDateTime().getTime() + daysTo * secondsInDay, newClass.getEndDateTime().getTime())
        assertEquals(courseClass.getAttendanceType(), newClass.getAttendanceType())
        assertEquals(courseClass.getMaximumPlaces(), newClass.getMaximumPlaces())
        assertEquals(courseClass.getMinimumPlaces(), newClass.getMinimumPlaces())
        assertEquals(courseClass.getSessionsSkipWeekends(), newClass.getSessionsSkipWeekends())
        assertEquals(courseClass.getSessionRepeatType(), newClass.getSessionRepeatType())
        assertEquals(courseClass.getSessionsCount(), newClass.getSessionsCount())
        assertEquals(courseClass.getWebDescription(), newClass.getWebDescription())
        assertEquals(courseClass.getReportableHours(), newClass.getReportableHours())
        assertEquals(courseClass.getFeeExGst(), newClass.getFeeExGst())
        assertEquals(courseClass.getPaymentPlanLines().size(), newClass.getPaymentPlanLines().size())
        assertEquals(courseClass.getTags().size(), newClass.getTags().size())
        assertEquals(courseClass.getSessions().size(), newClass.getSessions().size())
    }

    @Test
    void testClassDuplicationTutors() {
        context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(context)

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

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, context, courseClassDao, null).duplicate()

        //1 tutor role
        assertEquals(courseClass.getTutorRoles().size(), newClass.getTutorRoles().size())
        assertEquals(courseClass.getTutorRoles().get(0).getDefinedTutorRole(), newClass.getTutorRoles().get(0).getDefinedTutorRole())
        assertEquals(courseClass.getTutorRoles().get(0).getTutor(), newClass.getTutorRoles().get(0).getTutor())
        assertEquals(courseClass.getTutorRoles().get(0).getSessionsTutors().size(), newClass.getTutorRoles().get(0).getSessionsTutors().size())


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

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, context, courseClassDao, null).duplicate()

        assertEquals(0, newClass2.getTutorRoles().size())
    }


    @Test
    void testClassDuplicationTrainingPlans() {
        context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(context)

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

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, context,courseClassDao, null).duplicate()

        List<Session> oldSessions = courseClass.getSessions()
        List<Session> newSessions = newClass.getSessions()

        //3 sessions
        assertEquals(oldSessions.size(), newSessions.size())

        Session.START_DATETIME.asc().orderList(oldSessions)
        Session.START_DATETIME.asc().orderList(newSessions)

        assertEquals(oldSessions.get(0).getSessionModules().size(), newSessions.get(0).getSessionModules().size())
        assertEquals(oldSessions.get(1).getSessionModules().size(), newSessions.get(1).getSessionModules().size())
        assertEquals(oldSessions.get(2).getSessionModules().size(), newSessions.get(2).getSessionModules().size())


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

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, context, courseClassDao, null).duplicate()

        List<Session> newSessions2 = newClass2.getSessions()

        assertEquals(oldSessions.size(), newSessions2.size())

        Session.START_DATETIME.asc().orderList(oldSessions)
        Session.START_DATETIME.asc().orderList(newSessions2)

        assertEquals(0, newSessions2.get(0).getSessionModules().size())
        assertEquals(0, newSessions2.get(1).getSessionModules().size())
        assertEquals(0, newSessions2.get(2).getSessionModules().size())
    }


    @Test
    void testClassDuplicationDiscounts() {
        context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(context)
        ClassCost cost = context.newObject(ClassCost.class)
        cost.setCourseClass(courseClass)
        courseClass.getDiscountCourseClasses().each{dcc -> dcc.setClassCost(cost)}
        context.commitChanges()

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

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, context, courseClassDao, null).duplicate()

        //2 discounts
        assertEquals(courseClass.getDiscountCourseClasses().size(), newClass.getDiscountCourseClasses().size())

        DiscountCourseClass newDiscountCourseClass1 = newClass.getDiscountCourseClasses().get(0)
        DiscountCourseClass oldDiscountCourseClass1 = courseClass.getDiscountCourseClasses()
                .find{dcc -> newDiscountCourseClass1.getDiscount().equals(dcc.getDiscount())}

        assertEquals(oldDiscountCourseClass1.getPredictedStudentsPercentage(), newDiscountCourseClass1.getPredictedStudentsPercentage())
        assertEquals(oldDiscountCourseClass1.getDiscountDollar(), newDiscountCourseClass1.getDiscountDollar())

        DiscountCourseClass newDiscountCourseClass2 = newClass.getDiscountCourseClasses().get(1)
        DiscountCourseClass oldDiscountCourseClass2 = courseClass.getDiscountCourseClasses()
                .find{dcc -> newDiscountCourseClass2.getDiscount().equals(dcc.getDiscount())}

        assertEquals(oldDiscountCourseClass2.getPredictedStudentsPercentage(), newDiscountCourseClass2.getPredictedStudentsPercentage())
        assertEquals(oldDiscountCourseClass2.getDiscountDollar(), newDiscountCourseClass2.getDiscountDollar())


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

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, context, courseClassDao, null).duplicate()

        assertEquals(0, newClass2.getDiscountCourseClasses().size())
    }

    @Test
    void testClassDuplicationAssessments() {
        context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        courseClassService = injector.getInstance(CourseClassService.class)
        courseClassDao = injector.getInstance(CourseClassDao.class)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(context)

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

        CourseClass newClass = DuplicateCourseClass.valueOf(courseClass, request, courseClassService, context, courseClassDao, null).duplicate()

        //2 assessmentClasses
        assertEquals(courseClass.getAssessmentClasses().size(), newClass.getAssessmentClasses().size())

        AssessmentClass newAssessmentClass = newClass.getAssessmentClasses().get(0)
        AssessmentClass oldAssessmentClass = courseClass.getAssessmentClasses()
                .find{ac -> newAssessmentClass.getAssessment().equals(ac.getAssessment())}

        assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass.getReleaseDate(),daysTo),  newAssessmentClass.getReleaseDate())
        assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass.getDueDate(),daysTo), newAssessmentClass.getDueDate())
        assertEquals(oldAssessmentClass.getAssessmentClassTutors().size(), newAssessmentClass.getAssessmentClassTutors().size())
        assertEquals(oldAssessmentClass.getAssessmentClassModules().size(), newAssessmentClass.getAssessmentClassModules().size())

        AssessmentClass newAssessmentClass2 = newClass.getAssessmentClasses().get(1)
        AssessmentClass oldAssessmentClass2 = courseClass.getAssessmentClasses()
                .find{ac -> newAssessmentClass2.getAssessment().equals(ac.getAssessment())}

        assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass2.getReleaseDate(),daysTo),  newAssessmentClass2.getReleaseDate())
        assertEquals(DateTimeUtil.addDaysDaylightSafe(oldAssessmentClass2.getDueDate(),daysTo), newAssessmentClass2.getDueDate())
        assertEquals(oldAssessmentClass2.getAssessmentClassTutors().size(), newAssessmentClass2.getAssessmentClassTutors().size())
        assertEquals(oldAssessmentClass2.getAssessmentClassModules().size(), newAssessmentClass2.getAssessmentClassModules().size())


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

        CourseClass newClass2 = DuplicateCourseClass.valueOf(courseClass, allFalseRequest, courseClassService, context, courseClassDao, null).duplicate()

        assertEquals(0, newClass2.getAssessmentClasses().size())
    }
}
