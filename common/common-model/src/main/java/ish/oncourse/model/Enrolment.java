package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.auto._Enrolment;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.List;

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
	
	@Override
	public void setStatus(EnrolmentStatus status) {
		if (PaymentSource.SOURCE_WEB.equals(getSource()) 
				&& (EnrolmentStatus.IN_TRANSACTION.equals(getStatus()) || EnrolmentStatus.NEW.equals(getStatus()))
				&& (EnrolmentStatus.SUCCESS.equals(status))) {
			
			if (getCourseClass() != null) {
				for (Session session : getCourseClass().getSessions()) {
					if (getAttendanceForSessionAndStudent(session, getStudent()) == null) {
						Attendance a = getObjectContext().newObject(Attendance.class);
						a.setAttendanceType(0);
						a.setSession(session);
						a.setStudent(getStudent());
						a.setCollege(getCollege());
					}
				}
			}
		}
		super.setStatus(status);
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
	 * Check if async replication is allowed on this object. To replicate enrolment shouldn't have null, QUEUED or IN_TRANSACTION statuses.
	 * 
	 * @return
	 */
	public boolean isAsyncReplicationAllowed() {
		//first of all we check if enrolment, linked to PaymentIn with either SUCCESS or FAIL status. 
		//If so enrolment is allowed to go to the queue. We need that since there may be several payments made for enrolment,
		//for instance when first payment failed and second payment is in progress, enrolment is allowed to go to the queue.
		if (getInvoiceLine() != null && !getInvoiceLine().getInvoice().getPaymentInLines().isEmpty()) {
			for (PaymentInLine line : getInvoiceLine().getInvoice().getPaymentInLines()) {
				PaymentIn paymentIn = line.getPaymentIn();
				if (paymentIn.getStatus() != PaymentStatus.IN_TRANSACTION && paymentIn.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED) {
					return true;
				}
			}
			return false;
		}
		
		return getStatus() != null && getStatus() != EnrolmentStatus.IN_TRANSACTION && getStatus() != EnrolmentStatus.QUEUED;
	}
}
