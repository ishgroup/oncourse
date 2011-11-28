package ish.oncourse.model;

import java.util.List;

import ish.common.types.PaymentSource;
import ish.oncourse.model.auto._Enrolment;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class Enrolment extends _Enrolment implements Queueable {

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

	@Override
	protected void onPostAdd() {
		if (getStatus() == null) {
			setStatus(EnrolmentStatus.PENDING);
		}
		
		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_WEB);
		}
	}

	/* (non-Javadoc)
	 * @see ish.oncourse.model.auto._Enrolment#onPrePersist()
	 */
	@Override
	protected void onPrePersist() {
		onPostAdd();
		updateAttendanceAndOutcomes();
	}
	
	@Override
	protected void onPreUpdate() {
		updateAttendanceAndOutcomes();
	}
	
	/**
	 * Removes Outcomes and Attendances them in case of changing the enrolment to one of the failed statuses etc.
	 * 
	 */
	private void updateAttendanceAndOutcomes() {
		if (EnrolmentStatus.CANCELLED.equals(getStatus()) || EnrolmentStatus.FAILED.equals(getStatus()) ||
				EnrolmentStatus.REFUNDED.equals(getStatus())) {
			for (Session session : getCourseClass().getSessions()) {
				Attendance a = getAttendanceForSessionAndStudent(session, getStudent());
				if (a != null)
					getObjectContext().deleteObject(a);
			}
			// there should be no outcomes yet (during pre-persist), but just to be sure
			deleteOutcomes();

		}
	}

	private void deleteOutcomes() {
		List<Outcome> outcomes = getOutcomes();
		for (Outcome o : outcomes) {
			getObjectContext().deleteObject(o);
		}
	}
	
	/**
	 * Convenience method getting an attendance for a given session and student
	 * 
	 * @param session - session linked to the attendance, cannot be null
	 * @param student - student linked to the attendance, cannot be null
	 * @return Attendance
	 */
	private Attendance getAttendanceForSessionAndStudent(Session session, Student student) {
		if (student == null || session == null) {
			return null;
		}

		List<Attendance> attendances = session.getAttendances();

		for (Attendance at : attendances) {
			if (at.getStudent().equals(student) || at.getStudent().getObjectId().equals(student.getObjectId())) {
				return at;
			}
		}

		return null;
	}
}
