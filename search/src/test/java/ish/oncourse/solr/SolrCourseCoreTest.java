package ish.oncourse.solr;

import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SolrQueryBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Calendar;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrCourseCoreTest extends CustomizedAbstractSolrTestCase {
	private static final String EXPECTED_GENERATED_QUERY_STRING = "qt=standard&fl=id,name,course_loc,score&start=0&rows=100&fq=+collegeId:1 +doctype:course " +
		"end:[NOW TO *]&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&qq=(*:*)&sort=" +
		"score desc,startDate asc,name asc";
	private static final String COURSE_TAG_FIELD_NAME = "tagId";
	private static final String COURSE_PRICE_FIELD_NAME = "price";
	private static final String COURSE_POSTCODE_FIELD_NAME = "course_postcode";
	private static final String COURSE_SUBURB_FIELD_NAME = "course_suburb";
	private static final String COURSE_END_FIELD_NAME = "end";
	private static final String CLASS_START_FIELD_NAME = "class_start";
	private static final String COURSE_START_DATE_FIELD_NAME = "startDate";
	private static final String WHEN_FIELD_NAME = "when";
	private static final String TUTOR_FIELD_NAME = "tutor";
	private static final String CLASS_CODE_FIELD_NAME = "class_code";
	private static final String COURSE_DETAIL_FIELD_NAME = "detail";
	private static final String COURSE_CODE_FIELD_NAME = "course_code";
	private static final String COURSE_NAME_FIELD_NAME = "name";
	private static final String COLLEGE_ID_FIELD_NAME = "collegeId";
	private static final String DOCTYPE_FIELD_NAME = "doctype";
	private static final String COURSE_ID_FIELD_NAME = "id";
	private static final String COURSES_CORE_NAME = "courses";
	
    @Before
    @Override
    public void setUp() throws Exception {
    	System.setProperty("SOLR_DATA", "src/test/resources/");
		System.setProperty("solr.master.url", "http://localhost:8081/search-internal");
		System.setProperty("solr.master.enable", "true");
		System.setProperty("solr.slave.enable", "false");
		System.setProperty("solr.core.name", COURSES_CORE_NAME);
		System.setProperty("solr.poll", "1");
        super.setUp();
        testSolrHome = "src/test/resources/";
        configString = "src/main/resources/solr/courses/conf/solrconfig.xml";
        schemaString = "src/main/resources/solr/courses/conf/schema.xml";
        customInitCore(COURSES_CORE_NAME);
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
    }
    
    @After
    public void destroy() {
    	server.shutdown();
    }
            
    @Test
    public void testEmptyIndex() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrQueryBuilder.FILTER_TEMPLATE_ALL);
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }
        
    @Test
    public void testAddReadDeleteDocuments() throws SolrServerException, IOException {
    	SolrInputDocument document = new SolrInputDocument();
    	document.setField(COURSE_ID_FIELD_NAME, "1");
    	document.setField(DOCTYPE_FIELD_NAME, "course");
    	document.setField(COLLEGE_ID_FIELD_NAME, 1);
    	document.setField(COURSE_NAME_FIELD_NAME, "courseTest Name");
    	document.setField(COURSE_CODE_FIELD_NAME, "courseTestCode");
    	document.setField(COURSE_DETAIL_FIELD_NAME, "course Test Detail");
    	document.setField(CLASS_CODE_FIELD_NAME, "courseTestCode-courseClassTestCode");
    	document.setField(TUTOR_FIELD_NAME, "tutorTest Name");
    	document.setField(WHEN_FIELD_NAME, "monday weekend evening");
    	
    	Calendar cal = Calendar.getInstance();
    	document.setField(COURSE_START_DATE_FIELD_NAME, cal.getTime());
    	document.setField(CLASS_START_FIELD_NAME, cal.getTime());
    	cal.add(Calendar.HOUR, 2);
    	document.setField(COURSE_END_FIELD_NAME, cal.getTime());
    	    	
    	//course_loc
    	
    	document.setField(COURSE_SUBURB_FIELD_NAME, "courseTestSuburb 0001");
    	document.setField(COURSE_POSTCODE_FIELD_NAME, "1");
    	Float price = 2.0f;
    	document.setField(COURSE_PRICE_FIELD_NAME, price);
    	Long tagId = 1l;
    	document.setField(COURSE_TAG_FIELD_NAME, tagId);
    	
    	//now add document to server
    	server.add(document);
        server.commit();
        
        //read the data from the server
        SolrParams params = new SolrQuery(SolrQueryBuilder.FILTER_TEMPLATE_ALL);
        QueryResponse response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        
        //delete the document
        server.deleteById((String) document.getFieldValue(COURSE_ID_FIELD_NAME));
        server.commit();
        
        //check that document deleted
        response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
        
        //test the real calls
        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder(new SearchParams(),"1",0,100);
        params = solrQueryBuilder.create();
        String value = URLDecoder.decode(solrQueryBuilder.create().toString(), "UTF-8");
        assertEquals("Commons parameters",  EXPECTED_GENERATED_QUERY_STRING, value);
        
        //re-add the document
        server.add(document);
        server.commit();
        
        response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        SolrDocument result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("Course id should be equal to original document", document.getFieldValue(COURSE_ID_FIELD_NAME), 
        	result.getFieldValue(COURSE_ID_FIELD_NAME));
        assertEquals("Course name should be equal to original document", document.getFieldValue(COURSE_NAME_FIELD_NAME), 
        	result.getFieldValue(COURSE_NAME_FIELD_NAME));
        //get the 5 digits after dot because score is runtime calculated value
        Float score = (Float) result.getFieldValue("score");
        System.out.println(String.format("Calculated score value = %s", score));//"0.5792748"
        assertTrue("Calculated score should be more then 0.5792", score.compareTo(0.5792f) > 0);
        assertTrue("Calculated score should be less then 0.5793", score.compareTo(0.5793f) < 0);
        System.out.println("testAddAndReadDocuments passed");
    }
}
