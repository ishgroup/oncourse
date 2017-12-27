package ish.oncourse.services.search;

import ish.oncourse.model.Tag;
import ish.oncourse.solr.query.DayOption;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.SolrQueryBuilder;
import ish.oncourse.solr.query.Suburb;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ish.oncourse.services.search.SearchParamsParser.DATE_FORMAT_FOR_AFTER_BEFORE;
import static org.junit.Assert.*;

public class SolrQueryBuilderTest {

	private static final String SUBJECT_QUERY = "(tagId:0 || tagId:1 || tagId:2 || tagId:3 || tagId:4 || tagId:5)";
	private static final String TAG1_QUERY = "(tagId:5 || tagId:6 || tagId:7 || tagId:8 || tagId:9 || tagId:10)";
	private static final String TAG2_QUERY = "(tagId:5 || tagId:6 || tagId:7 || tagId:8 || tagId:9 || tagId:10)";


	private static final String GEOFILTER_QUERY = "qt=edismax&fl=id,name,course_loc,score&start=5&rows=10&fq=+collegeId:2 +doctype:course end:[NOW TO *]&" +
			"fq={!score=distance}{!geofilt sfield=course_loc pt=-1.1,2.2 d=0.9044289887579477}&q={!boost b=$boostfunction v=$qq}&" +
			"boostfunction=recip(query($geofq),1,10,5)&geofq={!score=distance}{!geofilt sfield=course_loc pt=-1.1,2.2 d=0.9044289887579477}&" +
			"qq=(*:*)&sort=score desc,startDate asc,name asc";
	private static final String EXPECTED_RESULT_VALUE = "qt=edismax&fl=id,name,course_loc,score&start=0&rows=100&fq=+collegeId:1 +doctype:course " +
			"end:[NOW TO *]&fq=%s" +
			"&fq=%s" +
			"&fq=%s&q={!boost b=$boostfunction v=$qq}" +
			"&boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&qq=(%s " +
			"AND price:[* TO 1999.99] AND when:Monday AND when:TIME AND class_start:[2012-01-01T00:00:00Z TO 2012-01-01T00:00:00Z] AND siteId:1000)" +
			"&sort=score desc,startDate asc,name asc";
	private static final String EXPECTED_AFTER_REPLACEMENT_S_PARAM = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19";
	private static final String DIGITS_SEPARATED_BY_ALL_REPLACED_SOLR_SYNTAX_CHARACTERS = "1!2^3(4)5{6}7[8]9:10\"11?12+13~14*15|16&17;18\\19";

	@Test
	public void testConvertPostcodeParameterToLong() {
		final String POSTCODE_1234 = "1234", POSTCODE_0700 = "0700", POSTCODE_0050 = "0050", POSTCODE_0003 = "0003", POSTCODE_0000 = "0000";
		assertEquals("Postcode should not changes after the conversion", POSTCODE_1234, SuburbParser.convertPostcodeParameterToLong(POSTCODE_1234));
		assertEquals("Postcode starting from 0 like '0700' should be converted to 700 string", "700",
				SuburbParser.convertPostcodeParameterToLong(POSTCODE_0700));
		assertEquals("Postcode starting from 0 like '0050' should be converted to 50 string", "50",
				SuburbParser.convertPostcodeParameterToLong(POSTCODE_0050));
		assertEquals("Postcode starting from 0 like '0003' should be converted to 3 string", "3",
				SuburbParser.convertPostcodeParameterToLong(POSTCODE_0003));
		assertEquals("Postcode '0000' should be converted to 0 string", "0", SuburbParser.convertPostcodeParameterToLong(POSTCODE_0000));
	}

