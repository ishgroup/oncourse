package ish.oncourse.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.util.ExternalValidation;
import ish.oncourse.model.auto._PaymentIn;

public class PaymentIn extends _PaymentIn implements Queueable {

	private transient boolean doQueue = true;

	private static final Logger LOG = Logger.getLogger(PaymentIn.class);


	/**
	 * Returns the primary key property - id of {@link PaymentIn}.
	 * 
	 * @return
	 */
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	/**
	 * Validates the payment details: amount, credit card type, name credit card
	 * of owner, credit card number, credit card expiry date.
	 * 
	 * @return true if no errors exist.
	 */
	public boolean validateBeforeSend() {
		boolean result = true;
		result = validatePaymentAmount() && result;
		result = validateCCType() && result;
		result = validateCCName() && result;
		result = validateCCNumber() == null && result;
		result = validateCCExpiry() && result;
		return result;
	}

	/**
	 * Checks if the payment amount is not null and not negative.
	 * 
	 * @return true if the payment amount is greater or equal to zero.
	 */
	public boolean validatePaymentAmount() {
		BigDecimal amount = getAmount();
		boolean isValid = amount != null && amount.compareTo(BigDecimal.ZERO) != -1;
		if (!isValid) {
			LOG.warn("The payment amount cannot be negative:" + amount);
		}
		return isValid;
	}

	/**
	 * Checks if the credit card type is filled.
	 * 
	 * @return true if the credit card type is not null.
	 */
	public boolean validateCCType() {
		CreditCardType creditCardType = getCreditCardType();
		boolean isValid = creditCardType != null;
		if (!isValid) {
			LOG.warn("The credit card type " + creditCardType + " is invalid");
		}
		return isValid;
	}

	/**
	 * Checks if the credit card owner's name is filled.
	 * 
	 * @return true if the credit card owner's name is not empty.
	 */
	public boolean validateCCName() {
		String creditCardName = getCreditCardName();
		boolean isValid = creditCardName != null && !creditCardName.equals("");
		if (!isValid) {
			LOG.warn("The credit card name " + creditCardName + " is invalid");
		}
		return isValid;
	}

	/**
	 * Validates the syntax of the credit card number,
	 * {@link PaymentIn#getCreditCardNumber()}.
	 * 
	 * @return The error message.
	 */
	public String validateCCNumber() {
		String creditCardNumber = getCreditCardNumber();
		if (creditCardNumber == null || creditCardNumber.equals("")) {
			LOG.warn("The credit card number is invalid blank");
			return "The credit card number cannot be blank.";
		}

		CreditCardType creditCardType = getCreditCardType();
		if (!ExternalValidation.validateCreditCardNumber(creditCardNumber)
				|| (creditCardType != null && !ExternalValidation.validateCreditCardNumber(
						creditCardNumber, creditCardType))) {
			LOG.warn("The credit card number " + creditCardNumber + " is invalid");
			return "Invalid credit card number.";
		}

		return null;
	}

	/**
	 * Validates the credit card expiry date - it should be filed and not in the
	 * past.
	 * 
	 * @return true if the expiry date is valid.
	 */
	public boolean validateCCExpiry() {
		String creditCardExpiry = getCreditCardExpiry();
		if (creditCardExpiry == null || creditCardExpiry.equals("")) {
			LOG.warn("The credit card expiry date cannot be empty");
			return false;
		}
		String[] dateParts = creditCardExpiry.split("/");
		if (dateParts.length != 2 || !dateParts[0].matches("\\d{1,2}")
				&& !dateParts[0].matches("\\d{4}")) {
			LOG.warn("The credit card expiry date " + creditCardExpiry + " has invalid format");
			return false;
		}
		int ccExpiryMonth = Integer.parseInt(dateParts[0]) - 1;
		int ccExpiryYear = Integer.parseInt(dateParts[1]);
		Calendar today=Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, ccExpiryMonth);
		cal.set(Calendar.YEAR, ccExpiryYear);
		
		if (cal.getTime().before(today.getTime())) {
			LOG.warn("The credit card has expired: the date " + creditCardExpiry
					+ " is in past");
			return false;
		}
		return true;
	}

	/**
	 * Sets the status of payment to {@link PaymentStatus#SUCCESS}, and sets the
	 * success statuses to the related invoice ( {@link InvoiceStatus#SUCCESS} )
	 * and enrolment ( {@link EnrolmentStatus#SUCCESS} ).
	 * 
	 * Invoked when the payment gateway processing is succeed.
	 * 
	 */
	public void succeed() {
		setStatus(PaymentStatus.SUCCESS);
		for (PaymentInLine pl : getPaymentInLines()) {
			Invoice invoice = pl.getInvoice();
			invoice.setStatus(InvoiceStatus.SUCCESS);
			for (InvoiceLine il : invoice.getInvoiceLines()) {
				il.getEnrolment().setStatus(EnrolmentStatus.SUCCESS);
			}
		}
	}

	/**
	 * Sets the status of payment to {@link PaymentStatus#FAILED}, and sets the
	 * initial statuses to the related invoice ( {@link InvoiceStatus#PENDING} )
	 * and enrolment ( {@link EnrolmentStatus#PENDING} ).
	 * 
	 * Invoked when the payment gateway processing is failed(the payment is failed because of declined card).
	 * 
	 */
	public void failed() {
		setStatus(PaymentStatus.FAILED);
		setStatusNotes("Card declined");
		for (PaymentInLine pl : getPaymentInLines()) {
			Invoice invoice = pl.getInvoice();
			invoice.setStatus(InvoiceStatus.PENDING);
			for (InvoiceLine il : invoice.getInvoiceLines()) {
				il.getEnrolment().setStatus(EnrolmentStatus.PENDING);
			}
		}
	}

	/**
	 * If the contact has a student reference, sets it to the payment.
	 * 
	 * @param contact
	 *            the contact to set.
	 */
	@Override
	public void setContact(Contact contact) {
		super.setContact(contact);
		Student student = contact.getStudent();
		if (student != null) {
			setStudent(student);
		}
	}

	/**
	 * Retrieves the "client" identificator, ie with the "W" addon.
	 * 
	 * @return client identificator string.
	 */
	public String getClientReference() {
		return PaymentSource.SOURCE_WEB.getDatabaseValue() + getId();
	}

	public boolean getDoQueue() {
		return doQueue;
	}

	public void setDoQueue(boolean doQueue) {
		this.doQueue = doQueue;
	}

}
