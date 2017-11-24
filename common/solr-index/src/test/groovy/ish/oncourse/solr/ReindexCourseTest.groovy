package ish.oncourse.solr

import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.SolrTestCaseJ4
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by alex on 11/22/17.
 */
class ReindexCourseTest extends SolrTestCaseJ4{
    private TestContext testContext
    private ObjectContext objectContext
    private static InitSolr initSolr

    @BeforeClass
    static void beforeClass() throws Exception {
        initSolr = InitSolr.coursesCore()
        initSolr.init()
    }

    @Test
    void testReindexCourse() throws IOException, SolrServerException {
        testContext = new TestContext()
        testContext.open()

       objectContext = testContext.getRuntime().newContext()

        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())


        SCourse expected = new SCourse()
        expected.setId(Long.valueOf(1).toString())
        expected.setCollegeId(1)
        expected.setName("Course1")
        expected.setDetail("Course1 Details")
        expected.setCode("COURSE1")
        expected.setStartDate(new Date())

        solrClient.addBean(expected)
        solrClient.commit()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()

        SCourse actual = solrClient.query("courses", new SolrQuery("Cou*")).getBeans(SCourse.class).get(0)
        assertEquals(expected, actual)

        testContext.close()
    }
}