	@Test
	public void testSuburbFiltering() throws UnsupportedEncodingException {
		//SearchParamsParser parser = new SearchParamsParser(null, null, null);
		SearchParams searchParams = new SearchParams();
		Double km = SuburbParser.parseKm(null);
		assertNull("if no param passed km should be null", km);
		km = SuburbParser.parseKm(Integer.valueOf(Double.valueOf(SearchService.MAX_DISTANCE - 1).intValue()).toString());
		assertNotNull("Km should not be null", km);
		assertEquals("Km less then maximum should be set as is", Double.valueOf(SearchService.MAX_DISTANCE - 1), km);
		km = SuburbParser.parseKm(Integer.valueOf(0).toString());
		assertNotNull("Km should not be null", km);
		assertEquals("Km less then minumal value should be replaced with minimal value", Double.valueOf(SearchService.MIN_DISTANCE), km);
		km = SuburbParser.parseKm(Integer.valueOf(Double.valueOf(SearchService.MAX_DISTANCE + 1).intValue()).toString());
		assertNotNull("Km should not be null", km);
		assertEquals("Km more then maximum should be replaced with max distance", Double.valueOf(SearchService.MAX_DISTANCE), km);

		//set the default 100km distance for test
		SolrDocumentList solrSuburbs = new SolrDocumentList();
		SolrDocument suburb = new SolrDocument();
		suburb.addField(SolrQueryBuilder.PARAMETER_loc, "-1.1,2.2");
		suburb.addField(SolrQueryBuilder.FIELD_postcode, "224000");
		solrSuburbs.add(suburb);
		searchParams.addSuburb(Suburb.valueOf(StringUtils.EMPTY, null, solrSuburbs.get(0)));
		assertFalse("1 suburb should be inside", searchParams.getSuburbs().isEmpty());

		SolrQueryBuilder solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "2", 5, 10);
		String value = URLDecoder.decode(solrQueryBuilder.build().toString(), "UTF-8");
		assertEquals("Commons parameters", GEOFILTER_QUERY, value);
	}

	@Test
	public void testCreate() throws UnsupportedEncodingException, ParseException {
		SearchParams searchParams = new SearchParams();
		SolrQueryBuilder solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", 0, 100);
		String value = URLDecoder.decode(solrQueryBuilder.build().toString(), "UTF-8");

		assertEquals("Commons parameters", "qt=edismax&fl=id,name,course_loc,score&start=0&rows=100&fq=+collegeId:1 +doctype:course end:[NOW TO *]" +
				"&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&qq=(*:*)" +
				"&sort=score desc,startDate asc,name asc", value);

		searchParams.setAfter(FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, "UTC").parse("20120101"));
		searchParams.setBefore(FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, "UTC").parse("20120101"));
		searchParams.setS(DIGITS_SEPARATED_BY_ALL_REPLACED_SOLR_SYNTAX_CHARACTERS);
		searchParams.setPrice(1999.99d);
		searchParams.setDay(DayOption.mon);
		searchParams.setTime("TIME");
		searchParams.setSiteId(1000L);
		searchParams.setSubject(new Tag() {
			@Override
			public Long getId() {
				return 0L;
			}

			@Override
			public List<Tag> getAllWebVisibleChildren() {
				ArrayList<Tag> list = new ArrayList<>();
				for (int i = 0; i < 5; i++) {
					final long id = i + 1;
					Tag tag = new Tag() {
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

		searchParams.addTag(new Tag() {
			@Override
			public Long getId() {
				return 10L;
			}

			@Override
			public List<Tag> getAllWebVisibleChildren() {
				ArrayList<Tag> list = new ArrayList<>();
				for (int i = 10; i < 15; i++) {
					final long id = i + 1;
					Tag tag = new Tag() {
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

		searchParams.addTag(new Tag() {
			@Override
			public Long getId() {
				return 5L;
			}

			@Override
			public List<Tag> getAllWebVisibleChildren() {
				ArrayList<Tag> list = new ArrayList<>();
				for (int i = 5; i < 10; i++) {
					final long id = i + 1;
					Tag tag = new Tag() {
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

		solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", 0, 100);
		ArrayList<String> filters = new ArrayList<>();

		solrQueryBuilder.appendFilterS(filters);
		assertEquals("Test filters.size for filter SearchParam.s", 1, filters.size());

		String fullValue = String.format("\"%s\"", EXPECTED_AFTER_REPLACEMENT_S_PARAM);
		String expectedFilterForS = String.format(SolrQueryBuilder.EXTENED_FILTER_TEMPLATE_s,
				String.format(SolrQueryBuilder.FILTER_TEMPLATE_s,
						EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM, EXPECTED_AFTER_REPLACEMENT_S_PARAM),
				String.format(SolrQueryBuilder.FILTER_TEMPLATE_s,
						fullValue, fullValue, fullValue, fullValue));

		assertEquals("Test filters.get(0) for filter SearchParam.s", expectedFilterForS, filters.get(0));

		filters.clear();
		solrQueryBuilder.appendFilterPrice(filters);
		assertEquals("Test filters.size for filter SearchParam.price", 1, filters.size());
		assertEquals("Test filters.get(0) for filter SearchParam.price", String.format(SolrQueryBuilder.FILTER_TEMPLATE_price, 1999.99), filters.get(0));

		filters.clear();
		solrQueryBuilder.appendFilterDay(filters);
		assertEquals("Test filters.size for filter SearchParam.day", 1, filters.size());
		assertEquals("Test filters.get(0) for filter SearchParam.day", String.format(SolrQueryBuilder.FILTER_TEMPLATE_when, DayOption.mon.getFullName()), filters.get(0));

		filters.clear();
		solrQueryBuilder.appendFilterTime(filters);
		assertEquals("Test filters.size for filter SearchParam.time", 1, filters.size());
		assertEquals("Test filters.get(0) for filter SearchParam.time", String.format(SolrQueryBuilder.FILTER_TEMPLATE_when, "TIME"), filters.get(0));

		SolrQuery q = new SolrQuery();
		solrQueryBuilder.appendFilterTag(q);
		assertEquals("Test filter query length for filter SearchParam.tag1 and SearchParam.tag2 abd  SearchParam.subject", 3, q.getFilterQueries().length);
		List<String> tagQueries = Arrays.asList(q.getFilterQueries());
		assertTrue("Test filter query first element for filter SearchParam.tag1", tagQueries.contains(TAG1_QUERY));
		assertTrue("Test filter query first element for filter SearchParam.tag2", tagQueries.contains(TAG2_QUERY));
		assertTrue("Test filter query first element for filter SearchParam.subject", tagQueries.contains(SUBJECT_QUERY));

		filters.clear();
		solrQueryBuilder.appendFilterStartBetween(filters);
		assertEquals("Test filters.size for filter SearchParam.after", 1, filters.size());
		assertEquals("Test filters.get(0) for filter SearchParam.after", "class_start:[2012-01-01T00:00:00Z TO 2012-01-01T00:00:00Z]", filters.get(0));

		value = URLDecoder.decode(solrQueryBuilder.build().toString(), "UTF-8");
		String expectedValue = String.format(EXPECTED_RESULT_VALUE,
				tagQueries.get(0),
				tagQueries.get(1),
				tagQueries.get(2),
				expectedFilterForS);
		assertEquals("Query parameters", expectedValue, value);

		//check that if tag1 or tag2 not specified everything works too
		searchParams.getTags().remove(0);
		solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", 0, 100);
		q = new SolrQuery();
		solrQueryBuilder.appendFilterTag(q);
		assertEquals("Test filter query length for filter SearchParam.tag2 and  SearchParam.subject", 2, q.getFilterQueries().length);
		assertTrue("Test filter query has element for filter SearchParam.tag2", Arrays.asList(q.getFilterQueries()).contains(TAG2_QUERY));
		assertTrue("Test filter query has element for filter SearchParam.subject", Arrays.asList(q.getFilterQueries()).contains(SUBJECT_QUERY));

		searchParams.getTags().remove(0);
		solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", 0, 100);
		q = new SolrQuery();
		solrQueryBuilder.appendFilterTag(q);
		assertEquals("Test filter query length for empty filter SearchParam.tag1 and tag2, only subject tag", 1, q.getFilterQueries().length);
		assertTrue("Test filter query has element for filter SearchParam.subject", Arrays.asList(q.getFilterQueries()).contains(SUBJECT_QUERY));
	}

	@Test
	public void testNulls() throws UnsupportedEncodingException, ParseException {
		SearchParams searchParams = new SearchParams();
		SolrQueryBuilder solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", null, null);
		String value = URLDecoder.decode(solrQueryBuilder.build().toString(), "UTF-8");

		assertEquals("Commons parameters", "qt=edismax&fl=id,name,course_loc,score&fq=+collegeId:1 +doctype:course end:[NOW TO *]" +
				"&q={!boost b=$boostfunction v=$qq}&boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&qq=(*:*)" +
				"&sort=score desc,startDate asc,name asc", value);
	}


	@Test
	public void testDayParameter() throws UnsupportedEncodingException {
		String template = "qt=edismax&" +
				"fl=id,name,course_loc,score&" +
				"fq=+collegeId:1 +doctype:course end:[NOW TO *]&" +
				"q={!boost b=$boostfunction v=$qq}&" +
				"boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&" +
				"qq=(when:%s)&" +
				"sort=score desc,startDate asc,name asc";

		SearchParams searchParams = new SearchParams();
		for (DayOption dayOption : DayOption.values()) {
			searchParams.setDay(dayOption);
			SolrQueryBuilder solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", null, null);
			String value = URLDecoder.decode(solrQueryBuilder.build().toString(), "UTF-8");
			assertEquals(String.format(template, dayOption.getFullName()), value);
		}
	}

	@Test
	public void testTutorIdParameter() throws UnsupportedEncodingException {
		String template = "qt=edismax&" +
				"fl=id,name,course_loc,score&" +
				"fq=+collegeId:1 +doctype:course end:[NOW TO *]&" +
				"q={!boost b=$boostfunction v=$qq}&" +
				"boostfunction=recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)&" +
				"qq=(tutorId:%d)&sort=score desc,startDate asc,name asc";

		SearchParams searchParams = new SearchParams();
		searchParams.setTutorId(1000L);
		SolrQueryBuilder solrQueryBuilder = SolrQueryBuilder.valueOf(searchParams, "1", null, null);
		String value = URLDecoder.decode(solrQueryBuilder.build().toString(), "UTF-8");
		assertEquals(String.format(template, 1000L), value);
	}
}
