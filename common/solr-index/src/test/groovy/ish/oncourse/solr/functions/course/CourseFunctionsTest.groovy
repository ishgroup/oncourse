package ish.oncourse.solr.functions.course

import ish.oncourse.entityBuilder.CourseClassBuilder
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.solr.model.CollegeContext
import ish.oncourse.solr.model.DataContext
import ish.oncourse.test.TestContext
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static ish.oncourse.test.functions.Functions.createRuntime
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

/**
 * Created by alex on 11/14/17.
 */
class CourseFunctionsTest {
    private TestContext testContext
    private ObjectContext objectContext

    private CollegeContext collegeContext

    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()
        objectContext = createRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        collegeContext = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
    }

    @Test
    void testCourseClassQuery(){
        Course targetCourse = collegeContext.course("Target course")
        Course otherCourse = collegeContext.course("Other course")

        CourseClass expected1 = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("expected1", collegeContext.college)
                .addToCourse(targetCourse)
                .setStartDate(new Date().plus(1))
                .build()
        CourseClass expected2 = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("expected2", collegeContext.college)
                .addToCourse(targetCourse)
                .setStartDate(new Date().plus(1))
                .build()
        CourseClass expected3Distant = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("expected3Distant", collegeContext.college)
                .addToCourse(targetCourse)
                .setDistantLearning(true)
                .build()
        CourseClass invisible = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("invisible", collegeContext.college)
                .addToCourse(targetCourse)
                .setStartDate(new Date().plus(1))
                .setVisible(false)
                .build()
        CourseClass cancelled = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("cancelled", collegeContext.college)
                .addToCourse(targetCourse)
                .setStartDate(new Date().plus(1))
                .setCancelled(true)
                .build()
        CourseClass notFuture = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("notFuture", collegeContext.college)
                .addToCourse(targetCourse)
                .setStartDate(new Date())
                .build()
        CourseClass otherCourse1 = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("otherCourse1", collegeContext.college)
                .addToCourse(otherCourse)
                .setStartDate(new Date().plus(1))
                .build()
        CourseClass otherCourse2 = new CourseClassBuilder("objectContext" : objectContext)
                .createDefaultClass("otherCourse2", collegeContext.college)
                .addToCourse(otherCourse)
                .setDistantLearning(true)
                .build()

        CourseContext courseContext = new CourseContext("course" : targetCourse, "context" : objectContext)
        List<CourseClass> actualClasses = CourseFunctions.courseClassQuery(courseContext).select(objectContext)
        Assert.assertEquals(3, actualClasses.size())
        assertNotNull(actualClasses.find {cc -> (cc.code == expected1.code) })
        assertNotNull(actualClasses.find {cc -> (cc.code == expected2.code) })
        assertNotNull(actualClasses.find {cc -> (cc.code == expected3Distant.code) })

        assertNull("Only WEB VISIBLE Classes can be selected",actualClasses.find {cc -> (cc.code == invisible.code) })
        assertNull("Only ACTIVE Classes can be selected",actualClasses.find {cc -> (cc.code == cancelled.code) })
        assertNull("Only FUTURE or DISTANT Classes can be selected",actualClasses.find {cc -> (cc.code == notFuture.code) })
        assertNull("There is a Class from other Course",actualClasses.find {cc -> (cc.code == otherCourse1.code) })
        assertNull("There is a Class from other Course",actualClasses.find {cc -> (cc.code == otherCourse2.code) })
    }

    @Test
    void testCourseTagsQuery(){
    }

    @After
    void after() {
        testContext.close()
    }
}
