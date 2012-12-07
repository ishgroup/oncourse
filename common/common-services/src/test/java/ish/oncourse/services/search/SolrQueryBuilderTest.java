package ish.oncourse.services.search;

import ish.oncourse.model.Tag;
import ish.oncourse.util.FormatUtils;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.services.search.SearchParamsParser.DATE_FORMAT_FOR_AFTER_BEFORE;
import static org.junit.Assert.*;

public class SolrQueryBuilderTest {
    private static final String GEOFILTER_QUERY = "qt=standard&fl=id,name,course_loc,score&start=5&rows=10&fq=+collegeId:2 +doctype:course +end:[NOW TO *]&fq={!geofilt}&sfield=course_loc&pt=-1.1,2.2&d=100.0&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(geodist(),1,10,5)&qq=(*:*)&sort=score desc,startDate asc,name asc";
	private static final String EXPECTED_RESULT_VALUE = "qt=standard&fl=id,name,course_loc,score&start=0&rows=100&fq=+collegeId:1 +doctype:course +end:[NOW TO *]&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(ms(startDate,NOW-1YEAR/DAY),1.15e-8,1,1)&qq=((detail:(%s)^1 || tutor:(%s)^5 || course_code:(%s)^30 || name:(%s)^20) AND price:[* TO 1999.99] AND when:DAY AND when:TIME AND class_start:[2012-01-01T12:00:00Z TO *] AND end:[NOW TO 2012-01-01T12:00:00Z] AND (tagId:0 || tagId:1 || tagId:2 || tagId:3 || tagId:4 || tagId:5))&sort=score desc,startDate asc,name asc";
	private static final String EXPECTED_AFTER_REPLACEMENT_S_PARAM = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19";
	private static final String DIGITS_SEPARATED_BY_ALL_REPLACED_SOLR_SYNTAX_CHARACTERS = "1!2^3(4)5{6}7[8]9:10\"11?12+13~14*15|16&17;18\\19";

	@Test
	public void testSuburbFiltering() throws UnsupportedEncodingException {
		SearchParamsParser parser = new SearchParamsParser(null, null, null);
		SearchParams searchParams = new SearchParams();
		searchParams.setKm(parser.parseKm(null));
		assertNull("if no param passed km should be null",searchParams.getKm());
		searchParams.setKm(parser.parseKm(Integer.valueOf(Double.valueOf(SearchService.MAX_DISTANCE - 1).intValue()).toString()));
		assertNotNull("Km should not be null", searchParams.getKm());
		assertEquals("Km less then 100.0 should be set as is", Double.valueOf(SearchService.MAX_DISTANCE - 1), searchParams.getKm());
		searchParams.setKm(parser.parseKm(Integer.valueOf(Double.valueOf(SearchService.MAX_DISTANCE + 1).intValue()).toString()));
		assertNotNull("Km should not be null", searchParams.getKm());
		assertEquals("Km more then 100.0 should be replaced with max distance", Double.valueOf(SearchService.MAX_DISTANCE), searchParams.getKm());
		SolrDocumentList solrSuburbs = new SolrDocumentList();
		SolrDocument suburb = new SolrDocument();
		searchParams.setNear(solrSuburbs);
		suburb.addField(SolrQueryBuilder.PARAMETER_loc, "-1.1,2.2");
		suburb.addField(SolrQueryBuilder.FIELD_postcode, "224000");
		solrSuburbs.add(suburb);
		searchParams.setNear(solrSuburbs);
		assertFalse("1 suburb should be inside", searchParams.getSuburbs().isEmpty());
		
		SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder(searchParams,"2",5,10);
		String value = URLDecoder.decode(solrQueryBuilder.create().toString(), "UTF-8");
		assertEquals("Commons parameters", GEOFILTER_QUERY, value);
	}
	
