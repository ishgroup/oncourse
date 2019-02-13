/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.query;

import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.Arrays;
import java.util.Set;

public class ClassesQueryBuilder extends SolrQueryBuilder {

	private static final String DEFAULT_QUERY_TYPE         = "edismax";
	private static final String DEFAULT_FILED_LIST         = "*";
	private static final String DEFAULT_CRITERIA_DELIMITER = " AND ";

	private static final String FILTER_TEMPLATE_COURSES    = "courseId:(%s)";

	private static final String CRITERION_TEMPLATE_CLASSES_GEOFILT    = "({!geofilt pt=%s sfield=classLoc d=%s})";
	private static final String CRITERION_TEMPLATE_CLASSES_PRICE      = "(price:[0 TO %s])";
	private static final String CRITERION_TEMPLATE_CLASSES_WHEN       = "(when:%s)";
	private static final String CRITERION_TEMPLATE_CLASSES_SITE_ID    = "(siteId:%s)";
	private static final String CRITERION_TEMPLATE_CLASSES_TUTOR_ID   = "(tutorId:%s)";
	private static final String CRITERION_TEMPLATE_CLASSES_START_DATE = "(start:[%s TO %s])";
	private static final String CRITERION_TEMPLATE_CLASSES_COURSES    = "(courseId:(%s))^=0";

	private static final String DISTANCE_FIELD_NAME = "dist";

	protected Set<String> coursesIds;
	protected SolrQuery query;

	private boolean enabledGrouping;
	private boolean enabledReturnDistance;

	public static ClassesQueryBuilder valueOf(SearchParams params, Set<String> coursesIds) {
		ClassesQueryBuilder builder = new ClassesQueryBuilder();
		builder.searchParams(params);
		builder.coursesIds = coursesIds;
		return builder;
	}

	public SolrQuery build() {
		this.query = new SolrQuery();

		query.setRequestHandler(DEFAULT_QUERY_TYPE);

		setUpDistanceField();
		setUpGroupByCourseId();

		setUpSearchParams();
		setUpFilterQueryList();
		setUpFieldList();
		setUpPage(0, Integer.MAX_VALUE);

		return query;
	}

	public ClassesQueryBuilder enableGrouping() {
		enabledGrouping = true;
		return this;
	}

	public ClassesQueryBuilder enableDistanceField() {
		enabledReturnDistance = true;
		return this;
	}

	private void setUpSearchParams() {

		StringBuilder searchCriteria = new StringBuilder();

		if (params != null) {

			if (params.getPrice() != null) {
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_PRICE, params.getPrice()));
			}

			if (params.getDay() != null) {
				searchCriteria.append(DEFAULT_CRITERIA_DELIMITER);
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_WHEN, params.getDay().getFullName()));
			}

			if (params.getTime() != null) {
				searchCriteria.append(DEFAULT_CRITERIA_DELIMITER);
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_WHEN, params.getTime()));
			}

			if (params.hasSuburbs()) {
				searchCriteria.append(DEFAULT_CRITERIA_DELIMITER);
				Suburb suburb = params.getSuburbs().get(0);
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_GEOFILT, suburb.getCoordinates(), suburb.getDistance()));
			}

			if (params.getSiteId() != null) {
				searchCriteria.append(DEFAULT_CRITERIA_DELIMITER);
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_SITE_ID, params.getSiteId()));
			}

			if (params.getTutorId() != null) {
				searchCriteria.append(DEFAULT_CRITERIA_DELIMITER);
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_TUTOR_ID, params.getTutorId()));
			}

			if (params.getAfter() != null || params.getBefore() != null) {
				searchCriteria.append(DEFAULT_CRITERIA_DELIMITER);
				String dateAfter = params.getAfter() == null ? "*" : FormatUtils.convertDateToISO8601(params.getAfter());
				String dateBefore = params.getBefore() == null ? "*" : FormatUtils.convertDateToISO8601(params.getBefore());
				searchCriteria.append(String.format(CRITERION_TEMPLATE_CLASSES_START_DATE, dateAfter, dateBefore));
			}
		}

		String commonCriterion = String.format(CRITERION_TEMPLATE_CLASSES_COURSES, StringUtils.join(coursesIds, " "));
		query.setQuery(commonCriterion + String.format(" (%s)^=1.0", searchCriteria.toString()));
	}

	private void addDefaultFilters() {
		filtersList.add(String.format(FILTER_TEMPLATE_COURSES, String.join(" ", coursesIds)));
	}

	private void setUpFilterQueryList() {
		if (filtersList == null || filtersList.isEmpty()) {
			addDefaultFilters();
		}
		query.setFilterQueries(StringUtils.join(filtersList, " "));
	}

	public ClassesQueryBuilder addFieldList(String fields) {
		this.fieldList.addAll(Arrays.asList(fields.split(" ")));
		return this;
	}

	public ClassesQueryBuilder addFieldList(Set<String> fields) {
		this.fieldList.addAll(fields);
		return this;
	}

	public ClassesQueryBuilder addField(String field){
		this.fieldList.add(field);
		return this;
	}

	private void setUpFieldList() {
		if(fieldList.isEmpty()) {
			fieldList.add(DEFAULT_FILED_LIST);
		}
		query.setFields(String.join(" ", fieldList));
	}

	private void setUpDistanceField() {
		if (enabledReturnDistance && params.hasSuburbs()) {
			Suburb suburb = params.getSuburbs().get(0);
			addField(DISTANCE_FIELD_NAME + ":geodist()");
			query.set("sfield", "classLoc");
			query.set("pt", suburb.getCoordinates());
		}
	}

	private void setUpGroupByCourseId() {
		if (enabledGrouping) {
			query.set("group", "true");
			query.set("group.field", "courseId");
			query.set("group.limit", "1000");
		}
	}

	private void setUpPage(int start, int rows) {
		query.setStart(start);
		query.setRows(rows);
	}
}
