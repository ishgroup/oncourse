/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.statistics;

import ish.math.Money;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Serialisable class for transporting whole set of numbers and some images from server to client<br />
 *
 */
public class EnrolmentStats implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_ENROLMENT = "enrolment";
	public static final String TYPE_REVENUE = "revenue";
	public static final String TYPE_CLASSES = "classes";
	public static final String TYPE_COUNT = "count";

	// enum of statistics values calculated
	public enum EnrolmentStatsField {
		ENROLMENTS_TODAY(TYPE_ENROLMENT, "today "),
		ENROLMENTS_WEEK(TYPE_ENROLMENT, "week "),
		ENROLMENTS_MONTH(TYPE_ENROLMENT, "month "),
		ENROLMENTS_WEB(TYPE_ENROLMENT, "web ", "%"),
		REVENUE_TODAY(TYPE_REVENUE, "today $"),
		REVENUE_WEEK(TYPE_REVENUE, "week $"),
		REVENUE_MONTH(TYPE_REVENUE, "month $"),
		REVENUE_WEB(TYPE_REVENUE, "web ", "%"),
		CLASSES_IN_DEVELOPMENT(TYPE_CLASSES, "In development ", ""),
		CLASSES_OPEN(TYPE_CLASSES, "Open for enrolment ", ""),
		CLASSES_COMMENCED(TYPE_CLASSES, "Commenced ", ""),
		CLASSES_CANCELLED(TYPE_CLASSES, "Cancelled ", ""),
		CLASSES_COMPLETED(TYPE_CLASSES, "Completed ", ""),
		COUNT_CONTACTS(TYPE_COUNT, "Contacts ", ""),
		COUNT_ENROLMENTS(TYPE_COUNT, "Enrolments ", ""),
		WAITLISTED_COURSE1("waitlistCourse1", "", ""),
		WAITLISTED_COURSE2("waitlistCourse2", "", ""),
		WAITLISTED_COURSE3("waitlistCourse3", "", ""),

		WAITLISTED_COURSE1_NAME("waitlistCourse1_name", "", ""),
		WAITLISTED_COURSE2_NAME("waitlistCourse2_name", "", ""),
		WAITLISTED_COURSE3_NAME("waitlistCourse3_name", "", ""),
		WAITLISTED_COURSE1_CODE("waitlistCourse1_code", "", ""),
		WAITLISTED_COURSE2_CODE("waitlistCourse2_code", "", ""),
		WAITLISTED_COURSE3_CODE("waitlistCourse3_code", "", ""),

		ENROLMENT_RECENT1("recentEnrolments1", "", ""),
		ENROLMENT_RECENT2("recentEnrolments2", "", ""),
		ENROLMENT_RECENT3("recentEnrolments3", "", ""),

		ENROLMENT_RECENT1_COURSE("recentEnrolments1_course", "", ""),
		ENROLMENT_RECENT2_COURSE("recentEnrolments2_course", "", ""),
		ENROLMENT_RECENT3_COURSE("recentEnrolments3_course", "", ""),
		ENROLMENT_RECENT1_TIME("recentEnrolments1_time", "", ""),
		ENROLMENT_RECENT2_TIME("recentEnrolments2_time", "", ""),
		ENROLMENT_RECENT3_TIME("recentEnrolments3_time", "", "");

		private String type;
		private String displayPrefix;
		private String suffix;

		EnrolmentStatsField(String s, String displayPrefix) {
			this(s, displayPrefix, "");
		}

		EnrolmentStatsField(String s, String displayPrefix, String suffix) {
			this.type = s;
			this.displayPrefix = displayPrefix;
			this.suffix = suffix;
		}

		public String getGuiKey() {
			return this.type + "_" + this.displayPrefix;
		}

		public String getDisplayPrefix() {
			return this.displayPrefix;
		}

		/**
		 * @return the suffix
		 */
		public String getDisplaySuffix() {
			return this.suffix;
		}

	}

	// map of values
	private Map<EnrolmentStatsField, String> values;

	// graph of recent enrolments
	private byte[] enrolmentsGraph;
	// graph of recent revenue
	private byte[] revenueGraph;


	private List<Integer> enrolmentsCountList;
	private List<Money> ravenueList;


	public List<Integer> getEnrolmentsCountList() {
		return enrolmentsCountList;
	}

	public void setEnrolmentsCountList(List<Integer> enrolmentsCountList) {
		this.enrolmentsCountList = enrolmentsCountList;
	}

	public List<Money> getRavenueList() {
		return ravenueList;
	}

	public void setRavenueList(List<Money> ravenueList) {
		this.ravenueList = ravenueList;
	}

	/**
	 * default constructor
	 */
	public EnrolmentStats() {
		this.values = new EnumMap<>(EnrolmentStatsField.class);
	}

	/**
	 * @param key to set
	 * @param value to set
	 */
	public void setValue(EnrolmentStatsField key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}

		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}

		this.values.put(key, value);
	}

	/**
	 * @param key to fetch
	 * @return number value
	 */
	public String getValue(EnrolmentStatsField key) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}

		return this.values.get(key);
	}

	/**
	 * @param enrolmentsGraph the enrolmentsGraph to set
	 */
	public void setEnrolmentsGraph(byte[] enrolmentsGraph) {
		this.enrolmentsGraph = enrolmentsGraph.clone();
	}

	/**
	 * @return the enrolmentsGraph
	 */
	public byte[] getEnrolmentsGraph() {
		return this.enrolmentsGraph;
	}

	/**
	 * @param revenueGraph the revenueGraph to set
	 */
	public void setRevenueGraph(byte[] revenueGraph) {
		this.revenueGraph = revenueGraph.clone();
	}

	/**
	 * @return the revenueGraph
	 */
	public byte[] getRevenueGraph() {
		return this.revenueGraph;
	}
}
