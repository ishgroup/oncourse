package ish.oncourse.solr.model;

import ish.oncourse.solr.InitSolr;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * The first test which shows how we can populate SolrCourse document to the solr index
 */
public class SolrCourseIndexTest extends SolrTestCaseJ4 {
    static  {
        InitSolr.INIT_STATIC_BLOCK();
    }

    private static InitSolr initSolr;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initSolr = InitSolr.coursesCore();
        initSolr.init();
    }

    @Test
    public void test() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore());
        SCourse expected = new SCourse();
        expected.setId(Long.valueOf(1).toString());
        expected.setCollegeId(1);
        expected.setName("Course1");
        expected.setDetail("Course1 Details");
        expected.setCode("COURSE1");
        expected.setStartDate(new Date());

        solrClient.addBean(expected);
        solrClient.commit();

        SCourse actual = solrClient.query("courses", new SolrQuery("*:*")).getBeans(SCourse.class).get(0);
        assertEquals(expected, actual);
    }
}
