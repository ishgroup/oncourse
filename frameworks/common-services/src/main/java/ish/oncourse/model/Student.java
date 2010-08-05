package ish.oncourse.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._Student;

public class Student extends _Student {
	/**
	 * @return the number of years since the contact's birth date.
	 */
	public Integer getYearsOfAge() {

		if (getBirthDate() == null) {
			return null;
		}

		Calendar ref = Calendar.getInstance();
		Calendar stu = Calendar.getInstance();
		stu.setTime(getBirthDate());

		int refMonth = ref.get(Calendar.MONTH);
		int refDay = ref.get(Calendar.DATE);
		int stuMonth = stu.get(Calendar.MONTH);
		int stuDay = stu.get(Calendar.DATE);

		int age = ref.get(Calendar.YEAR) - stu.get(Calendar.YEAR);
		if (refMonth > stuMonth || refMonth == stuMonth && refDay > stuDay) {
			age--;
		}
		return Integer.valueOf(age);

	}

	/**
	 * @return all enrolments for this student where the payment succeeded and
	 *         the course was not cancelled or deleted.
	 */
	public List<Enrolment> getActiveEnrolments() {
		List<Enrolment> enrolments = getEnrolments();
		if (enrolments == null || enrolments.size() == 0) {
			return new ArrayList<Enrolment>();
		}
		Expression qualifier = ExpressionFactory
				.matchExp(Enrolment.STATUS_PROPERTY, 0/*
													 * TODO
													 * Payment.STATUS_SUCCEEDED
													 */)
				.andExp(
						ExpressionFactory
								.matchExp(Enrolment.COURSE_CLASS_PROPERTY + "."
										+ CourseClass.CANCELLED_PROPERTY, false))
				.andExp(
						ExpressionFactory.matchExp(
								Enrolment.COURSE_CLASS_PROPERTY + "."
										+ CourseClass.DELETED_PROPERTY, false));
		return qualifier.filterObjects(enrolments);
	}

}
