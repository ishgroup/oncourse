package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 1/9/18.
 */
class SolrCourseClassQueryWithPrice  extends ASolrTest{
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
    void testSortCoursesWithPrice(){
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        cCollege.newCourse("course1").newCourseClassWithSessions("cheapest").feeExTax(50).build()
        cCollege.newCourse("course2").newCourseClassWithSessions("90 dollars").feeExTax(99).build()
        cCollege.newCourse("course3").newCourseClassWithSessions("105 dollars").feeExTax(80).feeTax(40).build()
        cCollege.newCourse("course4").newCourseClassWithSessions("110 dollars").feeExTax(110).build()
        cCollege.newCourse("course5").newCourseClassWithSessions("withoutPrice").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }
        
        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", price: 100), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())

        assertNotNull(actualSCourses.find {c -> c.name == "course1" })
        assertNotNull(actualSCourses.find {c -> c.name == "course2" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        assertNull(actualSCourses.find {c -> c.name == "course4" })
        assertNotNull(actualSCourses.find {c -> c.name == "course5" })


        //change some prices and reindex again to check correct reindexing
        cCollege.cCourses.get("COURSE2").classes.first.feeExTax(130).build()
        cCollege.cCourses.get("COURSE4").classes.first.feeExTax(70).build()
        
        job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", price: 100), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())

        assertNotNull(actualSCourses.find {c -> c.name == "course1" })
        assertNull(actualSCourses.find {c -> c.name == "course2" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        assertNotNull(actualSCourses.find {c -> c.name == "course4" })
        assertNotNull(actualSCourses.find {c -> c.name == "course5" })
    }

    @After
    void after(){
        Schedulers.shutdown()
        testContext.close()
    }
}
