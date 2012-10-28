package ish.oncourse.services.search;

import ish.oncourse.model.Tag;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.List;

public class SolrQueryBuilder {

    static final String QUERY_TYPE_standard = "standard";

    static final String PARAMETER_fl = "fl";
    static final String PARAMETER_sfield = "sfield";
    static final String PARAMETER_pt = "pt";
    static final String PARAMETER_d = "d";
    public static final String PARAMETER_loc = "loc";
    static final String PARAMETER_dateboost = "dateboost";
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
    static final String FILTER_TEMPLATE_s = "(detail:(%s) || tutor:(%s) || course_code:(%s) || name:(%s))";
    static final String FILTER_TEMPLATE_price = "price:[* TO %s]";
    static final String FILTER_TEMPLATE_when = "when:%s";
    static final String FILTER_TEMPLATE_tagId = "tagId:%d";
    static final String FILTER_TEMPLATE_after = FIELD_class_start  + ":[%s TO *]";
    static final String FILTER_TEMPLATE_before = FIELD_end + ":[NOW TO %s]";

    static final String FILTER_TEMPLATE_geofilt = "{!geofilt}";

    static final String FILTER_TEMPLATE_ALL = "*:*";

    static final String QUERY_brackets = "(%s)";
    static final String QUERY_AND = "AND";
    static final String QUERY_OR = "||";
    static final String QUERY_DELIMITER = " ";

    static final String QUERY_SORT_FIELD_geodist = "geodist()";

    private static final String DATE_BOOST_STM = "{!boost b=$dateboost v=$qq}";
    private static final String DATE_BOOST_FUNCTION = "recip(max(ms(startDate, NOW), 0),1.15e-8,1,1)";




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

        ArrayList<String> filters = new ArrayList<String>();
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

        appendFilterSubject(filters);

        clearLastAnd(filters);

        if (filters.isEmpty())
            appendFilterAll(filters);

        setFiltersTo(q,filters);

        return q;
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
        q.setQueryType(QUERY_TYPE_standard);
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

    void appendFilterSubject(List<String> filters) {
        if (params.getSubject() != null) {
            Object tagParameter = params.getSubject();
            if (tagParameter instanceof Tag) {
                Tag browseTag = (Tag) tagParameter;
                ArrayList<String> tags = new ArrayList<String>();
                tags.add(String.format(FILTER_TEMPLATE_tagId, browseTag.getId()));
                for (Tag t : browseTag.getAllWebVisibleChildren()) {
                    tags.add(QUERY_OR);
                    tags.add(String.format(FILTER_TEMPLATE_tagId, t.getId()));
                }
                filters.add(String.format(QUERY_brackets, StringUtils.join(tags.toArray(), QUERY_DELIMITER)));
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
        query.addFilterQuery(FILTER_TEMPLATE_geofilt);
        query.add(PARAMETER_sfield, PARAMETER_VALUE_sfield);
        query.add(PARAMETER_pt, suburb.getLocation());
        query.add(PARAMETER_d, suburb.getDistance().toString());
        query.addSortField(QUERY_SORT_FIELD_geodist, SolrQuery.ORDER.asc);
        query.addSortField(FIELD_name, SolrQuery.ORDER.asc);
        query.setQuery(String.format(QUERY_brackets,convert(filters)));
    }

    void setFiltersTo(SolrQuery query,List<String> filters)
    {
        if (!params.getSuburbs().isEmpty())
        {
            setFiltersAndNearTo(query,filters);
        }
        else
        {
            query.setQuery(DATE_BOOST_STM);
            query.setParam(PARAMETER_dateboost, DATE_BOOST_FUNCTION);
            query.setParam(PARAMETER_qq, String.format(QUERY_brackets,convert(filters)));
        }
    }


    String convert(List<String> filters)
    {
        return StringUtils.join(filters.toArray(), QUERY_DELIMITER);
    }
    
    static String replaceSOLRSyntaxisCharacters(String original) {
    	String resultString = original.replaceAll("[\\!\\^\\(\\)\\{\\}\\[\\]\\:\"\\?\\+\\~\\*\\|\\&\\;\\\\]", StringUtils.EMPTY);
    	return resultString;
    }
}
