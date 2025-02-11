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
package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.cayenne.glue._Enrolment;
import ish.oncourse.server.cayenne.glue._Invoice;
import ish.statistics.EnrolmentStats;
import ish.statistics.EnrolmentStats.EnrolmentStatsField;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service method to provide client with enrolment statistics
 *
 */
public class StatsService {

	private static final Logger logger = LogManager.getLogger();
	private EnrolmentStats stats;
	private Calendar lastMidnight;
	private Calendar lastRun;

	@Inject
	private ICayenneService cayenneService;

	public synchronized EnrolmentStats getStats() {
		return this.stats;
	}

	/**
	 * @param daysAgo - number of days before today to calculate the number of enrolments for
	 * @param webOnly - whether to account only for web enrolments
	 * @return count of enrolments
	 */
	private Integer getEnrolmentCount(int daysAgo, boolean webOnly) {
		var from = (Calendar) this.lastMidnight.clone();
		from.add(Calendar.DATE, -daysAgo);
		var to = (Calendar) this.lastMidnight.clone();
		to.add(Calendar.DATE, -daysAgo + 1);

		var expr = ExpressionFactory.inExp(_Enrolment.STATUS_PROPERTY, EnrolmentStatus.STATUSES_LEGIT);
		expr = expr.andExp(ExpressionFactory.betweenExp(_Enrolment.CREATED_ON_PROPERTY, from.getTime(), to.getTime()));

		if (webOnly) {
			expr = expr.andExp(ExpressionFactory.matchExp(_Enrolment.SOURCE_PROPERTY, PaymentSource.SOURCE_WEB));
		}

		var sq = SelectQuery.query(Enrolment.class, expr);
		sq.setPageSize(1);

		var list = cayenneService.getNewContext().select(sq);
		return list.size();
	}

	/**
	 * @param daysAgo - number of days before today to calculate the revenue for (based on invoices)
	 * @param webOnly - whether to account only for web revenue
	 * @return total revenue
	 */
	private Money getRevenue(int daysAgo, boolean webOnly) {
		var from = (Calendar) this.lastMidnight.clone();
		from.add(Calendar.DATE, -daysAgo);
		var to = (Calendar) this.lastMidnight.clone();
		to.add(Calendar.DATE, -daysAgo + 1);

		var expr = ExpressionFactory.betweenExp(_Invoice.CREATED_ON_PROPERTY, from.getTime(), to.getTime());

		if (webOnly) {
			expr = expr.andExp(ExpressionFactory.matchExp(_Invoice.SOURCE_PROPERTY, PaymentSource.SOURCE_WEB));
		}

		var list = cayenneService.getNewContext().select(SelectQuery.query(Invoice.class, expr));
		var result = Money.ZERO();
		for (var i : list) {
			result = result.add(i.getTotalIncTax());
		}
		return result;
	}

	/**
	 * @param e to filter classes
	 * @return simple count of course classes
	 */
	private int getClassesCount(Expression e) {
		var sq = SelectQuery.query(CourseClass.class, e);
		sq.setPageSize(1);
		return cayenneService.getNewContext().select(sq).size();
	}

