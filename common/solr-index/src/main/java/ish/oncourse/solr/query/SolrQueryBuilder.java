package ish.oncourse.solr.query;

import ish.oncourse.model.Tag;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.common.params.CommonParams;

import java.util.*;
import java.util.stream.Collectors;

public class SolrQueryBuilder {
	static final String DIGIT_PATTERN = "(\\d)+";
	static final String SPACE_PATTERN = "[\\s]+";

	public static final double KM_IN_DEGREE_VALUE = 110.567;
	private static final String SPACE_REPLACEMENT_CHARACTER = " ";
	private static final String SOLR_SYNTAX_CHARACTERS_STRING = "[\\!\\^\\(\\)\\{\\}\\[\\]\\:\"\\?\\+\\~\\*\\|\\&\\;\\\\]";

	static final String QUERY_TYPE = "edismax";

	static final String PARAMETER_fl = "fl";
	static final String PARAMETER_geofq = "geofq";
	static final String PARAMETER_d = "d";
	public static final String PARAMETER_loc = "loc";
	static final String PARAMETER_BOOST_FUNCTION = "boostfunction";
	static final String PARAMETER_qq = "qq";

	public static final String FIELD_score = "score";
	public static final String FIELD_name = "name";
	/**
	 * CourseClass solr fields
	 */
	public static final String FIELD_class_start = "class_start";
	public static final String FIELD_end = "end";


	public static final String FIELD_startDate = "startDate";
	public static final String FIELD_suburb = "suburb";
	public static final String FIELD_postcode = "postcode";

	static final String PARAMETER_VALUE_fl = "id,name,course_loc";
	static final String PARAMETER_VALUE_sfield = "course_loc";


	static final String FILTER_TEMPLATE_collegeId = "+collegeId:%s +doctype:course end:[%s TO *]";
	public static final String FILTER_TEMPLATE_s = "(detail:(%s)^1 || tutor:(%s)^5 || course_code:(%s)^30 || name:(%s)^20)";
	public static final String EXTENED_FILTER_TEMPLATE_s = "%s || (%s)^100";
	public static final String FILTER_TEMPLATE_price = "price:[* TO %s]";
	public static final String FILTER_TEMPLATE_when = "when:%s";
	static final String FILTER_TEMPLATE_tagId = "tagId:%d";
	static final String FILTER_TEMPLATE_siteId = "siteId:%d";
	static final String FILTER_TEMPLATE_tutorId = "tutorId:%d";
	static final String FILTER_TEMPLATE_duration_eq = "duration:%d";
	static final String FILTER_TEMPLATE_duration_lt = "duration:[0 TO %d]";
	static final String FILTER_TEMPLATE_duration_gt = "duration:[%d TO *]";
	static final String FILTER_TEMPLATE_duration_self_paced= "duration:\"-1\"";
	static final String FILTER_TEMPLATE_between = FIELD_class_start + ":[%s TO %s]";

	static final String FILTER_TEMPLATE_geofilt = "{!geofilt score=distance sfield=course_loc pt=%s d=%s}";
	
	public static final String FILTER_TEMPLATE_ALL = "*:*";

	static final String QUERY_brackets = "(%s)";
	static final String QUERY_AND = "AND";
	static final String QUERY_OR = "||";
	static final String QUERY_DELIMITER = SPACE_REPLACEMENT_CHARACTER;

	private static final String BOOST_STATEMENT = "{!boost b=$boostfunction v=$qq}";
	//here we can use the date in format like 2008-01-01T00:00:00Z, but I hope then will no classes longs more then 1 years
	private static final String DATE_BOOST_FUNCTION = "recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)";
	private static final String GEO_LOCATION_BOOST_FUNCTION = "recip(query($geofq),1,10,5)";

	protected SearchParams params;
	protected Set<String> fieldList = new HashSet<>();
	protected ArrayList<String> filtersList = new ArrayList<>();

	private String collegeId;
	private Integer start;
	private Integer rows;
	private boolean appendFacet = false;

	private String fields = PARAMETER_VALUE_fl;

	private TagGroups tagGroups;

	public SolrQueryBuilder searchParams(SearchParams searchParams) {
		this.params = searchParams;
		return this;
	}

