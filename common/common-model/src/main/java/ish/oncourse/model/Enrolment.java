package ish.oncourse.model;

import ish.common.payable.EnrolmentInterface;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.oncourse.model.auto._Enrolment;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;

public class Enrolment extends _Enrolment implements EnrolmentInterface,Queueable {

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
			if (at.getStudent() != null && (at.getStudent().equals(student) || at.getStudent().getObjectId().equals(student.getObjectId()))) {
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
				final ObjectIdQuery q = new ObjectIdQuery(paymentIn.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
				paymentIn = (PaymentIn) Cayenne.objectForQuery(getObjectContext(), q);
				if (paymentIn.isAsyncReplicationAllowed()) {
					return true;
				}
			}
			return false;
		}
		
		return getStatus() != null && getStatus() != EnrolmentStatus.IN_TRANSACTION && getStatus() != EnrolmentStatus.QUEUED;
	}
	
	@Override
	public void setStatus(EnrolmentStatus status) {
		if (getStatus() == null) {
			//nothing to check
		} else {
			switch (getStatus()) {
			case NEW:
				if (status == null) {
					throw new IllegalArgumentException("Can't set the empty enrolment status!");
				}
				break;
			case QUEUED:
				if (status == null || EnrolmentStatus.NEW.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for enrolment with %s status!", status, getStatus()));
				}
				break;
			case IN_TRANSACTION:
				if (status == null /*|| EnrolmentStatus.NEW.equals(status)*/ || EnrolmentStatus.QUEUED.equals(status)) {//TODO: adjust the check after #17611
					throw new IllegalArgumentException(String.format("Can't set the %s status for enrolment with %s status!", status, getStatus()));
				}
				break;
			case SUCCESS:
				if (!(EnrolmentStatus.SUCCESS.equals(status) || EnrolmentStatus.CANCELLED.equals(status) || EnrolmentStatus.REFUNDED.equals(status))) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for enrolment with %s status!", status, getStatus()));
				}
				break;
			case FAILED:
			case FAILED_CARD_DECLINED:
			case FAILED_NO_PLACES:
			case CANCELLED:
			case REFUNDED:
			case CORRUPTED:
				if (!(getStatus().equals(status))) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for enrolment with %s status!", status, getStatus()));
				}
				break;
			default:
				throw new IllegalArgumentException(String.format("Unsupported status %s found for enrolment", getStatus()));
			}
		}
		super.setStatus(status);
	}

	@Override
	public boolean isInFinalStatus() {
		return EnrolmentStatus.STATUSES_FINAL.contains(getStatus());
	}
}
