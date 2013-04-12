package ish.oncourse.solr;

import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.search.SolrQueryBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Calendar;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.Test;

public class SolrCourseCoreTest extends CustomizedAbstractSolrTestCase {
	private static final String COURSES_CORE_NAME = "courses";
	private static final String EXPECTED_GENERATED_QUERY_STRING = "qt=standard&fl=id,name,course_loc,score&start=0&rows=100&fq=+collegeId:1 +doctype:course " +
		"end:[NOW TO *]&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&qq=(*:*)" +
		"&sort=score desc,startDate asc,name asc&debugQuery=false";
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
	private static final String COURSE_LOCATION_FIELD_NAME = "course_loc";
	private static final String SCORE_FIELD_NAME = "score";
	
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        prepareCore(COURSES_CORE_NAME);
    }

    @Test
    public void testEmptyIndex() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrQueryBuilder.FILTER_TEMPLATE_ALL);
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }
    
    private SolrInputDocument prepareInitCourseDocument() {
    	SolrInputDocument document = new SolrInputDocument();
    	document.setField(ID_FIELD_NAME, "1");
    	document.setField(DOCTYPE_FIELD_NAME, "course");
    	document.setField(COLLEGE_ID_FIELD_NAME, 1);
    	document.setField(NAME_FIELD_NAME, "courseTest Name");
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
    	document.setField(COURSE_LOCATION_FIELD_NAME, Arrays.asList(TEST_LOCATION_1, TEST_LOCATION_2));
    	document.setField(COURSE_SUBURB_FIELD_NAME, "courseTestSuburb 0001");
    	document.setField(COURSE_POSTCODE_FIELD_NAME, "1");
    	Float price = 2.0f;
    	document.setField(COURSE_PRICE_FIELD_NAME, price);
    	Long tagId = 1l;
    	document.setField(COURSE_TAG_FIELD_NAME, tagId);
    	return document;
    }
    
    @Test
    public void testDistanceFiltering() throws SolrServerException, IOException {
    	SolrInputDocument document = prepareInitCourseDocument();
    	//add document to server
    	server.add(document);
        server.commit();
        
    	//prepare data for distance filtering
        SearchParams searchParams = new SearchParams();
        //set the default 100km distance for test
		searchParams.setKm(SearchParamsParser.parseKm(null));
		SolrDocumentList solrSuburbs = new SolrDocumentList();
		SolrDocument suburb = new SolrDocument();
		suburb.addField(SolrQueryBuilder.PARAMETER_loc, document.getFieldValues(COURSE_LOCATION_FIELD_NAME).iterator().next());
		suburb.addField(SolrQueryBuilder.FIELD_postcode, 
			SearchParamsParser.convertPostcodeParameterToLong((String) document.getFieldValue(COURSE_POSTCODE_FIELD_NAME)));
		solrSuburbs.add(suburb);
		searchParams.setNear(solrSuburbs);
		
		//check distance filtering
		SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder(searchParams, ((Integer) document.getFieldValue(COLLEGE_ID_FIELD_NAME)).toString(), 0, 5);
		SolrParams params = solrQueryBuilder.create();
		String value = URLDecoder.decode(params.toString(), "UTF-8");
        System.out.println(value);
        
        QueryResponse response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        SolrDocument result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("Course id should be equal to original document", document.getFieldValue(ID_FIELD_NAME), result.getFieldValue(ID_FIELD_NAME));
        assertEquals("Course name should be equal to original document", document.getFieldValue(NAME_FIELD_NAME), 
        	result.getFieldValue(NAME_FIELD_NAME));
        Float score = (Float) result.getFieldValue(SCORE_FIELD_NAME);
        System.out.println(String.format("Calculated score value = %s", score));
        assertEquals("Calculated score for distance filtering should be equal to 1.9999998", Float.valueOf(1.9999998f), score);
        
        //and now try again with updated location
        searchParams = new SearchParams();
        searchParams.setKm(SearchParamsParser.parseKm("10"));
		solrSuburbs = new SolrDocumentList();
		suburb = new SolrDocument();
		suburb.addField(SolrQueryBuilder.PARAMETER_loc, TEST_LOCATION_3);
		suburb.addField(SolrQueryBuilder.FIELD_postcode, 
			SearchParamsParser.convertPostcodeParameterToLong((String) document.getFieldValue(COURSE_POSTCODE_FIELD_NAME)));
		solrSuburbs.add(suburb);
		searchParams.setNear(solrSuburbs);
		
		solrQueryBuilder = new SolrQueryBuilder(searchParams, ((Integer) document.getFieldValue(COLLEGE_ID_FIELD_NAME)).toString(), 0, 5);
		params = solrQueryBuilder.create();
        value = URLDecoder.decode(params.toString(), "UTF-8");
        System.out.println(value);
        
        response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }
    
    @Test
    public void testRealWebSiteSearch() throws SolrServerException, IOException {
    	SolrInputDocument document = prepareInitCourseDocument();
    	//test the real calls
        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder(new SearchParams(), 
        	((Integer) document.getFieldValue(COLLEGE_ID_FIELD_NAME)).toString(), 0, 100);
        SolrParams params = solrQueryBuilder.create();
        String value = URLDecoder.decode(params.toString(), "UTF-8");
        assertEquals("Commons parameters",  EXPECTED_GENERATED_QUERY_STRING, value);
        
        //check that document not exist
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
        
        //add document to server
    	server.add(document);
        server.commit();
        
        response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        SolrDocument result = response.getResults().get(0);
        assertNotNull("Result document should not be empty", result);
        assertEquals("Course id should be equal to original document", document.getFieldValue(ID_FIELD_NAME), result.getFieldValue(ID_FIELD_NAME));
        assertEquals("Course name should be equal to original document", document.getFieldValue(NAME_FIELD_NAME), 
        	result.getFieldValue(NAME_FIELD_NAME));
        //get the 4 digits after dot because score is runtime calculated value
        Float score = (Float) result.getFieldValue(SCORE_FIELD_NAME);
        System.out.println(String.format("Calculated score value = %s", score));//"0.5792748"
        assertTrue("Calculated score should be more then 0.5791", score.compareTo(0.5791f) > 0);
        assertTrue("Calculated score should be less then 0.5794", score.compareTo(0.5794f) < 0);
        assertNotNull("Course location should not be empty", result.getFieldValues(COURSE_LOCATION_FIELD_NAME));
        assertFalse("Course location should not be empty", result.getFieldValues(COURSE_LOCATION_FIELD_NAME).isEmpty());
        assertEquals("Course should have 2 locations", 2, result.getFieldValues(COURSE_LOCATION_FIELD_NAME).size());
    }
        
    @Test
    public void testAddReadDeleteDocuments() throws SolrServerException, IOException {
    	SolrInputDocument document = prepareInitCourseDocument();
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
}
