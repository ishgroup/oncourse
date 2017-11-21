package ish.oncourse.solr.functions.course

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.solr.model.DataContext
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
import ish.oncourse.test.context.CTutor
import ish.oncourse.test.context.CTutorRole
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static ish.oncourse.test.functions.Functions.createRuntime

/**
 * Created by alex on 11/21/17.
 */
class CourseQueryTest {
    private TestContext testContext
    private ObjectContext objectContext
    private CCollege collegeContext
    private College college


    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()

        objectContext = createRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        collegeContext = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
        college = collegeContext.college
    }

    @Test
    void testByTutor(){
        CTutor tutor1 = CTutor.instance(objectContext, college, "targetTutor")
        CTutor tutor2 = CTutor.instance(objectContext, college, "otherTutor")
        CCourse expectedCourse1 = collegeContext.cCourse("expectedCourse1")
        CCourse expectedCourse2 = collegeContext.cCourse("expectedCourse2")
        CCourse otherCourse = collegeContext.cCourse("otherCourse")
        List<Course> actualCourses

        CTutorRole.instance(objectContext, tutor1.tutor, expectedCourse1.cCourseClass("expectedClass11").courseClass)
        CTutorRole.instance(objectContext, tutor1.tutor, expectedCourse1.cCourseClass("expectedClass12").courseClass)
        CTutorRole.instance(objectContext, tutor1.tutor, expectedCourse2.cCourseClass("expectedClass21").courseClass)

        CTutorRole.instance(objectContext, tutor2.tutor, expectedCourse2.cClasses.get("expectedClass21").courseClass)
        CTutorRole.instance(objectContext, tutor2.tutor, otherCourse.cCourseClass("otherClass").courseClass)

        actualCourses = CourseQuery.byTutor(tutor1.tutor).select(objectContext)
        Assert.assertEquals(2, actualCourses.size())
        Assert.assertNotNull(actualCourses.find {c -> c.id == expectedCourse1.course.id})
        Assert.assertNotNull(actualCourses.find {c -> c.id == expectedCourse2.course.id})
        Assert.assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})

        actualCourses = CourseQuery.byTutor(tutor2.tutor).select(objectContext)
        Assert.assertEquals(2, actualCourses.size())
        Assert.assertNull(actualCourses.find {c -> c.id == expectedCourse1.course.id})
        Assert.assertNotNull(actualCourses.find {c -> c.id == expectedCourse2.course.id})
        Assert.assertNotNull(actualCourses.find {c -> c.id == otherCourse.course.id})
    }

    @After
    void after() {
        testContext.close()
    }
}
