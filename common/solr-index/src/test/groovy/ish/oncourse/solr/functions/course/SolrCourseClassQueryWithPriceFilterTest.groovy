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
class SolrCourseClassQueryWithPriceFilterTest extends ASolrTest{
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
    void testSortCoursesWithPriceFilter(){
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        cCollege.newCourse("course1").newCourseClassWithSessions("cheapestPast", -5, -4).feeExTax(50).build()
        cCollege.newCourse("course2").newCourseClassWithSessions("cheapestCurrent", -1, 1).feeExTax(50).build()
        cCollege.newCourse("course3").newCourseClassWithSessions("cheapestFuture", 5, 6).feeExTax(50).build()
        
        cCollege.newCourse("course4").newCourseClassWithSessions("99 dollars past", -5, -4).feeExTax(99).build()
        cCollege.newCourse("course5").newCourseClassWithSessions("99 dollars current", -1, 1).feeExTax(99).build()
        cCollege.newCourse("course6").newCourseClassWithSessions("99 dollars future", 5, 6).feeExTax(99).build()
        
        cCollege.newCourse("course7").newCourseClassWithSessions("80 with tax past", -5, -4).feeExTax(80).feeTax(40).build()
        cCollege.newCourse("course8").newCourseClassWithSessions("80 with tax current", -1, 1).feeExTax(80).feeTax(40).build()
        cCollege.newCourse("course9").newCourseClassWithSessions("80 with tax future", 5, 6).feeExTax(80).feeTax(40).build()
        
        cCollege.newCourse("course10").newCourseClassWithSessions("110 dollars past", -5, -4).feeExTax(110).build()
        cCollege.newCourse("course11").newCourseClassWithSessions("110 dollars current", -1, 1).feeExTax(110).build()
        cCollege.newCourse("course12").newCourseClassWithSessions("110 dollars future", 5, 6).feeExTax(110).build()
        
        cCollege.newCourse("course13").newCourseClassWithSessions("withoutPrice past", -5, -4).build()
        cCollege.newCourse("course14").newCourseClassWithSessions("withoutPrice current", -1, 1).build()
        cCollege.newCourse("course15").newCourseClassWithSessions("withoutPrice future", 5, 6).build()

        cCollege.newCourse("course16").newSelfPacedClass("99 dollars selfpaced").feeExTax(99).build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }
        
        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", price: 100), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        //sort order is: current first, than selfpaced, than future. No past classes. price order isn't in use
        assertEquals(9, actualSCourses.size())
        assertTrue(actualSCourses.subList(0, 4).name.contains("course2"))
        assertTrue(actualSCourses.subList(0, 4).name.contains("course5"))
        assertTrue(actualSCourses.subList(0, 4).name.contains("course8"))
        assertTrue(actualSCourses.subList(0, 4).name.contains("course14"))
        assertTrue(actualSCourses.get(4).name == "course16")
        assertTrue(actualSCourses.subList(5, 9).name.contains("course3"))
        assertTrue(actualSCourses.subList(5, 9).name.contains("course6"))
        assertTrue(actualSCourses.subList(5, 9).name.contains("course9"))
        assertTrue(actualSCourses.subList(5, 9).name.contains("course15"))
    }

    @After
    void after(){
        Schedulers.shutdown()
        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
