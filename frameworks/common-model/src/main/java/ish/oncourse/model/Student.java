package ish.oncourse.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._Student;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class Student extends _Student {

	/**
	 * @return the number of years since the contact's birth date.
	 */
	public Integer getYearsOfAge() {

		Integer age = null;

		if ((getContact() == null) || (getContact().getDateOfBirth() == null)) {
			Period interval = new Period(getContact().getDateOfBirth()
					.getTime(), System.currentTimeMillis(), PeriodType.years());
			age = interval.getYears();
		}

		return age;
	}

	/**
	 * @return all enrolments for this student where the payment succeeded and
	 *         the course was not cancelled or deleted.
	 */
	public List<Enrolment> getActiveEnrolments() {
		List<Enrolment> enrolments = getEnrolments();
		if ((enrolments == null) || (enrolments.isEmpty())) {
			return new ArrayList<Enrolment>();
		}

		Expression qualifier = ExpressionFactory.matchExp(
				Enrolment.STATUS_PROPERTY, 0/*
											 * TODO Payment.STATUS_SUCCEEDED
											 */).andExp(
				ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY
						+ "." + CourseClass.CANCELLED_PROPERTY, false));

		return qualifier.filterObjects(enrolments);
	}

	public WaitingList getActiveWaitingListForCourse(Course course) {

		List<WaitingList> waits = getWaitingLists();
		Expression qualifier = ExpressionFactory.matchExp(
				WaitingList.COURSE_PROPERTY, course);
		waits = qualifier.filterObjects(waits);
		if (waits.size() > 0) {
			return waits.get(waits.size() - 1);
		}
		return null;
	}

}
