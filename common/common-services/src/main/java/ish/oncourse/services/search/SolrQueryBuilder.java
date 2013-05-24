package ish.oncourse.services.search;

import ish.oncourse.model.Tag;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

import java.util.ArrayList;
import java.util.List;

public class SolrQueryBuilder {
	static final String DIGIT_PATTERN = "(\\d)+";
	static final String SPACE_PATTERN = "[\\s]+";
	
	static final double KM_IN_DEGREE_VALUE = 110.567;
	private static final String SPACE_REPLACEMENT_CHARACTER = " ";
	private static final String SOLR_SYNTAX_CHARACTERS_STRING = "[\\!\\^\\(\\)\\{\\}\\[\\]\\:\"\\?\\+\\~\\*\\|\\&\\;\\\\]";

	static final String QUERY_TYPE = "standard";

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


    static final String FILTER_TEMPLATE_collegeId = "+collegeId:%s +doctype:course end:[NOW TO *]";
    static final String FILTER_TEMPLATE_s = "(detail:(%s)^1 || tutor:(%s)^5 || course_code:(%s)^30 || name:(%s)^20)";
    static final String FILTER_TEMPLATE_price = "price:[* TO %s]";
    static final String FILTER_TEMPLATE_when = "when:%s";
    static final String FILTER_TEMPLATE_tagId = "tagId:%d";
    static final String FILTER_TEMPLATE_after = FIELD_class_start  + ":[%s TO *]";
    static final String FILTER_TEMPLATE_before = FIELD_end + ":[NOW TO %s]";

    static final String FILTER_TEMPLATE_geofilt = "{!score=distance}%s:\"Intersects(Circle(%s %s=%s))\"";

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
    private final int start;
    private final int rows;

    public SolrQueryBuilder(
                            SearchParams params,
                            String collegeId,
                            int start,
                            int rows) {
        this.params = params;
        this.collegeId = collegeId;
        this.start = start;
        this.rows = rows;
    }


    public SolrQuery create() {
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

        appendFilterAfter(filters);
        appendAnd(filters);

        appendFilterBefore(filters);
        appendAnd(filters);

        appendFilterSubject(q);

        clearLastAnd(filters);

        if (filters.isEmpty())
            appendFilterAll(filters);

        setFiltersTo(q,filters);
        q.addSort(FIELD_score, ORDER.desc);
        q.addSort(FIELD_startDate, ORDER.asc);
        q.addSort(FIELD_name, ORDER.asc);
        q.setShowDebugInfo(params.getDebugQuery());
        return q;
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

    void clearLastAnd(List<String> filters)
    {
        if (filters.size() > 0 && filters.get(filters.size() - 1) == QUERY_AND)
        {
            filters.remove(filters.size() - 1);
        }

    }

    void appendAnd(List<String> filters)
    {
        if (filters.size() > 0 && filters.get(filters.size() - 1) != QUERY_AND)
        {
            filters.add(QUERY_AND);
        }
    }

    void fillCommons(SolrQuery q) {
        q.setRequestHandler(QUERY_TYPE);
        q.setParam(PARAMETER_fl, PARAMETER_VALUE_fl);
        q.setStart(start);
        q.setRows(rows);
        q.setIncludeScore(true);
        q.addFilterQuery(String.format(FILTER_TEMPLATE_collegeId, collegeId));
    }

    void appendFilterAll(List<String> filters)
    {
        filters.add(FILTER_TEMPLATE_ALL);
    }

    void appendFilterAfter(List<String> filters) {
        if (params.getAfter() != null) {
            filters.add(String.format(FILTER_TEMPLATE_after, FormatUtils.convertDateToISO8601(params.getAfter())));
        }
    }

    void appendFilterBefore(List<String> filters) {
        if (params.getBefore() != null) {
            filters.add(String.format(FILTER_TEMPLATE_before, FormatUtils.convertDateToISO8601(params.getBefore())));
        }
    }

    void appendFilterS(List<String> filters) {
        if (params.getS() != null) {
            String value = replaceSOLRSyntaxisCharacters(params.getS());
            filters.add(String.format(FILTER_TEMPLATE_s, value, value, value, value));
        }
    }

    void appendFilterPrice(List<String> filters) {
        if (params.getPrice() != null) {
            Double price = params.getPrice();
            filters.add(String.format(FILTER_TEMPLATE_price, price));
        }
    }

    void appendFilterDay(List<String> filters) {
         appendFilterWhen(filters, params.getDay());
    }

    void appendFilterTime(List<String> filters) {
         appendFilterWhen(filters, params.getTime());
    }

    void appendFilterSubject(SolrQuery query) {
        if (params.getSubject() != null) {
            Object tagParameter = params.getSubject();
            if (tagParameter instanceof Tag) {
                Tag browseTag = (Tag) tagParameter;
                ArrayList<String> tags = new ArrayList<>();
                tags.add(String.format(FILTER_TEMPLATE_tagId, browseTag.getId()));
                for (Tag t : browseTag.getAllWebVisibleChildren()) {
                    tags.add(QUERY_OR);
                    tags.add(String.format(FILTER_TEMPLATE_tagId, t.getId()));
                }
                query.addFilterQuery(String.format(QUERY_brackets, StringUtils.join(tags.toArray(), QUERY_DELIMITER)));
            } else {
                final String message = String.format("Illegal parameter detected with value = %s with type = %s  for college = %s",
                        tagParameter, tagParameter != null ? tagParameter.getClass() : "undefined", collegeId);
                throw new IllegalArgumentException(message);
            }
        }
    }

    private void appendFilterWhen(List<String> filters, String whenValue) {
        if (whenValue != null) {
            filters.add(String.format(FILTER_TEMPLATE_when, whenValue));
        }
    }


    void setFiltersAndNearTo(SolrQuery query,List<String> filters)
    {
        List<Suburb> suburbs = params.getSuburbs();
        Suburb suburb = suburbs.get(0);
        final String geoFilterQuery = String.format(FILTER_TEMPLATE_geofilt, PARAMETER_VALUE_sfield, suburb.getLocation(), PARAMETER_d, suburb.getDistance()/KM_IN_DEGREE_VALUE);
        query.addFilterQuery(geoFilterQuery);
        query.setQuery(BOOST_STATEMENT);
        query.setParam(PARAMETER_BOOST_FUNCTION, GEO_LOCATION_BOOST_FUNCTION);
        query.setParam(PARAMETER_geofq, geoFilterQuery);
        query.setParam(PARAMETER_qq, String.format(QUERY_brackets,convert(filters)));
    }

    void setFiltersTo(SolrQuery query,List<String> filters)
    {
        if (!params.getSuburbs().isEmpty())
        {
            setFiltersAndNearTo(query,filters);
        }
        else
        {
            query.setQuery(BOOST_STATEMENT);
            query.setParam(PARAMETER_BOOST_FUNCTION, DATE_BOOST_FUNCTION);
            query.setParam(PARAMETER_qq, String.format(QUERY_brackets,convert(filters)));
        }
    }


    String convert(List<String> filters) {
        return StringUtils.join(filters.toArray(), QUERY_DELIMITER);
    }
    
	public static String replaceSOLRSyntaxisCharacters(String original) {
    	String resultString = original.replaceAll(SOLR_SYNTAX_CHARACTERS_STRING, SPACE_REPLACEMENT_CHARACTER);
    	return resultString;
    }
}
