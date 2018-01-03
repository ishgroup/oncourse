package ish.oncourse.solr.functions.course

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.*
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

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

        objectContext = testContext.serverRuntime.newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        collegeContext = dataContext.newCollege()
        college = collegeContext.college
    }

    @Test
    void testByTutor(){
        CTutor tutor1 = CTutor.instance(objectContext, college, "targetTutor")
        CTutor tutor2 = CTutor.instance(objectContext, college, "otherTutor")
        CCourse expectedCourse1 = collegeContext.newCourse("expectedCourse1")
        CCourse expectedCourse2 = collegeContext.newCourse("expectedCourse2")
        CCourse otherCourse = collegeContext.newCourse("otherCourse")
        List<Course> actualCourses

        CTutorRole.instance(objectContext, tutor1.tutor, expectedCourse1.newCourseClass("expectedClass11").courseClass)
        CTutorRole.instance(objectContext, tutor1.tutor, expectedCourse1.newCourseClass("expectedClass12").courseClass)
        CTutorRole.instance(objectContext, tutor1.tutor, expectedCourse2.newCourseClass("expectedClass21").courseClass)

        CTutorRole.instance(objectContext, tutor2.tutor, expectedCourse2.newCourseClass("expectedClass21").courseClass)
        CTutorRole.instance(objectContext, tutor2.tutor, otherCourse.newCourseClass("otherClass").courseClass)

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
        CCourse expectedCourse = collegeContext.newCourse("expectedCourse")
        CRoom targetRoom = CRoom.instance(objectContext, college)
        CSession.instance(objectContext, expectedCourse.newCourseClass("expectedClass1").courseClass).room(targetRoom.room)
        CSession.instance(objectContext, expectedCourse.newCourseClass("expectedClass1").courseClass).room(targetRoom.room)

        CCourse otherCourse = collegeContext.newCourse("expectedCourse")
        CRoom otherRoom = CRoom.instance(objectContext, college)
        CSession.instance(objectContext, otherCourse.newCourseClass("otherClass1").courseClass).room(otherRoom.room)

        CCourse courseWithoutSession = collegeContext.newCourse("courseWithoutSession")

        CRoom mainRoom = CRoom.instance(objectContext, college)
        CCourse courseWithMainRoom = collegeContext.newCourse("courseWithMainRoom")
        courseWithMainRoom.newCourseClass("classWithMainRoom1").room(mainRoom.room)
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
        CCourse expectedCourse = collegeContext.newCourse("expectedCourse")
        CRoom targetRoom = CRoom.instance(objectContext, college)
        expectedCourse.newCourseClass("expectedClass1").room(targetRoom.room)

        CCourse otherCourse = collegeContext.newCourse("otherCourse")
        CRoom otherRoom = CRoom.instance(objectContext, college)
        otherCourse.newCourseClass("otherClass1").room(otherRoom.room)

        CCourse sessionCourse = collegeContext.newCourse("sessionCourse")
        CSession.instance(objectContext, sessionCourse.newCourseClass("expectedClass1").courseClass).room(targetRoom.room)

        CCourse courseWithoutSession = collegeContext.newCourse("courseWithoutSession")
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
        CCourse expectedCourse = collegeContext.newCourse("expectedCourse")
        CSite targetSite = CSite.instance(objectContext, college)
        CSession.instance(objectContext, expectedCourse.newCourseClass("expectedClass1").courseClass).newRoom(targetSite.site)

        CCourse otherCourse = collegeContext.newCourse("otherCourse")
        CSite otherSite = CSite.instance(objectContext, college)
        CSession.instance(objectContext, otherCourse.newCourseClass("otherClass1").courseClass).newRoom(otherSite.site)

        CCourse courseWithMainRoomSite = collegeContext.newCourse("courseWithMainRoomSite")
        courseWithMainRoomSite.newCourseClass("classWithMainRoomSite1").cRoom(targetSite.site)
        objectContext.commitChanges()

        List<Course> actualCourses = CourseQuery.bySessionSite(targetSite.site).select(objectContext)
        Assert.assertEquals(1, actualCourses.size())
        assertNotNull(actualCourses.find {c -> c.id == expectedCourse.course.id})
        assertNull(actualCourses.find {c -> c.id == courseWithMainRoomSite.course.id})
        assertNull(actualCourses.find {c -> c.id == otherCourse.course.id})
    }

    @Test
    void testByCourseClassRoomSite(){
        CCourse expectedCourse = collegeContext.newCourse("expectedCourse")
        CSite targetSite = CSite.instance(objectContext, college)
        expectedCourse.newCourseClass("expectedClass1").cRoom(targetSite.site)

        CCourse otherCourse = collegeContext.newCourse("otherCourse")
        CSite otherSite = CSite.instance(objectContext, college)
        otherCourse.newCourseClass("otherClass1").cRoom(otherSite.site)

        CCourse sessionCourse = collegeContext.newCourse("sessionCourse")
        CSession.instance(objectContext, sessionCourse.newCourseClass("expectedClass1").courseClass).newRoom(targetSite.site)
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
        CCourse expectedCourse1 = collegeContext.newCourse("expectedCourse1")
        CCourse expectedCourse2 = collegeContext.newCourse("expectedCourse2")
        collegeContext.tagCourse(expectedCourse1.course, targetTag)
        collegeContext.tagCourse(expectedCourse2.course, targetTag)

        Tag otherTag = collegeContext.tag("otherTag")
        CCourse otherCourse1 = collegeContext.newCourse("otherCourse1")
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
        CCourse expectedCourse1 = collegeContext.newCourse("expectedCourse1")
        CCourse expectedCourse2 = collegeContext.newCourse("expectedCourse2")
        collegeContext.tagCourse(expectedCourse1.course, targetTag)
        collegeContext.tagCourse(expectedCourse2.course, targetTag)

        Tag otherTag = collegeContext.tag("otherTag")
        CCourse otherCourse1 = collegeContext.newCourse("otherCourse1")
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
