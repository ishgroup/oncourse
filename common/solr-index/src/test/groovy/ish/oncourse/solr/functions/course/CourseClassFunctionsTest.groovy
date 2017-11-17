package ish.oncourse.solr.functions.course

import ish.oncourse.entityBuilder.*
import ish.oncourse.model.*
import ish.oncourse.solr.model.DataContext
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static ish.oncourse.test.functions.Functions.createRuntime

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

        objectContext = createRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        CCollege collegeContext = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
        college = collegeContext.college
        course = collegeContext.cCourse("course")
    }

    @Test
    void testSessionsQuery(){
        CourseClass targetClass = course.cCourseClass("targetClass").get()
        SessionBuilder.instance(objectContext, targetClass).startDate(new Date().plus(1)).build()
        SessionBuilder.instance(objectContext, targetClass).startDate(new Date()).build()
        SessionBuilder.instance(objectContext, targetClass).startDate(new Date().minus(1)).build()
        SessionBuilder.instance(objectContext, targetClass).startDate(new Date()).build()

        CourseClass otherClass = course.cCourseClass("otherClass").get()
        SessionBuilder.instance(objectContext, otherClass).startDate(new Date()).build()
        SessionBuilder.instance(objectContext, otherClass).startDate(new Date().plus(1)).build()

        List<Session> actualSessions = CourseClassFunctions.sessionsQuery(targetClass).select(objectContext)
        Assert.assertEquals(4, actualSessions.size())

        for (int i = 1; i < actualSessions.size(); i++) {
            Assert.assertTrue("all sessions must be related with target course", actualSessions.get(i).courseClass.code == targetClass.code)
            Assert.assertTrue("query sorts sessions by date in asc", actualSessions.get(i).startDate >= actualSessions.get(i-1).startDate)
        }
    }

    @Test
    void testSessionSitesQuery(){
        CourseClass targetClass = course.cCourseClass("targetClass").get()

        Site expectedSite = SiteBuilder.instance(objectContext, college).isWebVisible(true).build()
        SessionBuilder.instance(objectContext,targetClass).newDefaultRoomWithSite(expectedSite).build()

        Site invisibleSite = SiteBuilder.instance(objectContext, college).isWebVisible(false).build()
        SessionBuilder.instance(objectContext,targetClass).newDefaultRoomWithSite(invisibleSite).build()

        CourseClass otherClass = course.cCourseClass("otherClass").get()
        Site otherClassSite = SiteBuilder.instance(objectContext, college).isWebVisible(true).build()
        SessionBuilder.instance(objectContext,otherClass).newDefaultRoomWithSite(otherClassSite).build()

        Site mainSite = SiteBuilder.instance(objectContext, college).isWebVisible(true).build()
        targetClass.room = RoomBuilder.instance(objectContext, mainSite).build()


        List<Site> actualSites = CourseClassFunctions.sessionSitesQuery(targetClass).select(objectContext)
        Assert.assertEquals(1, actualSites.size())
        Assert.assertNotNull(actualSites.find({site -> site.id == expectedSite.id}))
        Assert.assertNull("query searches only visible sites", actualSites.find({site -> site.id == invisibleSite.id}))
        Assert.assertNull(actualSites.find({site -> site.id == otherClassSite.id}))
        Assert.assertNull("query searches sites of session rooms, not of courseClass room", actualSites.find({site -> site.id == mainSite.id}))
    }

    @Test
    void testContactsQuery(){
        CourseClass targetClass = course.cCourseClass("targetClass").get()
        Contact expectedContact = ContactBuilder.instance(objectContext, college, "expected contact").build()
        TutorRoleBuilder.instance(objectContext, expectedContact, targetClass).build()

        CourseClass otherClass = course.cCourseClass("otherClass").get()
        Contact otherClassContact = ContactBuilder.instance(objectContext, college, "otherClass tutor").build()
        TutorRoleBuilder.instance(objectContext, otherClassContact, otherClass).build()

        Contact simpleContact = ContactBuilder.instance(objectContext, college, "simple contact").build()

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
