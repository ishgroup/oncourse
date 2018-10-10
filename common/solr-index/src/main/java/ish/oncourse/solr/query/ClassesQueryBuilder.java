/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.query;

import ish.oncourse.util.FormatUtils;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;


import static ish.oncourse.solr.query.SolrQueryBuilder.*;

public class ClassesQueryBuilder {


    private static final String QUERY_TYPE_SELECT = "select";
    private static final String FILTER_TEMPLATE_start_between = "start:[%s TO %s]";
    private static final String FILTER_TEMPLATE_courses = "courseId:(%s)";
    private static final String FILTER_TEMPLATE_site = "siteKey:%s";


    private ClassesQueryParams params;

    public static ClassesQueryBuilder valueOf(ClassesQueryParams params) {
        ClassesQueryBuilder builder = new ClassesQueryBuilder();
        builder.params = params;
        return builder;
    }

    public SolrQuery build() {
        ArrayList<String> filters = new ArrayList<>();
        
        filters.add(String.format(FILTER_TEMPLATE_courses, params.getCoursesString()));
        filters.add(QUERY_AND);
        filters.add(String.format(FILTER_TEMPLATE_site, params.getSiteKey()));
        filters.add(QUERY_AND);
        

        if (params.getPrice() != null) {
            filters.add(String.format(FILTER_TEMPLATE_price, params.getPrice()));
            filters.add(QUERY_AND);
        }

        if (params.getDay() != null) {
            appendFilterWhen(filters, params.getDay().getFullName());
            filters.add(QUERY_AND);
        }
        
        appendFilterWhen(filters, params.getTime());
        
        if (params.getAfter() != null || params.getBefore() != null) {
            filters.add(String.format(FILTER_TEMPLATE_start_between,
                    params.getAfter() != null ? FormatUtils.convertDateToISO8601(params.getAfter()) : "*",
                    params.getBefore() != null ? FormatUtils.convertDateToISO8601(params.getBefore()) : "*"));
            filters.add(QUERY_AND);
        }
        
        if (params.getSiteId() != null) {
            filters.add(String.format(FILTER_TEMPLATE_siteId, params.getSiteId()));
            filters.add(QUERY_AND);
        }
        if (params.getTutorId() != null) {
            filters.add(String.format(FILTER_TEMPLATE_tutorId, params.getTutorId()));
            filters.add(QUERY_AND);
        }
        clearLastAnd(filters);
        
        SolrQuery q =  new BuildBoostQuery()
                .hideAge(params.getClassAge())
                .mainQuery(String.format(QUERY_brackets, convert(filters))).build();

        q.setRequestHandler(QUERY_TYPE_SELECT);
        q.setStart(0);
        q.setRows(Integer.MAX_VALUE);
        return q;
    }


}
