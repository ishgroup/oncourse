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
package ish.oncourse.entity.services;

import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.CourseClass;
import ish.persistence.CommonExpressionFactory;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.Date;

/**
 */
public class CourseService {

	public static Expression getExpressionForNotCancelledClasses() {
		return ExpressionFactory.noMatchExp(CourseClass.IS_CANCELLED_KEY, Boolean.TRUE);
	}

	public static Expression getExpressionForClassesWhichNotEnded() {
		return ExpressionFactory.greaterOrEqualExp(CourseClass.END_DATE_TIME_KEY, CommonExpressionFactory.previousMidnight(new Date()));
	}

	public static Expression getExpressionForClassesWhichNotEndedOrHaveNoEndDate() {
		Expression result = ExpressionFactory.matchExp(CourseClass.END_DATE_TIME_KEY, null);
		result = result.orExp(getExpressionForClassesWhichNotEnded());
		result = result.andExp(getExpressionForNotCancelledClasses());
		return result;
	}

	/**
	 * @return number of classes which are current
	 * deprecated, use CourseTrait.
	 */
	@Deprecated
	public Integer getCurrentClassesCount(Course course) {
		if (course.getCourseClasses() == null || course.getCourseClasses().size() == 0) {
			return 0;
		}

		return getExpressionForClassesWhichNotEndedOrHaveNoEndDate().filterObjects(course.getCourseClasses()).size();
	}

}
