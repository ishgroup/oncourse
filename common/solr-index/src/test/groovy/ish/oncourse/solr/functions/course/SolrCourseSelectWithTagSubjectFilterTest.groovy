package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.model.Course
import ish.oncourse.model.Tag
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 2/7/18.
 */
class SolrCourseSelectWithTagSubjectFilterTest extends ASolrTest{
    private TestContext testContext
    private ObjectContext objectContext
    private InitSolr initSolr
    private CCollege cCollege

    @Before
    void before() throws Exception {
        initSolr = InitSolr.coursesCore()
        initSolr.init()

        testContext = new TestContext()
        testContext.open()
        objectContext = testContext.getServerRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        cCollege = dataContext.newCollege()
    }
    
    @Test
    void testSortCoursesWithTagSubjectFilter() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())
        List<SCourse> actualSCourses

        Tag tag1 = cCollege.tag("Tag1")
        Tag tag11 = cCollege.tag("Tag11", false, false)
        Tag tag12 = cCollege.tag("Tag12", false)
        Tag tag2 = cCollege.tag("Tag2")
        Tag tag21 = cCollege.tag("Tag21", false)
        Tag tag3 = cCollege.tag("Tag3")

        cCollege.addTag(tag1, tag11, tag12)
        cCollege.addTag(tag2, tag21)

        Course course1 = cCollege.newCourse("course1").course
        Course course2 = cCollege.newCourse("course2").course
        Course course3 = cCollege.newCourse("course3").course
        Course course4 = cCollege.newCourse("course4").course
        Course course5 = cCollege.newCourse("course5").course
        Course course6 = cCollege.newCourse("course6").course
        Course course7 = cCollege.newCourse("course7").course
        Course course8 = cCollege.newCourse("course8").course
        Course course9 = cCollege.newCourse("course9").course
        
        cCollege.tagCourse(course1, tag1)
        cCollege.tagCourse(course2, tag11)
        cCollege.tagCourse(course3, tag12)
        cCollege.tagCourse(course4, tag2)
        cCollege.tagCourse(course5, tag21)
        cCollege.tagCourse(course6, tag3)
        
        cCollege.tagCourse(course7, tag1)
        cCollege.tagCourse(course7, tag2)

        cCollege.tagCourse(course8, tag11)
        cCollege.tagCourse(course8, tag21)

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", subject: tag1), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(3, actualSCourses.size())
        assertNotNull(actualSCourses.find {it.name == "course1"})
        assertNotNull(actualSCourses.find {it.name == "course3"})
        assertNotNull(actualSCourses.find {it.name == "course7"})
        actualSCourses.each { println it.name + " : " + it.score}

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", subject: tag21), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(2, actualSCourses.size())
        assertNotNull(actualSCourses.find {it.name == "course5"})
        assertNotNull(actualSCourses.find {it.name == "course8"})
        println actualSCourses.size()
        actualSCourses.each { println it.name + " : " + it.score}

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tags: Arrays.asList(tag1, tag2)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(1, actualSCourses.size())
        assertNotNull(actualSCourses.find {it.name == "course7"})
        println actualSCourses.size()
        actualSCourses.each { println it.name + " : " + it.score}
    }
    
    @After
    void after(){
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