	@SuppressWarnings("serial")
	@Test
    public void testCreate() throws UnsupportedEncodingException, ParseException {
        SearchParams searchParams = new SearchParams();
        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder(searchParams,"1",0,100);
        String value = URLDecoder.decode(solrQueryBuilder.create().toString(), "UTF-8");
        assertEquals("Commons parameters",  "qt=standard&fl=id,name,course_loc,score&start=0&rows=100&fq=+collegeId:1 +doctype:course +end:[NOW TO *]&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(ms(startDate,NOW-1YEAR/DAY),1.15e-8,1,1)&qq=(*:*)&sort=score desc,startDate asc,name asc", value);
        System.out.println(value);

        searchParams.setAfter(FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, "UTC").parse("20120101"));
        searchParams.setBefore(FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, "UTC").parse("20120101"));
        searchParams.setS(DIGITS_SEPARATED_BY_ALL_REPLACED_SOLR_SYNTAX_CHARACTERS);
        searchParams.setPrice(1999.99d);
        searchParams.setDay("DAY");
        searchParams.setTime("TIME");
        searchParams.setSubject(new Tag(){
            @Override
            public Long getId() {
                return 0L;
            }

            @Override
            public List<Tag> getAllWebVisibleChildren() {

                ArrayList<Tag> list = new ArrayList<Tag>();
                for (int i = 0; i < 5; i++) {
                    final long id = i+1;
                    Tag tag = new Tag()
                    {
                        @Override
                        public Long getId() {
                            return id;
                        }
                    };
                    list.add(tag);
                }
                return list;
            }
        });

        solrQueryBuilder = new SolrQueryBuilder(searchParams,"1",0,100);
        ArrayList<String> filters = new ArrayList<String>();

        solrQueryBuilder.appendFilterS(filters);
        assertEquals("Test filters.size for filter SearchParam.s",1,filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.s", String.format(SolrQueryBuilder.FILTER_TEMPLATE_s, 
        	EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM, 
        		EXPECTED_AFTER_REPLACEMENT_S_PARAM),filters.get(0));

        filters.clear();
        solrQueryBuilder.appendFilterPrice(filters);
        assertEquals("Test filters.size for filter SearchParam.price", 1, filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.price", String.format(SolrQueryBuilder.FILTER_TEMPLATE_price, 1999.99), filters.get(0));

        filters.clear();
        solrQueryBuilder.appendFilterDay(filters);
        assertEquals("Test filters.size for filter SearchParam.day",1,filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.day", String.format(SolrQueryBuilder.FILTER_TEMPLATE_when, "DAY"),filters.get(0));

        filters.clear();
        solrQueryBuilder.appendFilterTime(filters);
        assertEquals("Test filters.size for filter SearchParam.time",1,filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.time", String.format(SolrQueryBuilder.FILTER_TEMPLATE_when, "TIME"),filters.get(0));

        filters.clear();
        solrQueryBuilder.appendFilterSubject(filters);
        assertEquals("Test filters.size for filter SearchParam.subject",1,filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.subject", "(tagId:0 || tagId:1 || tagId:2 || tagId:3 || tagId:4 || tagId:5)",filters.get(0));

        filters.clear();
        solrQueryBuilder.appendFilterAfter(filters);
        assertEquals("Test filters.size for filter SearchParam.after",1,filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.after", "class_start:[2012-01-01T12:00:00Z TO *]",filters.get(0));

        filters.clear();
        solrQueryBuilder.appendFilterBefore(filters);
        assertEquals("Test filters.size for filter SearchParam.before",1,filters.size());
        assertEquals("Test filters.get(0) for filter SearchParam.before", "end:[NOW TO 2012-01-01T12:00:00Z]",filters.get(0));

        value = URLDecoder.decode(solrQueryBuilder.create().toString(), "UTF-8");
        String expectedValue = String.format(EXPECTED_RESULT_VALUE, EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM, 
        	EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM);
        assertEquals("Query parameters", expectedValue, value);
        System.out.println(value);

    }

}
