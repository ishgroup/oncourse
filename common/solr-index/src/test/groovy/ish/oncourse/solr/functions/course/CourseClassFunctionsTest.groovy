package ish.oncourse.solr.functions.course

import ish.oncourse.model.*
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.*
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 11/14/17.
 */
class CourseClassFunctionsTest {
    private TestContext testContext
    private ObjectContext objectContext

    private College college
    private CCourse course

    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()

        objectContext = testContext.serverRuntime.newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        CCollege collegeContext = dataContext.newCollege()
        college = collegeContext.college
        course = collegeContext.newCourse("course")
    }

    @Test
    void testSessionsQuery(){
        CourseClass targetClass = course.newCourseClass("targetClass").courseClass
        CSession.instance(objectContext, targetClass).startDate(new Date().plus(1)).build()
        CSession.instance(objectContext, targetClass).startDate(new Date()).build()
        CSession.instance(objectContext, targetClass).startDate(new Date().minus(1)).build()
        CSession.instance(objectContext, targetClass).startDate(new Date()).build()

        CourseClass otherClass = course.newCourseClass("otherClass").courseClass
        CSession.instance(objectContext, otherClass).startDate(new Date()).build()
        CSession.instance(objectContext, otherClass).startDate(new Date().plus(1)).build()

        List<Session> actualSessions = CourseClassFunctions.sessionsQuery(targetClass).select(objectContext)
        Assert.assertEquals(4, actualSessions.size())

        for (int i = 1; i < actualSessions.size(); i++) {
            Assert.assertTrue("all sessions must be related with target course", actualSessions.get(i).courseClass.code == targetClass.code)
            Assert.assertTrue("query sorts sessions by date in asc", actualSessions.get(i).startDate >= actualSessions.get(i-1).startDate)
        }
    }

    @Test
    void testSessionSitesQuery(){
        CourseClass targetClass = course.newCourseClass("targetClass").courseClass

        Site expectedSite = CSite.instance(objectContext, college).isWebVisible(true).build().site
        CSession.instance(objectContext,targetClass).newRoom(expectedSite).build()

        Site invisibleSite = CSite.instance(objectContext, college).isWebVisible(false).build().site
        CSession.instance(objectContext,targetClass).newRoom(invisibleSite).build()

        CourseClass otherClass = course.newCourseClass("otherClass").courseClass
        Site otherClassSite = CSite.instance(objectContext, college).isWebVisible(true).build().site
        CSession.instance(objectContext,otherClass).newRoom(otherClassSite).build()

        Site mainSite = CSite.instance(objectContext, college).isWebVisible(true).build().site
        targetClass.room = CRoom.instance(objectContext, mainSite).build().room


        List<Site> actualSites = CourseClassFunctions.sessionSitesQuery(targetClass).select(objectContext)
        Assert.assertEquals(1, actualSites.size())
        Assert.assertNotNull(actualSites.find({site -> site.id == expectedSite.id}))
        Assert.assertNull("query searches only visible sites", actualSites.find({site -> site.id == invisibleSite.id}))
        Assert.assertNull(actualSites.find({site -> site.id == otherClassSite.id}))
        Assert.assertNull("query searches sites of session rooms, not of courseClass room", actualSites.find({site -> site.id == mainSite.id}))
    }

    @Test
    void testContactsQuery(){
        CourseClass targetClass = course.newCourseClass("targetClass").courseClass
        Contact expectedContact = CContact.instance(objectContext, college, "expected contact").build().contact
        CTutorRole.instance(objectContext, expectedContact, targetClass).build()

        CourseClass otherClass = course.newCourseClass("otherClass").courseClass
        Contact otherClassContact = CContact.instance(objectContext, college, "otherClass tutor").build().contact
        CTutorRole.instance(objectContext, otherClassContact, otherClass).build()

        Contact simpleContact = CContact.instance(objectContext, college, "simple contact").build().contact

        List<Contact> actualContacts = CourseClassFunctions.contactsQuery(targetClass).select(objectContext)
        Assert.assertEquals(1, actualContacts.size())
        Assert.assertNotNull(actualContacts.find({site -> site.id == expectedContact.id}))
        Assert.assertNull("query will return only tutors of query defined class", actualContacts.find({site -> site.id == otherClassContact.id}))
        Assert.assertNull("query will return only tutors", actualContacts.find({site -> site.id == simpleContact.id}))
    }

    @After
    void after() {
        testContext.close()
    }
}
