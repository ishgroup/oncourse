package ish.oncourse.model;

import ish.oncourse.model.auto._Enrolment;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class Enrolment extends _Enrolment {

	/**
	 * Checks if this enrolment is duplicated, ie checks if there is a record
	 * with such a courseClass and student, that this enrolment has.
	 * 
	 * @return true if the student is already enrolled to the courseClass.
	 */
	public Boolean isDuplicated() {
		if (getStudent() == null || getCourseClass() == null) {
			return null;
		}

		Expression filter = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, getStudent());

		return !filter.filterObjects(getCourseClass().getValidEnrolments()).isEmpty();
	}

}
