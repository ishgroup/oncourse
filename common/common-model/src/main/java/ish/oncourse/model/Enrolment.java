package ish.oncourse.model;

import java.util.List;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.auto._Enrolment;
import ish.oncourse.utils.QueueableObjectUtils;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class Enrolment extends _Enrolment implements Queueable {

	private static final long serialVersionUID = 8361159336001022666L;

	/**
	 * Statuses for which the class place is considered to be occupied.
	 */
	public static EnrolmentStatus[] VALID_ENROLMENTS = new EnrolmentStatus[] { EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.SUCCESS };

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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
			setStatus(EnrolmentStatus.IN_TRANSACTION);
		}

		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_WEB);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.model.auto._Enrolment#onPrePersist()
	 */
	@Override
	protected void onPrePersist() {
		onPostAdd();
	}

	/**
	 * Convenience method getting an attendance for a given session and student
	 * 
	 * @param session
	 *            - session linked to the attendance, cannot be null
	 * @param student
	 *            - student linked to the attendance, cannot be null
	 * @return Attendance
	 */
	public Attendance getAttendanceForSessionAndStudent(Session session, Student student) {
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

	/**
	 * Check if async replication is allowed on this object.
	 * 
	 * @return
	 */
	public boolean isAsyncReplicationAllowed() {

		if (getInvoiceLine() != null && !getInvoiceLine().getInvoice().getPaymentInLines().isEmpty()) {
			for (PaymentInLine line : getInvoiceLine().getInvoice().getPaymentInLines()) {
				PaymentIn paymentIn = line.getPaymentIn();
				if (paymentIn.getStatus() != PaymentStatus.IN_TRANSACTION && paymentIn.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED) {
					return true;
				}
			}
			return false;
		}

		return getStatus() != null && getStatus() != EnrolmentStatus.IN_TRANSACTION;
	}
}
