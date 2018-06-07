package ish.oncourse.solr.suburbs

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SSuburb
import org.apache.solr.SolrTestCaseJ4
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.response.QueryResponse
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * User: akoiro
 * Date: 13/3/18
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
class SuburbsConfigTest extends SolrTestCaseJ4 {
    static {
        InitSolr.INIT_STATIC_BLOCK()
    }

    private InitSolr initSolr
    private SolrClient solrClient

    @Before
    void before() throws Exception {
        initSolr = InitSolr.suburbsCore()
        initSolr.init()
        solrClient = new EmbeddedSolrServer(h.getCore())
    }

    @Test
    void test_df_and_q_op() throws IOException, SolrServerException {
        SSuburb suburb = new SSuburb().with {
            it.id = "id"
            it.postcode = "postcode"
            it.suburb = "suburb"
            it.state = "state"
            it.loc = "1.0,1.0"
            it
        }
        solrClient.addBean(suburb)
        solrClient.commit()

        QueryResponse response = solrClient.query(new SolrQuery( 'suburb:sub*'))
        List<SSuburb> results = response.getBeans(SSuburb)
        assertEquals(1, results.size())
        assertEquals(suburb, results.get(0))
    }

    @After
    void after() {
        solrClient.close()
    }

}
