package ish.oncourse.model;

import ish.common.payable.EnrolmentInterface;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.model.auto._Enrolment;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Enrolment extends _Enrolment implements EnrolmentInterface,Queueable {

	private static final long serialVersionUID = 8361159336001022666L;
	private static final Logger LOG = Logger.getLogger(Enrolment.class);
	public static final String TO_MANY_INVOICE_LINE_SUPPORT_VERSION = "4.1";

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
	 * Get the original enrollment invoice line. 
	 * This is a workaround to detect which invoice line should be linked with enrollment as "original". 
	 * Currently not-negative invoice line with lowest create date used for this cases.
	 * If negative (reverse invoice line) linked with enrollment they should not be used as "original".
	 * @return the original enrollment invoice line
	 */
	@Deprecated
	public InvoiceLine getOriginalInvoiceLine() {
		if (getInvoiceLines() != null && !getInvoiceLines().isEmpty()) {
			SortedSet<InvoiceLine> invoices = new TreeSet<InvoiceLine>(new Comparator<InvoiceLine> () {
				@Override
				public int compare(InvoiceLine invoiceLine0, InvoiceLine invoiceLine1) {
					if (!invoiceLine0.getFinalPriceToPayIncTax().isLessThan(Money.ZERO) && !invoiceLine1.getFinalPriceToPayIncTax().isLessThan(Money.ZERO)) {
						return invoiceLine0.getCreated().compareTo(invoiceLine1.getCreated());
					}
					int compareResult = invoiceLine1.getFinalPriceToPayIncTax().compareTo(invoiceLine0.getFinalPriceToPayIncTax());
					if (compareResult == 0) {
						compareResult = invoiceLine0.getCreated().compareTo(invoiceLine1.getCreated());
					}
					return compareResult;
				}
			});
			for (InvoiceLine invoiceLine : getInvoiceLines()) {
				invoices.add(invoiceLine);
			}
			InvoiceLine originalInvoiceLine = invoices.first();
			if (originalInvoiceLine.getFinalPriceToPayIncTax().isLessThan(Money.ZERO)) {
				LOG.error(String.format("Negative invoiceLine with id = %s used as original for enrolment with id = %s", originalInvoiceLine.getId(), getId()));
			}
			return originalInvoiceLine;
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
		if (getInvoiceLines() != null && !getInvoiceLines().isEmpty()) {
			for (InvoiceLine invoiceLine : getInvoiceLines()) {
				if (!invoiceLine.getInvoice().getPaymentInLines().isEmpty()) {
					for (PaymentInLine paymentInLine : invoiceLine.getInvoice().getPaymentInLines()) {
						PaymentIn paymentIn = paymentInLine.getPaymentIn();
						final ObjectIdQuery q = new ObjectIdQuery(paymentIn.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
						paymentIn = (PaymentIn) Cayenne.objectForQuery(getObjectContext(), q);
						if (paymentIn.isAsyncReplicationAllowed()) {
							return true;
						}
					}
				} else {
					return isAsyncReplicationAllowedByStatusCheck();
				}
			}
			return false;
		}		
		return isAsyncReplicationAllowedByStatusCheck();
	}
	
	private boolean isAsyncReplicationAllowedByStatusCheck() {
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
				// TODO: status NEW should be included in this test after task 17341 will be done
				if (status == null || /** EnrolmentStatus.NEW.equals(status) ||**/ EnrolmentStatus.QUEUED.equals(status)) {
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