	public SolrQueryBuilder collegeId(String collegeId) {
		this.collegeId = collegeId;
		return this;
	}

	public SolrQueryBuilder start(Integer start) {
		this.start = start;
		return this;
	}

	public SolrQueryBuilder rows(Integer rows) {
		this.rows = rows;
		return this;
	}

	public SolrQueryBuilder appendFacet(boolean appendFacet) {
		this.appendFacet = appendFacet;
		return this;
	}

	public SolrQueryBuilder tagGroups(TagGroups tagGroups) {
		this.tagGroups = tagGroups;
		return this;
	}

	public SolrQuery build() {
		tagGroups = tagGroups == null ? TagGroups.valueOf(params) : tagGroups;

		ArrayList<String> filters = new ArrayList<>();
		appendFilterS(filters);
		appendAnd(filters);

		appendFilterPrice(filters);
		appendAnd(filters);

		appendFilterDay(filters);
		appendAnd(filters);

		appendFilterTime(filters);
		appendAnd(filters);

		appendFilterStartBetween(filters);
		appendAnd(filters);

		appendFilterSiteId(filters);
		appendAnd(filters);

		appendFilterTutorId(filters);
		appendAnd(filters);
		
		appendFilterDuration(filters);
		appendAnd(filters);

		clearLastAnd(filters);

		if (filters.isEmpty())
			appendFilterAll(filters);

		SolrQuery q = createSolrQuery(filters);
		fillCommons(q);

		appendFilterTag(q);

		q.addSort(FIELD_score, ORDER.desc);
		q.addSort(FIELD_startDate, ORDER.asc);
		q.addSort(FIELD_name, ORDER.asc);

		if (appendFacet) {
			appendFacet(q);
		}
		return q;
	}

	public SolrQueryBuilder withFields(String fields) {
		this.fields = fields;
		return this;
	}

	private void appendFilterTutorId(ArrayList<String> filters) {
		if (params.getTutorId() != null) {
			Long tutorId = params.getTutorId();
			filters.add(String.format(FILTER_TEMPLATE_tutorId, tutorId));
		}
	}

	private void appendFilterDuration(ArrayList<String> filters) {
		List<Duration> durations = params.getDurations();
		if (!durations.isEmpty()) {
			String durationsFilter = durations.stream().map(SolrQueryBuilder::getDurationQuery).collect(Collectors.joining(QUERY_DELIMITER + QUERY_OR + QUERY_DELIMITER, "(",")"));
			filters.add(durationsFilter);
		}
	}
	
	public static String getDurationQuery(Duration duration) {
		
		if (duration == null) {
			return null;	
		}

		if (duration.isSelfPaced()) {
			return FILTER_TEMPLATE_duration_self_paced;
		} else {
			switch (duration.getCondition()) {
				case QE:
					return String.format(FILTER_TEMPLATE_duration_eq, duration.getDays());
				case LT:
					return String.format(FILTER_TEMPLATE_duration_lt, duration.getDays());
				case GT:
					return String.format(FILTER_TEMPLATE_duration_gt, duration.getDays());
				default: return null;	
			}
		}
	}

	private void appendFacet(SolrQuery q) {
		q.setFacet(true);
		for (String query : q.getFilterQueries()) {
			q.addFacetQuery(query);
		}
	}

	void appendFilterSiteId(ArrayList<String> filters) {
		if (params.getSiteId() != null) {
			Long siteId = params.getSiteId();
			filters.add(String.format(FILTER_TEMPLATE_siteId, siteId));
		}
	}

