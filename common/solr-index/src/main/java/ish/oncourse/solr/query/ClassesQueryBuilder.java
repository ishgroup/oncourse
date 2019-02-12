/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.query;

import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.Set;

public class ClassesQueryBuilder extends SolrQueryBuilder {

	private static final String QUERY_TYPE_SELECT = "edismax";
	private static final String FILTER_TEMPLATE_start_between = "start:[%s TO %s]";
	private static final String FILTER_TEMPLATE_courses = "courseId:(%s)";
	private static final String FILTER_TEMPLATE_site = "siteKey:%s";

	private Set<String> coursesIds;

	private SolrQuery query;

	public static SolrQueryBuilder valueOf(SearchParams params, Set<String> coursesIds) {
		ClassesQueryBuilder builder = new ClassesQueryBuilder();
		builder.searchParams(params);
		builder.coursesIds = coursesIds;
		return builder;
	}

	public SolrQuery build() {

		this.query = new SolrQuery();

		ArrayList<String> filtersList = new ArrayList<>();

		filtersList.add(String.format(FILTER_TEMPLATE_courses, String.join(" ", coursesIds)));
		filtersList.add(QUERY_AND);

		/*if (searchParams.getPrice() != null) {
			filtersList.add(String.format(FILTER_TEMPLATE_price, searchParams.getPrice()));
			filtersList.add(QUERY_AND);
		}

		if (searchParams.getDay() != null) {
			appendFilterWhen(filtersList, searchParams.getDay().getFullName());
			filtersList.add(QUERY_AND);
		}

		appendFilterWhen(filtersList, searchParams.getTime());

		if (searchParams.getAfter() != null || searchParams.getBefore() != null) {
			filtersList.add(String.format(FILTER_TEMPLATE_start_between,
					searchParams.getAfter() != null ? FormatUtils.convertDateToISO8601(searchParams.getAfter()) : "*",
					searchParams.getBefore() != null ? FormatUtils.convertDateToISO8601(searchParams.getBefore()) : "*"));
			filtersList.add(QUERY_AND);
		}

		if (searchParams.getSiteId() != null) {
			filtersList.add(String.format(FILTER_TEMPLATE_siteId, searchParams.getSiteId()));
			filtersList.add(QUERY_AND);
		}
		if (searchParams.getTutorId() != null) {
			filtersList.add(String.format(FILTER_TEMPLATE_tutorId, searchParams.getTutorId()));
			filtersList.add(QUERY_AND);
		}

		// %20dist:{!func}geodist()&sfield=classLoc&pt=-33.896449,151.180013
		*/

		clearLastAnd(filtersList);

		query.setRequestHandler(QUERY_TYPE_SELECT);
		query.setFields("* score [explain]");
		query.setFilterQueries(convert(filtersList));

		setUpDistanceField();
		setUpPage(0, Integer.MAX_VALUE);

		return query;
	}

	private void setUpDistanceField() {
		Suburb suburb = params.getSuburbs().get(0);
		query.setFields("dist:geodist()");
		query.set("sfield", "classLoc");
		query.set("pt", suburb.getCoordinates());
	}

	/**
	 * Grouping by courseId and score
	 */
	private void setUpFacets() {

	}

	private void setUpPage(int start, int rows) {
		query.setStart(start);
		query.setRows(rows);
	}
}
