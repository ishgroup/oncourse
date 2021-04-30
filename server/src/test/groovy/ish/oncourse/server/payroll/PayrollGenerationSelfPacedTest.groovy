package ish.oncourse.server.payroll

import ish.common.types.ClassCostRepetitionType
import ish.math.Money
import ish.oncourse.entity.services.SessionService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.DataObject
import org.apache.cayenne.exp.Expression
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static ish.common.types.ClassCostFlowType.WAGES
import static org.junit.Assert.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class PayrollGenerationSelfPacedTest {

    protected ICayenneService cayenneService = null
    protected SessionService sessionService = null

    @BeforeEach
    void prepare() {
        cayenneService = mock(ICayenneService.class)
        sessionService = mock(SessionService.class)
    }

    @Test
    void testSelfPlacedCourseClass() throws Exception {

        Date date = new Date()

        ClassCost classCost = mock(ClassCost.class)

        Contact contact = mock(Contact.class)
        when(classCost.getContact()).thenReturn(contact)

        Tutor tutor = mock(Tutor.class)
        when(classCost.getContact().getTutor()).thenReturn(tutor)

        CourseClass courseClass =  mock(CourseClass.class)
        when(courseClass.getIsDistantLearningCourse()).thenReturn(true)
        when(classCost.getCourseClass()).thenReturn(courseClass)

        PayrollService service = new PayrollService(cayenneService, sessionService)

        // self-paced class: check non payable
        when(classCost.getRepetitionType()).thenReturn(ClassCostRepetitionType.PER_SESSION)
        assertFalse(service.isEligibleToProcess(classCost, date))

        when(classCost.getRepetitionType()).thenReturn(ClassCostRepetitionType.PER_TIMETABLED_HOUR)
        assertFalse(service.isEligibleToProcess(classCost, date))

        when(classCost.getRepetitionType()).thenReturn(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        assertFalse(service.isEligibleToProcess(classCost, date))
    }

    @Test
    void testIsEligibleRate() throws Exception {

        ClassCost classCost = mock(ClassCost.class)

        CourseClassTutor ccTutor = mock(CourseClassTutor.class)
        when(classCost.getTutorRole()).thenReturn(ccTutor)

        DefinedTutorRole tutorRole = mock(DefinedTutorRole.class)
        when(ccTutor.getDefinedTutorRole()).thenReturn(tutorRole)

        PayRate payRate = mock(PayRate.class)
        when(payRate.getValidFrom()).thenReturn(new Date(0L))
        when(payRate.getRate()).thenReturn(new Money("1"))
        List<PayRate> payRates = new ArrayList<>()
        payRates.add(payRate)
        when(tutorRole.getPayRates()).thenReturn(payRates)

        PayrollService service = new PayrollService(cayenneService, sessionService)
        assertTrue(service.hasEligibleRateOnDate(classCost, new Date()))
    }

    @Test
    void testLackingPaylinesValidation() throws Exception {
        Date until = null

        CourseClass courseClass = mock(CourseClass.class)
        when(courseClass.getIsDistantLearningCourse()).thenReturn(true)

        ClassCost classCost = mock(ClassCost.class)
        when(classCost.getFlowType()).thenReturn(WAGES)
        when(classCost.getContact()).thenReturn(mock(Contact.class))
        when(classCost.getCourseClass()).thenReturn(courseClass)

        PayrollService service = new PayrollService(cayenneService, sessionService)
        assertTrue(service.validateClassCost(classCost, until))
    }

    /**
     * Test cayenne expression by filtering mock collection
     * @throws Exception
     */
    @Test
    void testEligibleClassCostsExpression() throws Exception {
        List<DataObject> sourceList = createClassCostDataSetForFiltering()

        PayrollService service = new PayrollService(cayenneService, sessionService)
        Expression expr = service.getEligibleClassCostsExpression(new Date(1L))

        List<DataObject> filteredList = expr.filterObjects(sourceList)

        long[] expectedIds  = [1L, 2L, 4L]
        assertEquals(expectedIds.length, filteredList.size())
        filteredList.each{ o -> Arrays.asList(expectedIds).contains(o.readNestedProperty(ClassCost.ID.getName()))}
    }

    private List<DataObject> createClassCostDataSetForFiltering() {
        List<DataObject> classCosts = new ArrayList<>()
        classCosts.add(createClassCostMock(1L, new Date(0L), false))
        classCosts.add(createClassCostMock(2L, new Date(0L), true))
        classCosts.add(createClassCostMock(3L, new Date(2L), false))
        classCosts.add(createClassCostMock(4L, new Date(2L), true))
        return classCosts
    }

    private DataObject createClassCostMock(Long id, Date startDateTime, boolean isDistant) {
        DataObject o = mock(DataObject.class)

        fillByCourseClassProps(o, startDateTime, isDistant)

        when(o.readNestedProperty(ClassCost.FLOW_TYPE.getName())).thenReturn(WAGES)
        when(o.readNestedProperty(ClassCost.IS_SUNK.getName())).thenReturn(true)
        when(o.readNestedProperty(ClassCost.TUTOR_ROLE.getName())).thenReturn(mock(DataObject.class))
        when(o.readNestedProperty(ClassCost.ID.getName())).thenReturn(id)
        return o
    }

    private void fillByCourseClassProps(DataObject o, Date startDateTime, boolean isDistant) {
        String startDateTimePropName = ClassCost.COURSE_CLASS.dot(CourseClass.START_DATE_TIME).getName()
        String isDistantPropName = ClassCost.COURSE_CLASS.dot(CourseClass.IS_DISTANT_LEARNING_COURSE).getName()

        when(o.readNestedProperty(isDistantPropName)).thenReturn(isDistant)
        when(o.readNestedProperty(startDateTimePropName)).thenReturn(startDateTime)
    }
}