	public static SolrQuery createSearchSuburbByLocationQuery(String location) {
		location = location.trim();
		int separator = location.lastIndexOf(" ");
		String[] suburbParams = separator > 0 ? new String[]{location.substring(0, separator), location.substring(separator + 1)}
				: new String[]{location, null};
		if (suburbParams[1] != null && !suburbParams[1].matches(DIGIT_PATTERN)) {
			suburbParams[0] = location;
			suburbParams[1] = null;
		}
		SolrQuery query = new SolrQuery();
		StringBuilder queryString = new StringBuilder();
		queryString.append("(doctype:suburb");
		if (suburbParams[0] != null) {
			String near = suburbParams[0].replaceAll(SPACE_PATTERN, "+");
			queryString.append(" AND (suburb:").append(StringUtils.isBlank(near) ? "*": near);
			queryString.append(" OR postcode:").append(StringUtils.isBlank(near) ? "*": near);
			queryString.append(")");
		}
		if (suburbParams[1] != null) {
			queryString.append(" AND postcode:").append(suburbParams[1]);
		}
		queryString.append(") ");
		query.setQuery(queryString.toString());
		return query;
	}

	
	static void clearLastAnd(List<String> filters) {
		if (filters.size() > 0 && QUERY_AND.equals(filters.get(filters.size() - 1))) {
			filters.remove(filters.size() - 1);
		}

	}

	void appendAnd(List<String> filters) {
		if (filters.size() > 0 && !QUERY_AND.equals(filters.get(filters.size() - 1))) {
			filters.add(QUERY_AND);
		}
	}

	void fillCommons(SolrQuery q) {
		q.setRequestHandler(QUERY_TYPE);
		q.setParam(PARAMETER_fl, fields);
		q.setStart(start);
		q.setRows(rows);
		q.setIncludeScore(true);
		if (params.getDebugQuery() != null && params.getDebugQuery()) {
			q.setShowDebugInfo(params.getDebugQuery());
			q.set(CommonParams.EXPLAIN_STRUCT, String.valueOf(true));
		}


		getHideAge();

		q.addFilterQuery(String.format(FILTER_TEMPLATE_collegeId, collegeId, getHideAge()));
	}

	private String getHideAge() {
		if (params.getClassAge() != null) {
			switch (params.getClassAge().getType()) {
				case afterClassEnds:
					return String.format("NOW-%sDAYS", params.getClassAge().getDays());
				case beforeClassEnds:
					return String.format("NOW+%sDAYS", params.getClassAge().getDays());
				default:
					return "NOW";
			}
		}
		return "NOW";
	}

	void appendFilterAll(List<String> filters) {
		filters.add(FILTER_TEMPLATE_ALL);
	}

	public void appendFilterStartBetween(List<String> filters) {
		if (params.getAfter() != null || params.getBefore() != null) {
			filters.add(String.format(FILTER_TEMPLATE_between,
					params.getAfter() != null ? FormatUtils.convertDateToISO8601(params.getAfter()) : "*",
					params.getBefore() != null ? FormatUtils.convertDateToISO8601(params.getBefore()) : "*"));
		}
	}

	public void appendFilterS(List<String> filters) {
		if (params.getS() != null) {
			String value = replaceSOLRSyntaxisCharacters(params.getS());

			String filter = String.format(FILTER_TEMPLATE_s, value, value, value, value);

			if (hasMoreThanOneWord(value)) {
				String exactValue = String.format("\"%s\"", value);
				filter = String.format(EXTENED_FILTER_TEMPLATE_s, filter, String.format(FILTER_TEMPLATE_s, exactValue, exactValue, exactValue, exactValue));
			}
			filters.add(filter);
		}
	}

	public void appendFilterPrice(List<String> filters) {
		if (params.getPrice() != null) {
			Double price = params.getPrice();
			filters.add(String.format(FILTER_TEMPLATE_price, price));
		}
	}

	public void appendFilterDay(List<String> filters) {
		if (params.getDay() != null) {
			appendFilterWhen(filters, params.getDay().getFullName());
		}
	}

	public void appendFilterTime(List<String> filters) {
		appendFilterWhen(filters, params.getTime());
	}

	public void appendFilterTag(SolrQuery query) {
		for (List<Tag> tagGroup : tagGroups.getTagGroups()) {
			query.addFilterQuery(getQueryByTagGroup(tagGroup));
		}
	}

	private String getQueryByTagGroup(List<Tag> tagGroup) {
		ArrayList<String> queries = new ArrayList<>();
		for (Tag tag : tagGroup) {
			queries.addAll(getTagQueries(tag));
		}
		return String.format(QUERY_brackets, StringUtils.join(queries.toArray(), QUERY_DELIMITER + QUERY_OR + QUERY_DELIMITER));
	}

