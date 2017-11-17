package ish.oncourse.solr.functions.course

import ish.oncourse.entityBuilder.CourseClassBuilder
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Tag
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

        CourseClass expected1 = CourseClassBuilder.instance(objectContext, "expected1", targetCourse)
                .startDate(new Date().plus(1))
                .build()
        CourseClass expected2 = CourseClassBuilder.instance(objectContext, "expected2", targetCourse)
                .startDate(new Date().plus(1))
                .build()
        CourseClass expected3Distant = CourseClassBuilder.instance(objectContext, "expected3Distant", targetCourse)
                .isDistantLearningCourse(true)
                .build()
        CourseClass invisible = CourseClassBuilder.instance(objectContext, "invisible", targetCourse)
                .startDate(new Date().plus(1))
                .isWebVisible(false)
                .build()
        CourseClass cancelled = CourseClassBuilder.instance(objectContext, "cancelled", targetCourse)
                .startDate(new Date().plus(1))
                .cancelled(true)
                .build()
        CourseClass notFuture = CourseClassBuilder.instance(objectContext, "cancelled", targetCourse)
                .startDate(new Date())
                .build()
        CourseClass otherCourse1 = CourseClassBuilder.instance(objectContext, "otherCourse1", otherCourse)
                .startDate(new Date().plus(1))
                .build()
        CourseClass otherCourse2 = CourseClassBuilder.instance(objectContext, "otherCourse1", otherCourse)
                .isDistantLearningCourse(true)
                .build()

        CourseContext courseContext = new CourseContext(course : targetCourse, context : objectContext)
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
        collegeContext.tag("Tag1")
        collegeContext.tag("Tag11", false)
        collegeContext.tag("Tag12")
        collegeContext.tag("Tag2")
        collegeContext.tag("Tag21")
        collegeContext.tag("Tag22")

        collegeContext.addTag("Tag1", "Tag11", "Tag12")
        collegeContext.addTag("Tag2", "Tag21")

        collegeContext.course("Other course")
        collegeContext.tagCourse("OTHER COURSE", "Tag2")
        collegeContext.tagCourse("OTHER COURSE", "Tag21")

        Course targetCourse = collegeContext.course("Target course")
        collegeContext.tagCourse("TARGET COURSE", "Tag1")
        collegeContext.tagCourse("TARGET COURSE", "Tag11")
        collegeContext.tagCourse("TARGET COURSE", "Tag22")

        List<Tag> actualCourseTags = CourseFunctions.courseTagsQuery(targetCourse).select(objectContext)
        assertNotNull(actualCourseTags.find {tag -> tag.name == "Tag1"})
        assertNotNull("tag is related to target course and must be selected regardless of it's visibility", actualCourseTags.find {tag -> tag.name == "Tag11"})
        assertNotNull("tag is related to target course and must be selected regardless of it's parent", actualCourseTags.find {tag -> tag.name == "Tag22"})
        assertNull("tag isn't related with course regardless of it's parent tag", actualCourseTags.find {tag -> tag.name == "Tag12"})
        assertNull("tag isn't related to any course", actualCourseTags.find {tag -> tag.name == "Tag2"})
        assertNull("tag isn't related to other course", actualCourseTags.find {tag -> tag.name == "Tag21"})

    }

    @After
    void after() {
        testContext.close()
    }
}
