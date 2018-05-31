package ish.oncourse.solr.query;

import ish.oncourse.model.Tag;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.common.params.CommonParams;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

    static final String PARAMETER_VALUE_fl = "*";
    static final String PARAMETER_VALUE_sfield = "course_loc";


    static final String FILTER_TEMPLATE_collegeId = "+collegeId:%s +doctype:course end:[NOW TO *]";
    public static final String FILTER_TEMPLATE_s = "(detail:(%s)^1 || tutor:(%s)^5 || course_code:(%s)^30 || name:(%s)^20)";
    public static final String EXTENED_FILTER_TEMPLATE_s = "%s || (%s)^100";
    public static final String FILTER_TEMPLATE_price = "price:[* TO %s]";
    public static final String FILTER_TEMPLATE_when = "when:%s";
    static final String FILTER_TEMPLATE_tagId = "tagId:%d";
    static final String FILTER_TEMPLATE_siteId = "siteId:%d";
    static final String FILTER_TEMPLATE_tutorId = "tutorId:%d";
    static final String FILTER_TEMPLATE_between = FIELD_class_start + ":[%s TO %s]";

    static final String FILTER_TEMPLATE_geofilt = "{!score=distance}%s";

    static final String FILTER_TEMPLATE_course_loc = "{!geofilt sfield=%s pt=%s d=%s}";


    public static final String FILTER_TEMPLATE_ALL = "*:*";

    static final String QUERY_brackets = "(%s)";
    static final String QUERY_AND = "AND";
    static final String QUERY_OR = "||";
    static final String QUERY_DELIMITER = SPACE_REPLACEMENT_CHARACTER;

    private static final String BOOST_STATEMENT = "{!boost b=$boostfunction v=$qq}";
    //here we can use the date in format like 2008-01-01T00:00:00Z, but I hope then will no classes longs more then 1 years
    private static final String DATE_BOOST_FUNCTION = "recip(max(ms(startDate,NOW-1YEAR/DAY),0),1.15e-8,500,500)";
    private static final String GEO_LOCATION_BOOST_FUNCTION = "recip(query($geofq),1,10,5)";

    private SearchParams params;
    private String collegeId;
    private Integer start;
    private Integer rows;
    private boolean appendFacet = false;

    private TagGroups tagGroups;

    public SolrQuery build() {
        SolrQuery q = new SolrQuery();

        fillCommons(q);

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

        appendFilterTag(q);

        clearLastAnd(filters);

        if (filters.isEmpty())
            appendFilterAll(filters);

        setFiltersTo(q, filters);
        q.addSort(FIELD_score, ORDER.desc);
        q.addSort(FIELD_startDate, ORDER.asc);
        q.addSort(FIELD_name, ORDER.asc);

        if (appendFacet) {
            appendFacet(q);
        }
        return q;
    }

    private void appendFilterTutorId(ArrayList<String> filters) {
        if (params.getTutorId() != null) {
            Long tutorId = params.getTutorId();
            filters.add(String.format(FILTER_TEMPLATE_tutorId, tutorId));
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
            queryString.append(" AND (suburb:").append(near);
            queryString.append(" || postcode:").append(near);
            queryString.append(")");
        }
        if (suburbParams[1] != null) {
            queryString.append(" AND postcode:").append(suburbParams[1]);
        }
        queryString.append(") ");
        query.setQuery(queryString.toString());
        return query;
    }

    void clearLastAnd(List<String> filters) {
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
        q.setParam(PARAMETER_fl, PARAMETER_VALUE_fl);
        q.setStart(start);
        q.setRows(rows);
        q.setIncludeScore(true);
        if (params.getDebugQuery() != null && params.getDebugQuery()) {
            q.setShowDebugInfo(params.getDebugQuery());
            q.set(CommonParams.EXPLAIN_STRUCT, String.valueOf(true));
        }
        q.addFilterQuery(String.format(FILTER_TEMPLATE_collegeId, collegeId));
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

    private void appendFilterWhen(List<String> filters, String whenValue) {
        if (whenValue != null) {
            filters.add(String.format(FILTER_TEMPLATE_when, whenValue));
        }
    }


    void setFiltersAndNearTo(SolrQuery query, List<String> filters) {
        List<Suburb> suburbs = params.getSuburbs();

        ArrayList<String> intersects = new ArrayList<>();
        for (Suburb suburb : suburbs) {
            intersects.add(getSuburbQuery(suburb));
        }
        final String geoFilterQuery = String.format(FILTER_TEMPLATE_geofilt, StringUtils.join(intersects, " "));
        query.addFilterQuery(geoFilterQuery);
        query.setQuery(BOOST_STATEMENT);
        query.setParam(PARAMETER_BOOST_FUNCTION, GEO_LOCATION_BOOST_FUNCTION);
        query.setParam(PARAMETER_geofq, geoFilterQuery);
        query.setParam(PARAMETER_qq, String.format(QUERY_brackets, convert(filters)));
    }

    void setFiltersTo(SolrQuery query, List<String> filters) {
        if (!params.getSuburbs().isEmpty()) {
            setFiltersAndNearTo(query, filters);
        } else {
            query.setQuery(BOOST_STATEMENT);
            query.setParam(PARAMETER_BOOST_FUNCTION, DATE_BOOST_FUNCTION);
            query.setParam(PARAMETER_qq, String.format(QUERY_brackets, convert(filters)));
        }
    }


    String convert(List<String> filters) {
        return StringUtils.join(filters.toArray(), QUERY_DELIMITER);
    }


    public static String getSuburbQuery(Suburb suburb) {
        return String.format(FILTER_TEMPLATE_course_loc, PARAMETER_VALUE_sfield, suburb.getCoordinates(), (suburb.getDistance() / KM_IN_DEGREE_VALUE));
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
            TagGroups tagGroups,
            String collegeId) {
        return valueOf(params, tagGroups, collegeId, 0, 0);
    }

    public static SolrQueryBuilder valueOf(
            SearchParams params,
            String collegeId) {
        return valueOf(params, TagGroups.valueOf(params), collegeId, 0, 0);
    }

    public static SolrQueryBuilder valueOf(
            SearchParams params,
            String collegeId,Integer start,
            Integer rows) {
        return valueOf(params, TagGroups.valueOf(params), collegeId, start, rows);
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
