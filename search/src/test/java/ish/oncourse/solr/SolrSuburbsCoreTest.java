package ish.oncourse.solr;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;

import ish.oncourse.services.search.SolrQueryBuilder;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;

public class SolrSuburbsCoreTest extends CustomizedAbstractSolrTestCase {
	private static final String SUBURBS_CORE_NAME = "suburbs";
	
	private static final String SUBURBS_LOCATION_FIELD_NAME = "loc";
	private static final String SUBURBS_POSTCODE_FIELD_NAME = "postcode";
	private static final String SUBURBS_SUBURB_FIELD_NAME = "suburb";
	
	@Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        prepareCore(SUBURBS_CORE_NAME);
    }

    @Test
    public void testEmptyIndex() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrQueryBuilder.FILTER_TEMPLATE_ALL);
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }
    
    private SolrInputDocument prepareInitSuburbDocument() {
    	SolrInputDocument document = new SolrInputDocument();
    	document.setField(ID_FIELD_NAME, "1");
    	document.setField(DOCTYPE_FIELD_NAME, "suburb");
    	document.setField(SUBURBS_LOCATION_FIELD_NAME, Arrays.asList(TEST_LOCATION_1));
    	document.setField(SUBURBS_SUBURB_FIELD_NAME, "101 TestSuburb");
    	document.setField(SUBURBS_POSTCODE_FIELD_NAME, "101");
    	return document;
    }
    
    @Test
    public void testAddReadDeleteDocuments() throws SolrServerException, IOException {
    	SolrInputDocument document = prepareInitSuburbDocument();
    	//add document to server
    	server.add(document);
        server.commit();
        
        //read the data from the server
        SolrParams params = new SolrQuery(SolrQueryBuilder.FILTER_TEMPLATE_ALL);
        QueryResponse response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        
        //delete the document
        server.deleteById((String) document.getFieldValue(ID_FIELD_NAME));
        server.commit();
        
        //check that document deleted
        response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }
    
    @Test
    public void testRealWebSiteSearch() throws SolrServerException, IOException {
    	SolrInputDocument document = prepareInitSuburbDocument();
    	SolrParams params = SolrQueryBuilder.createSearchSuburbByLocationQuery((String) document.getFieldValue(SUBURBS_SUBURB_FIELD_NAME));
        String value = URLDecoder.decode(params.toString(), "UTF-8");
        System.out.println(value);

        //check that document not exist
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
        
        //add document to server
    	server.add(document);
        server.commit();
        
        SolrInputDocument document2 = prepareInitSuburbDocument();
        document2.setField(ID_FIELD_NAME, "2");
        document2.setField(SUBURBS_SUBURB_FIELD_NAME, "102 TestSuburb");
        document2.setField(SUBURBS_POSTCODE_FIELD_NAME, "102");
        
        //add document to server
    	server.add(document2);
        server.commit();
        
        response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        SolrDocument result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("Suburb name must be equal", document.getFieldValue(SUBURBS_SUBURB_FIELD_NAME), result.getFieldValue(SUBURBS_SUBURB_FIELD_NAME));
        assertEquals("Suburb postcode must be equal", document.getFieldValue(SUBURBS_POSTCODE_FIELD_NAME), result.getFieldValue(SUBURBS_POSTCODE_FIELD_NAME));
                
        //update params to match both suburbs
        params = SolrQueryBuilder.createSearchSuburbByLocationQuery("TestSuburb");
        value = URLDecoder.decode(params.toString(), "UTF-8");
        System.out.println(value);
        
        response = server.query(params);
        assertEquals(2L, response.getResults().getNumFound());
        result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("Suburb name must be equal", document.getFieldValue(SUBURBS_SUBURB_FIELD_NAME), result.getFieldValue(SUBURBS_SUBURB_FIELD_NAME));
        assertEquals("Suburb postcode must be equal", document.getFieldValue(SUBURBS_POSTCODE_FIELD_NAME), result.getFieldValue(SUBURBS_POSTCODE_FIELD_NAME));
    }
}
