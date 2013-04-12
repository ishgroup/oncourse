package ish.oncourse.solr;

import java.io.IOException;
import java.net.URLDecoder;

import ish.oncourse.services.search.SolrQueryBuilder;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;

public class SolrTagsCoreTest extends CustomizedAbstractSolrTestCase {
	private static final String TAGS_CORE_NAME = "tags";
	
	@Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        prepareCore(TAGS_CORE_NAME);
    }

    @Test
    public void testEmptyIndex() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrQueryBuilder.FILTER_TEMPLATE_ALL);
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }
    
    private SolrInputDocument prepareInitTagDocument() {
    	SolrInputDocument document = new SolrInputDocument();
    	document.setField(ID_FIELD_NAME, "1");
    	document.setField(DOCTYPE_FIELD_NAME, "tag");
    	document.setField(COLLEGE_ID_FIELD_NAME, 1);
    	document.setField(NAME_FIELD_NAME, "tagTest Name");
    	return document;
    }
    
    @Test
    public void testAddReadDeleteDocuments() throws SolrServerException, IOException {
    	SolrInputDocument document = prepareInitTagDocument();
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
    	SolrInputDocument document = prepareInitTagDocument();
    	SolrParams params = new SolrQuery(NAME_FIELD_NAME + ":tagTest");
        String value = URLDecoder.decode(params.toString(), "UTF-8");
        System.out.println(value);

        //check that document not exist
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
        
        //add document to server
    	server.add(document);
        server.commit();
        
        SolrInputDocument document2 = prepareInitTagDocument();
        document2.setField(ID_FIELD_NAME, "2");
        document2.setField(NAME_FIELD_NAME, "tagTest Name2");
        
        //add document to server
    	server.add(document2);
        server.commit();
        
        response = server.query(params);
        assertEquals(2L, response.getResults().getNumFound());
        SolrDocument result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("First result should match the first document name", document.getFieldValue(NAME_FIELD_NAME), result.getFieldValue(NAME_FIELD_NAME));
        assertEquals("First result should be the document with first index", document.getFieldValue(ID_FIELD_NAME), result.getFieldValue(ID_FIELD_NAME));
        result = response.getResults().get(1);
        assertEquals("Second result should match the second document name", document2.getFieldValue(NAME_FIELD_NAME), result.getFieldValue(NAME_FIELD_NAME));
        assertEquals("Second result should be the document with second index", document2.getFieldValue(ID_FIELD_NAME), result.getFieldValue(ID_FIELD_NAME));
                
        //update params to match both suburbs
        params = SolrQueryBuilder.createSearchSuburbByLocationQuery("Name2");
        value = URLDecoder.decode(params.toString(), "UTF-8");
        System.out.println(value);
        
        response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("Second result should match the second document name", document2.getFieldValue(NAME_FIELD_NAME), result.getFieldValue(NAME_FIELD_NAME));
        assertEquals("Second result should be the document with second index", document2.getFieldValue(ID_FIELD_NAME), result.getFieldValue(ID_FIELD_NAME));
    }

}
