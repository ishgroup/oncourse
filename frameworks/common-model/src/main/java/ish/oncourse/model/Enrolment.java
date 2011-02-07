package ish.oncourse.model;

import ish.oncourse.model.auto._Enrolment;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class Enrolment extends _Enrolment implements Queueable {

	private transient boolean doQueue = true;


	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ?
				(Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
	
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

	public boolean getDoQueue() {
		return doQueue;
	}

	public void setDoQueue(boolean doQueue) {
		this.doQueue = doQueue;
	}

}
