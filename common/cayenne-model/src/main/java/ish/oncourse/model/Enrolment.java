package ish.oncourse.model;

import ish.common.payable.EnrolmentInterface;
import ish.common.types.ConfirmationStatus;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.common.field.*;
import ish.oncourse.model.auto._Enrolment;
import ish.oncourse.utils.MessageFormat;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.validation.EnrolmentStatusValidator;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static ish.common.types.EnrolmentStatus.QUEUED;
import static ish.common.types.EnrolmentStatus.SUCCESS;
import static java.lang.String.format;

@Type(value = ContextType.ENROLMENT)
public class Enrolment extends _Enrolment implements EnrolmentInterface, Queueable {

	private static final long serialVersionUID = 8361159336001022666L;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Statuses for which the class place is considered to be occupied.
	 */
	public static List<EnrolmentStatus> VALID_ENROLMENTS = Arrays.asList(EnrolmentStatus.IN_TRANSACTION, SUCCESS);

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * Checks if this enrolment is duplicated, ie checks if there is a record
	 * with such a courseClass and student, that this enrolment has.
	 *
	 * @return true if the student is already enrolled to the courseClass.
	 */
	public boolean isDuplicated() {
		return getStudent() != null && getCourseClass() != null &&
				!Enrolment.STUDENT.eq(getStudent()).filterObjects(getCourseClass().getValidEnrolments()).isEmpty();
	}

	@Override
	protected void onPostAdd() {
		if (getStatus() == null) {
			setStatus(EnrolmentStatus.NEW);
		}

		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_WEB);
		}
		if (getCreated() == null) {
			setCreated(new Date());
		}
		if (getModified() == null) {
			setModified(getCreated());
		}
		if (getFeeHelpAmount() == null) {
			setFeeHelpAmount(Money.ZERO);
		}
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
		}

	}

	@Override
	protected void onPrePersist() {
		onPostAdd();
	}

	/**
	 * Convenience method getting an attendance for a given session and student
	 *
	 * @param session - session linked to the attendance, cannot be null
	 * @param student - student linked to the attendance, cannot be null
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
	 *
	 * @return the original enrollment invoice line
	 */
	@Deprecated
	public InvoiceLine getOriginalInvoiceLine() {
		if (getInvoiceLines() != null && !getInvoiceLines().isEmpty()) {
			SortedSet<InvoiceLine> invoices = new TreeSet<>(new Comparator<InvoiceLine>() {
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
				logger.error(format("Negative invoiceLine with id = %s used as original for enrolment with id = %s", originalInvoiceLine.getId(), getId()));
			}
			return originalInvoiceLine;
		}
		return null;
	}

	/**
	 * Check if async replication is allowed on this object. To replicate enrolment shouldn't have null, QUEUED or IN_TRANSACTION statuses.
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
		return getStatus() != null && getStatus() != EnrolmentStatus.IN_TRANSACTION && getStatus() != QUEUED;
	}

	@Override
	public void setStatus(EnrolmentStatus status) {
		if (getStatus() != null) {
			validateStatus(status);
		}
		super.setStatus(status);
	}

	private void validateStatus(EnrolmentStatus status) {
		if (status == null) {
			String message = MessageFormat.valueOf(this, "Cannot set null status!").format();
			throw new NullPointerException(message);
		}
		if (status == getStatus()) {
			return;
		}
		boolean error = !EnrolmentStatusValidator.valueOf(this.getStatus(), status).validate();
		if (error) {
			String message = MessageFormat.valueOf(this, "Can't set the %s status for enrolment with %s status!", status, getStatus()).format();
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	public boolean isInFinalStatus() {
		return EnrolmentStatus.STATUSES_FINAL.contains(getStatus());
	}

	@Override
	public Date getCreatedOn() {
		return super.getCreated();
	}
	
	@Override
	public CourseClass getCourseClass() {
		return super.getCourseClass();
	}


	@Property(value = FieldProperty.CUSTOM_FIELD_ENROLMENT, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value){
		setCustomFieldValue(key, value, EnrolmentCustomField.class);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_ENROLMENT, type = PropertyGetSetFactory.GET, params = {String.class})
	public String getCustomFieldValue(String key) {
		CustomField field = getCustomField(key);
		return  field == null ? null : field.getValue();
	}
}
