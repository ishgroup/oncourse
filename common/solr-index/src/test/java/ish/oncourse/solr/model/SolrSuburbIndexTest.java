package ish.oncourse.solr.model;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.solr.InitSolr;
import ish.oncourse.solr.functions.suburb.Functions;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SolrSuburbIndexTest extends SolrTestCaseJ4 {
    static {
        InitSolr.INIT_STATIC_BLOCK();
    }

    private static InitSolr initSolr;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initSolr = InitSolr.suburbsCore();
        initSolr.init();
    }

    @Test
    public void test() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

        PostcodeDb postcodeDb = mock(PostcodeDb.class);
        when(postcodeDb.getPostcode()).thenReturn(2017L);
        when(postcodeDb.getSuburb()).thenReturn("Waterloo");
        when(postcodeDb.getState()).thenReturn("NSW");
        when(postcodeDb.getLat()).thenReturn(-33.9036972D);
        when(postcodeDb.getLon()).thenReturn(151.1986751D);

        SSuburb suburb = Functions.getGetSSuburb().apply(postcodeDb);

        solrClient.addBean(suburb);
        solrClient.commit();

        SSuburb actual = solrClient.query("suburbs", new SolrQuery("*:*")).getBeans(SSuburb.class).get(0);
        assertEquals(suburb, actual);

    }
}
