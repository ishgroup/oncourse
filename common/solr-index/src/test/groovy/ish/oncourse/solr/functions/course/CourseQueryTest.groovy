package ish.oncourse.solr.functions.course

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.solr.model.DataContext
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.*
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static ish.oncourse.test.functions.Functions.createRuntime
import static org.junit.Assert.*

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
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse1.course.id})
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse2.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})

        actualCourses = CourseQuery.byTutor(tutor2.tutor).select(objectContext)
        Assert.assertEquals(2, actualCourses.size())
        assertNull(actualCourses.find {c -> c.id == expectedCourse1.course.id})
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse2.course.id})
        assertNotNull(actualCourses.find {c -> c.id == otherCourse.course.id})
    }

    @Test
    void testBySessionRoom(){
        CCourse expectedCourse = collegeContext.cCourse("expectedCourse")
        CRoom targetRoom = CRoom.instance(objectContext, college)
        CSession.instance(objectContext, expectedCourse.cCourseClass("expectedClass1").courseClass).room(targetRoom.room)
        CSession.instance(objectContext, expectedCourse.cClasses.get("expectedClass1").courseClass).room(targetRoom.room)

        CCourse otherCourse = collegeContext.cCourse("expectedCourse")
        CRoom otherRoom = CRoom.instance(objectContext, college)
        CSession.instance(objectContext, otherCourse.cCourseClass("otherClass1").courseClass).room(otherRoom.room)

        CCourse courseWithoutSession = collegeContext.cCourse("courseWithoutSession")

        CRoom mainRoom = CRoom.instance(objectContext, college)
        CCourse courseWithMainRoom = collegeContext.cCourse("courseWithMainRoom")
        courseWithMainRoom.cCourseClass("classWithMainRoom1").room(mainRoom.room)
        objectContext.commitChanges()

        List<Course> actualCourses = CourseQuery.bySessionRoom(targetRoom.room).select(objectContext)
        Assert.assertEquals(1, actualCourses.size())
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == courseWithoutSession.course.id})
        assertNull(actualCourses.find {c -> c.id == courseWithMainRoom.course.id})
    }

    @Test
    void testByCourseClassRoom(){
        CCourse expectedCourse = collegeContext.cCourse("expectedCourse")
        CRoom targetRoom = CRoom.instance(objectContext, college)
        expectedCourse.cCourseClass("expectedClass1").room(targetRoom.room)

        CCourse otherCourse = collegeContext.cCourse("otherCourse")
        CRoom otherRoom = CRoom.instance(objectContext, college)
        otherCourse.cCourseClass("otherClass1").room(otherRoom.room)

        CCourse sessionCourse = collegeContext.cCourse("sessionCourse")
        CSession.instance(objectContext, sessionCourse.cCourseClass("expectedClass1").courseClass).room(targetRoom.room)

        CCourse courseWithoutSession = collegeContext.cCourse("courseWithoutSession")
        objectContext.commitChanges()

        List<Course> actualCourses = CourseQuery.byCourseClassRoom(targetRoom.room).select(objectContext)
        Assert.assertEquals(1, actualCourses.size())
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == sessionCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == courseWithoutSession.course.id})
    }

    @Test
    void testBySessionRoomSite(){
        CCourse expectedCourse = collegeContext.cCourse("expectedCourse")
        CSite targetSite = CSite.instance(objectContext, college)
        CSession.instance(objectContext, expectedCourse.cCourseClass("expectedClass1").courseClass).cRoom(targetSite.site)

        CCourse otherCourse = collegeContext.cCourse("otherCourse")
        CSite otherSite = CSite.instance(objectContext, college)
        CSession.instance(objectContext, otherCourse.cCourseClass("otherClass1").courseClass).cRoom(otherSite.site)

        CCourse courseWithMainRoomSite = collegeContext.cCourse("courseWithMainRoomSite")
        courseWithMainRoomSite.cCourseClass("classWithMainRoomSite1").cRoom(targetSite.site)
        objectContext.commitChanges()

        List<Course> actualCourses = CourseQuery.bySessionSite(targetSite.site).select(objectContext)
        Assert.assertEquals(1, actualCourses.size())
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == courseWithMainRoomSite.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})
    }

    @Test
    void testByCourseClassRoomSite(){
        CCourse expectedCourse = collegeContext.cCourse("expectedCourse")
        CSite targetSite = CSite.instance(objectContext, college)
        expectedCourse.cCourseClass("expectedClass1").cRoom(targetSite.site)

        CCourse otherCourse = collegeContext.cCourse("otherCourse")
        CSite otherSite = CSite.instance(objectContext, college)
        otherCourse.cCourseClass("otherClass1").cRoom(otherSite.site)

        CCourse sessionCourse = collegeContext.cCourse("sessionCourse")
        CSession.instance(objectContext, sessionCourse.cCourseClass("expectedClass1").courseClass).cRoom(targetSite.site)
        objectContext.commitChanges()

        List<Course> actualCourses = CourseQuery.byCourseClassSite(targetSite.site).select(objectContext)
        Assert.assertEquals(1, actualCourses.size())
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == sessionCourse.course.id})
    }

    @Test
    void testCourseTaggableByTag(){
        Tag targetTag = collegeContext.tag("targetTag")
        CCourse expectedCourse1 = collegeContext.cCourse("expectedCourse1")
        CCourse expectedCourse2 = collegeContext.cCourse("expectedCourse2")
        collegeContext.tagCourse(expectedCourse1.course, targetTag)
        collegeContext.tagCourse(expectedCourse2.course, targetTag)

        Tag otherTag = collegeContext.tag("otherTag")
        CCourse otherCourse1 = collegeContext.cCourse("otherCourse1")
        collegeContext.tagCourse(expectedCourse1.course, otherTag)
        collegeContext.tagCourse(otherCourse1.course, otherTag)

        List<Taggable> actualTaggables = CourseQuery.courseTaggableByTag(targetTag).select(objectContext)
        assertEquals(2, actualTaggables.size())
        assertNotNull(actualTaggables.find {t -> t.entityWillowId == expectedCourse1.course.id})
        assertNotNull(actualTaggables.find {t -> t.entityWillowId == expectedCourse2.course.id})
        assertNull(actualTaggables.find {t -> t.entityWillowId == otherCourse1.course.id})
    }

    @Test
    void testByTaggable(){
        Tag targetTag = collegeContext.tag("targetTag")
        CCourse expectedCourse1 = collegeContext.cCourse("expectedCourse1")
        CCourse expectedCourse2 = collegeContext.cCourse("expectedCourse2")
        collegeContext.tagCourse(expectedCourse1.course, targetTag)
        collegeContext.tagCourse(expectedCourse2.course, targetTag)

        Tag otherTag = collegeContext.tag("otherTag")
        CCourse otherCourse1 = collegeContext.cCourse("otherCourse1")
        collegeContext.tagCourse(expectedCourse1.course, otherTag)
        collegeContext.tagCourse(otherCourse1.course, otherTag)

        List<Taggable> targetTaggables = CourseQuery.courseTaggableByTag(targetTag).select(objectContext)

        List<Course> actualCourses = CourseQuery.byTaggable(targetTaggables).select(objectContext)
        assertEquals(2, actualCourses.size())
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse1.course.id})
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse2.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse1.course.id})
    }

    @After
    void after() {
        testContext.close()
    }
}