	public static void appendFilterWhen(List<String> filters, String whenValue) {
		if (whenValue != null) {
			filters.add(String.format(FILTER_TEMPLATE_when, whenValue));
		}
	}


	private SolrQuery createGeoLocationQuery(List<String> filters) {
		List<Suburb> suburbs = params.getSuburbs();

		ArrayList<String> intersects = new ArrayList<>();
		for (Suburb suburb : suburbs) {
			intersects.add(getSuburbQuery(suburb));
		}

		final String geoFilterQuery ="{!score=distance}" + StringUtils.join(intersects, " ");
		SolrQuery query = new SolrQuery();
		query.addFilterQuery(geoFilterQuery);
		query.setQuery(BOOST_STATEMENT);
		query.setParam(PARAMETER_BOOST_FUNCTION, GEO_LOCATION_BOOST_FUNCTION);
		query.setParam(PARAMETER_geofq, geoFilterQuery);
		query.setParam(PARAMETER_qq, String.format(QUERY_brackets, convert(filters)));
		return query;
	}

	private SolrQuery createSolrQuery(List<String> filters) {
		return params.getSuburbs().isEmpty() ?
				new BuildBoostQuery()
						.hideAge(params.getClassAge())
						.mainQuery(String.format(QUERY_brackets, convert(filters)))
						.build() : createGeoLocationQuery(filters);
	}


	public static  String convert(List<String> filters) {
		return StringUtils.join(filters.toArray(), QUERY_DELIMITER);
	}


	public static String getSuburbQuery(Suburb suburb) {
		return String.format(FILTER_TEMPLATE_geofilt, suburb.getCoordinates(), (suburb.getDistance() / KM_IN_DEGREE_VALUE));
	}

	public static List<String> getTagQueries(Tag tag) {
		ArrayList<String> tags = new ArrayList<>();
		tags.add(String.format(FILTER_TEMPLATE_tagId, tag.getId()));
		for (Tag subTag : tag.getAllWebVisibleChildren()) {
			tags.add(String.format(FILTER_TEMPLATE_tagId, subTag.getId()));
		}
		return tags;
	}

	public static String getTagQuery(Tag tag) {
		ArrayList<String> tags = new ArrayList<>();
		tags.add(String.format(FILTER_TEMPLATE_tagId, tag.getId()));
		for (Tag subTag : tag.getAllWebVisibleChildren()) {
			tags.add(SolrQueryBuilder.QUERY_OR);
			tags.add(String.format(FILTER_TEMPLATE_tagId, subTag.getId()));
		}
		return tags.isEmpty() ? null : StringUtils.join(tags.toArray(), QUERY_DELIMITER);
	}

	/**
	 * @param params    cannot be null
	 * @param collegeId cannot be null
	 * @param start     can be null
	 * @param rows      can be null
	 */
	public static SolrQueryBuilder valueOf(
			SearchParams params,
			TagGroups tagGroups,
			String collegeId,
			Integer start,
			Integer rows) {
		SolrQueryBuilder builder = new SolrQueryBuilder();
		builder.params = params;
		builder.collegeId = collegeId;
		builder.start = start;
		builder.rows = rows;
		builder.tagGroups = tagGroups;
		return builder;
	}


	public static SolrQueryBuilder valueOf(
			SearchParams params,
			String collegeId, Integer start,
			Integer rows) {
		return new SolrQueryBuilder().searchParams(params)
				.tagGroups(TagGroups.valueOf(params))
				.collegeId(collegeId)
				.start(start).rows(rows);
	}


	public static String replaceSOLRSyntaxisCharacters(String original) {
		return original.replaceAll(SOLR_SYNTAX_CHARACTERS_STRING, SPACE_REPLACEMENT_CHARACTER);
	}

	private static boolean hasMoreThanOneWord(String text) {
		StringTokenizer st = new StringTokenizer(text);
		return st.countTokens() > 1;
	}

	/**
	 * The helper method appends filter not only for the tag also for its childrent.
	 */
	public static void appendFilterTag(SolrQuery query, Tag tag) {
		String sQuery = getTagQuery(tag);
		if (sQuery != null) {
			query.addFilterQuery(String.format(QUERY_brackets, sQuery));
		}
	}
}