	/**
	 * calculates stats for enrolments and revenue, also creates graphs
	 *
	 * @param newStats to which the calculated values are assigned
	 */
	private void createEnrolmentAndRevenueStats(EnrolmentStats newStats) {
		var enrolmentsDataset = new TimeSeriesCollection();
		var d = new Day();
		var dataEnrolments = new TimeSeries("enrolments", d.getClass());

		var revenueDataset = new TimeSeriesCollection();
		var dataRevenue = new TimeSeries("revenue", d.getClass());

		var weeklyEnrolmentCount = 0;
		var monthlyEnrolmentCount = 0;
		var monthlyWebEnrolmentCount = 0;
		var weeklyRevenue = Money.ZERO();
		var monthlyRevenue = Money.ZERO();
		var monthlyWebRevenue = Money.ZERO();


		List<Integer> enrolmentsCountList = new ArrayList<>(28);
		List<Money> ravenueList = new ArrayList<>(28);

		for (var i = 0; i < 28; i++) {

			int enrolmentsCount = getEnrolmentCount(i, false);
			int webEnrolmentsCount = getEnrolmentCount(i, true);
			var revenue = getRevenue(i, false);
			var webRevenue = getRevenue(i, true);

			if (i == 0) {
				newStats.setValue(EnrolmentStatsField.ENROLMENTS_TODAY, "" + enrolmentsCount);
				newStats.setValue(EnrolmentStatsField.REVENUE_TODAY, revenue.toBigDecimal().toBigInteger().toString());
			}
			if (i < 7) {
				weeklyEnrolmentCount = weeklyEnrolmentCount + enrolmentsCount;
				weeklyRevenue = weeklyRevenue.add(revenue);
			}

			monthlyEnrolmentCount = monthlyEnrolmentCount + enrolmentsCount;
			monthlyWebEnrolmentCount = monthlyWebEnrolmentCount + webEnrolmentsCount;

			monthlyRevenue = monthlyRevenue.add(revenue);
			monthlyWebRevenue = monthlyWebRevenue.add(webRevenue);

			var c = (Calendar) this.lastMidnight.clone();
			c.add(Calendar.DATE, -i);

			dataEnrolments.add(new Day(c.getTime()), enrolmentsCount);
			dataRevenue.add(new Day(c.getTime()), revenue.getNumber());

			enrolmentsCountList.add(0,enrolmentsCount);
			ravenueList.add(0,revenue);
		}

		newStats.setEnrolmentsCountList(enrolmentsCountList);
		newStats.setRavenueList(ravenueList);

		enrolmentsDataset.addSeries(dataEnrolments);
		revenueDataset.addSeries(dataRevenue);

		newStats.setEnrolmentsGraph(graphSparkline(enrolmentsDataset));
		newStats.setRevenueGraph(graphSparkline(revenueDataset));

		newStats.setValue(EnrolmentStatsField.ENROLMENTS_WEEK, "" + weeklyEnrolmentCount);
		newStats.setValue(EnrolmentStatsField.REVENUE_WEEK, weeklyRevenue.toBigDecimal().toBigInteger().toString());
		newStats.setValue(EnrolmentStatsField.ENROLMENTS_MONTH, "" + monthlyEnrolmentCount);
		newStats.setValue(EnrolmentStatsField.REVENUE_MONTH, monthlyRevenue.toBigDecimal().toBigInteger().toString());
		if (monthlyEnrolmentCount == 0) {
			newStats.setValue(EnrolmentStatsField.ENROLMENTS_WEB, "0");
		} else {
			newStats.setValue(EnrolmentStatsField.ENROLMENTS_WEB, "" + monthlyWebEnrolmentCount * 100 / monthlyEnrolmentCount);
		}
		if (Money.ZERO().equals(monthlyRevenue)) {
			newStats.setValue(EnrolmentStatsField.REVENUE_WEB, "0");
		} else {
			newStats.setValue(EnrolmentStatsField.REVENUE_WEB, monthlyWebRevenue.divide(monthlyRevenue).multiply(100).toBigDecimal().toBigInteger().toString());
		}
	}

