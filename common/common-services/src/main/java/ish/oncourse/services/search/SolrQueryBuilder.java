package ish.oncourse.services.search;

import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Tag;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public static final String FIELD_startDate = "startDate";
    public static final String FIELD_suburb = "suburb";
    public static final String FIELD_postcode = "postcode";

    static final String PARAMETER_VALUE_fl = "id,name,course_loc";
    static final String PARAMETER_VALUE_sfield = "course_loc";


    static final String FILTER_TEMPLATE_collegeId = "+collegeId:%s +doctype:course end:[NOW TO *]";
    static final String FILTER_TEMPLATE_s = "(detail:%s || tutor:%s || course_code:%s || name:%s)";
    static final String FILTER_TEMPLATE_price = "price:[* TO %s]";
    static final String FILTER_TEMPLATE_when = "when:%s";
    static final String FILTER_TEMPLATE_tagId = "tagId:%d";
    static final String FILTER_TEMPLATE_after = "startDate:[%s TO *]";
    static final String FILTER_TEMPLATE_before = "end:[NOW TO %s]";

    static final String FILTER_TEMPLATE_geofilt = "{!geofilt}";

    static final String FILTER_TEMPLATE_ALL = "*:*";

    static final String QUERY_brackets = "(%s)";
    static final String QUERY_AND = "AND";
    static final String QUERY_OR = "||";
    static final String QUERY_DELIMITER = " ";

    static final String QUERY_SORT_FIELD_geodist = "geodist()";

    private static final String DATE_BOOST_STM = "{!boost b=$dateboost v=$qq}";
    private static final String DATE_BOOST_FUNCTION = "recip(max(ms(startDate, NOW), 0),1.15e-8,1,1)";



    private ISearchService searchService;



    private Map<SearchParam, Object> params;
    private String collegeId;
    private final int start;
    private final int rows;

    public SolrQueryBuilder(ISearchService searchService,
                            Map<SearchParam, Object> params,
                            String collegeId,
                            int start,
                            int rows) {
        this.searchService = searchService;
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
        if (params.containsKey(SearchParam.after)) {
            String value = (String) params.get(SearchParam.after);
            filters.add(String.format(FILTER_TEMPLATE_after, value));
        }
    }

    void appendFilterBefore(List<String> filters) {
        if (params.containsKey(SearchParam.before)) {
            String value = (String) params.get(SearchParam.before);
            filters.add(String.format(FILTER_TEMPLATE_before, value));
        }
    }

    void appendFilterS(List<String> filters) {
        if (params.containsKey(SearchParam.s)) {
            String value = ClientUtils.escapeQueryChars((String) params.get(SearchParam.s));
            filters.add(String.format(FILTER_TEMPLATE_s, value, value, value, value));
        }
    }

    void appendFilterPrice(List<String> filters) {
        if (params.containsKey(SearchParam.price)) {
            String price = (String) params.get(SearchParam.price);
            if (price != null && price.length() > 0 && !StringUtils.isNumeric(price)) {
                // remove the possible currency sign
                price = price.replaceAll("[$]", "");
            }
            filters.add(String.format(FILTER_TEMPLATE_price, price));
        }
    }

    void appendFilterDay(List<String> filters) {
         appendFilterWhen(filters, SearchParam.day);
    }

    void appendFilterTime(List<String> filters) {
         appendFilterWhen(filters, SearchParam.time);
    }

    void appendFilterSubject(List<String> filters) {
        if (params.containsKey(SearchParam.subject)) {
            Object tagParameter = params.get(SearchParam.subject);
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

    private void appendFilterWhen(List<String> filters, SearchParam param) {
        if (params.containsKey(param)) {
            String value = (String) params.get(param);
            filters.add(String.format(FILTER_TEMPLATE_when, value));
        }
    }


    void setFiltersAndNearTo(SolrQuery query,List<String> filters)
    {
        SolrDocumentList solrSuburbs = (SolrDocumentList) params.get(SearchParam.near);
        String location = (String) solrSuburbs.get(0).get(PARAMETER_loc);
        query.addFilterQuery(FILTER_TEMPLATE_geofilt);
        query.add(PARAMETER_sfield, PARAMETER_VALUE_sfield);
        query.add(PARAMETER_pt, location);
        query.add(PARAMETER_d, getDistance());
        query.addSortField(QUERY_SORT_FIELD_geodist, SolrQuery.ORDER.asc);
        query.addSortField(FIELD_name, SolrQuery.ORDER.asc);
        query.setQuery(String.format(QUERY_brackets,convert(filters)));
    }

    void setFiltersTo(SolrQuery query,List<String> filters)
    {
        if (params.containsKey(SearchParam.near))
        {
            setFiltersAndNearTo(query,filters);
        }
        else
        {
            query.setQuery(DATE_BOOST_STM);
            query.setParam(PARAMETER_dateboost, DATE_BOOST_FUNCTION);
            query.setParam(PARAMETER_qq, String.format(QUERY_brackets,convert(filters)));
            query.addSortField(FIELD_score, SolrQuery.ORDER.desc);
            query.addSortField(FIELD_name, SolrQuery.ORDER.asc);
        }
    }


    String convert(List<String> filters)
    {
        return StringUtils.join(filters.toArray(), QUERY_DELIMITER);
    }


    String getDistance()
    {
        String distance = String.valueOf(SearchService.MAX_DISTANCE);
        if (params.containsKey(SearchParam.km)) {
            Double km = (Double) params.get(SearchParam.km);
            return km.toString();
        }
        return  distance;
    }

}