	/**
	 * creates list of recent enrolments
	 *
	 * @param newStats to which the calculated values are assigned
	 */
	private void createRecentEnrolmentsList(EnrolmentStats newStats) {
		var expr = ExpressionFactory.inExp(_Enrolment.STATUS_PROPERTY, EnrolmentStatus.STATUSES_LEGIT);
		var sq = SelectQuery.query(Enrolment.class, expr);
		sq.addOrdering(Enrolment.CREATED_ON.desc());
		sq.setPageSize(3);
		var enrolments = cayenneService.getNewContext().select(sq);
		newStats.setValue(EnrolmentStatsField.COUNT_ENROLMENTS, "" + enrolments.size());

		if (enrolments.size() == 0) {
			newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT1, "no data");
		} else {
			for (var i = 0; i < 3 && i < enrolments.size(); i++) {
				String output;
				var e = enrolments.get(i);
				output = e.getCourseClass().getCourse().getName() + " ";
				output = output + " <font color=#999999>";
				output = output + getTimeAgo(e.getCreatedOn().getTime());
				output = output + (PaymentSource.SOURCE_WEB.equals(e.getSource()) ? "on web" : "at office");
				output = output + "</font>";
				output = "<html>" + output + "</html>";

				if (i == 0) {
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT1, output);
				} else if (i == 1) {
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT2, output);
				} else if (i == 2) {
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT3, output);
				}
			}
		}

		fillNewDashboardEnrolmentProperties(newStats, enrolments);
	}

	private String getTimeAgo(Long time) {
		var now = new Date();
		var days = TimeUnit.MILLISECONDS.toDays(now.getTime() - time);
		var hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - time);
		var minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - time);
		if (days > 0L) {
			return days + " days ago ";
		} else if (hours > 0L) {
			return hours + " hours ago ";
		} else {
			return minutes + " mins ago ";
		}
	}

	private void fillNewDashboardEnrolmentProperties(EnrolmentStats newStats, List<Enrolment> enrolments) {
		if (enrolments.isEmpty()) {
			newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT1_COURSE, "no data");
		} else {
			for (var i = 0; i < 3 && i < enrolments.size(); i++) {
				var name = enrolments.get(i).getCourseClass().getCourse().getName();
				var timeAgo = getTimeAgo(enrolments.get(i).getCreatedOn().getTime());
				if (i == 0) {
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT1_COURSE, name);
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT1_TIME, timeAgo);
				} else if (i == 1) {
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT2_COURSE, name);
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT2_TIME, timeAgo);
				} else if (i == 2) {
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT3_COURSE, name);
					newStats.setValue(EnrolmentStatsField.ENROLMENT_RECENT3_TIME, timeAgo);
				}
			}
		}
	}

	/**
	 * creates list of most waitlisted courses
	 *
	 * @param newStats to which the calculated values are assigned
	 */
	private void createMostWaitlistedList(EnrolmentStats newStats) {
		var sqlQuery = new SQLTemplate(WaitingList.class,
				"SELECT  #result('courseId' 'java.lang.Long' 'courseId'), #result('count(id)' 'int' 'c')  FROM WaitingList GROUP BY courseId ORDER BY count(id) DESC");

		sqlQuery.setFetchingDataRows(true);

		List<DataRow> rows = cayenneService.getNewContext().performQuery(sqlQuery);

		if (rows.size() == 0) {
			newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE1, "no data");

			newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE1_NAME, "no data");
		} else {
			for (var i = 0; i < 3 && i < rows.size(); i++) {
				String output;
				var e = rows.get(i);

				var list = cayenneService.getNewContext().select(SelectQuery.query(Course.class, Course.ID.eq((Long) e.get("courseId"))));

				if (list.size() > 0) {
					var course = list.get(0);
					output = course.getName() + " <font color=#999999>" + course.getCode() + "</font> " + e.get("c");
					output = "<html>" + output + "</html>";

					if (i == 0) {
						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE1, output);

						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE1_NAME, course.getName());
						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE1_CODE, String.format("%s-%s",course.getCode(), e.get("c")));
					} else if (i == 1) {
						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE2, output);

						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE2_NAME, course.getName());
						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE2_CODE, String.format("%s-%s",course.getCode(), e.get("c")));
					} else if (i == 2) {
						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE3, output);

						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE3_NAME, course.getName());
						newStats.setValue(EnrolmentStatsField.WAITLISTED_COURSE3_CODE, String.format("%s-%s",course.getCode(), e.get("c")));
					}
				}
			}
		}
	}

	/**
	 * re-calculates all the statistical fields
	 */
	public synchronized void execute() {

		// check if the thread run in the last minute
		var calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.MINUTE, -1);
		if (this.lastRun != null && this.lastRun.after(calendar) && this.stats != null) {
			return;
		}

		// create new stats object
		var newStats = new EnrolmentStats();

		// setup start date:
		this.lastMidnight = GregorianCalendar.getInstance();
		this.lastMidnight.set(Calendar.MILLISECOND, 0);
		this.lastMidnight.set(Calendar.SECOND, 0);
		this.lastMidnight.set(Calendar.HOUR_OF_DAY, 0);

		// calculate stats for enrolments and revenue
		createEnrolmentAndRevenueStats(newStats);

		var c = (Calendar) this.lastMidnight.clone();
		c.add(Calendar.DATE, -28);

		// calculate stats for classes
		var expr = ExpressionFactory.matchExp(CourseClass.IS_CANCELLED_PROPERTY, true);
		expr = expr.andExp(ExpressionFactory.greaterExp(CourseClass.END_DATE_TIME_PROPERTY, c.getTime()));
		newStats.setValue(EnrolmentStatsField.CLASSES_CANCELLED, "" + getClassesCount(expr));

		expr = ExpressionFactory.matchExp(CourseClass.IS_CANCELLED_PROPERTY, false);
		expr = expr.andExp(ExpressionFactory.greaterExp(CourseClass.END_DATE_TIME_PROPERTY, new Date()));
		expr = expr.andExp(ExpressionFactory.lessExp(CourseClass.START_DATE_TIME_PROPERTY, new Date()));
		newStats.setValue(EnrolmentStatsField.CLASSES_COMMENCED, "" + getClassesCount(expr));

		expr = ExpressionFactory.lessExp(CourseClass.END_DATE_TIME_PROPERTY, new Date());
		newStats.setValue(EnrolmentStatsField.CLASSES_COMPLETED, "" + getClassesCount(expr));

		expr = ExpressionFactory.matchExp(CourseClass.IS_CANCELLED_PROPERTY, false);
		expr = expr.andExp(ExpressionFactory.greaterExp(CourseClass.END_DATE_TIME_PROPERTY, new Date()));
		expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.IS_ACTIVE_PROPERTY, false));
		newStats.setValue(EnrolmentStatsField.CLASSES_IN_DEVELOPMENT, "" + getClassesCount(expr));

		expr = ExpressionFactory.matchExp(CourseClass.IS_CANCELLED_PROPERTY, false);
		expr = expr.andExp(ExpressionFactory.greaterExp(CourseClass.END_DATE_TIME_PROPERTY, new Date()));
		expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.IS_ACTIVE_PROPERTY, true));
		newStats.setValue(EnrolmentStatsField.CLASSES_OPEN, "" + getClassesCount(expr));

		// calculate stats for record counts
		var sq = SelectQuery.query(Contact.class);
		sq.setPageSize(1);
		newStats.setValue(EnrolmentStatsField.COUNT_CONTACTS, "" + cayenneService.getNewContext().select(sq).size());

		// check most recent enrolments
		createRecentEnrolmentsList(newStats);

		// check most waitlisted courses
		createMostWaitlistedList(newStats);

		this.stats = newStats;
		this.lastRun = GregorianCalendar.getInstance();
	}

	/**
	 * produces 'spark' styled graph line
	 *
	 * @param dataSet
	 * @return image as a byte array
	 */
	public static byte[] graphSparkline(TimeSeriesCollection dataSet) {
		// The sparkline is created by setting a bunch of the visible properties
		// on the domain, range axis and the XYPlot to false

		var x = new DateAxis();
		x.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1));
		x.setTickLabelsVisible(false);
		x.setTickMarksVisible(false);
		x.setAxisLineVisible(false);
		x.setNegativeArrowVisible(false);
		x.setPositiveArrowVisible(false);
		x.setVisible(false);

		var y = new NumberAxis();
		y.setTickLabelsVisible(false);
		y.setTickMarksVisible(false);
		y.setAxisLineVisible(false);
		y.setNegativeArrowVisible(false);
		y.setPositiveArrowVisible(false);
		y.setVisible(false);

		var plot = new XYPlot();
		plot.setInsets(new RectangleInsets(-1, -1, 0, 0));
		plot.setDataset(dataSet);
		plot.setDomainAxis(x);
		plot.setDomainGridlinesVisible(false);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setRangeCrosshairVisible(false);
		plot.setRangeAxis(y);
		var gradient = new GradientPaint(0f, 0f, new Color(172, 195, 255), 0f, 30f, new Color(244, 247, 255));
		var renderer = new XYDifferenceRenderer(gradient, Color.WHITE, false);
		renderer.setSeriesPaint(0, Color.BLACK);
		renderer.setSeriesFillPaint(0, new Color(30, 30, 30));
		renderer.setSeriesStroke(0, new BasicStroke(2));
		plot.setRenderer(0, renderer);
		plot.setBackgroundAlpha(0.0f);
		plot.setBackgroundPaint(new Color(255, 255, 255, 0));

		var chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

		chart.setBorderVisible(false);
		chart.setBackgroundPaint(new Color(255, 255, 255, 0));
		chart.setBackgroundImageAlpha(0.0f);

		try {
			var out = new ByteArrayOutputStream();

			ChartUtilities.writeChartAsPNG(out, chart, 100, 30, null, true, 0);

			out.flush();
			return out.toByteArray();
		} catch (IOException e) {
			logger.warn("failed to create graph.", e);
		}
		return new byte[0];
	}
}
