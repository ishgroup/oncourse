package ish.oncourse.model;

import ish.common.util.ExternalValidation;
import ish.oncourse.model.auto._PaymentIn;

public class PaymentIn extends _PaymentIn {

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
	 * Validates the syntax of the credit card number,
	 * {@link PaymentIn#getCreditCardNumber()}.
	 * 
	 * @return The error message.
	 */
	public String validateCCNumber() {
		if (getCreditCardNumber() == null || getCreditCardNumber().equals("")) {
			return "The credit card number cannot be blank.";
		}

		if (!ExternalValidation.validateCreditCardNumber(getCreditCardNumber())
				|| (getCreditCardType() != null && !ExternalValidation.validateCreditCardNumber(
						getCreditCardNumber(), getCreditCardType()))) {

			return "Invalid credit card number.";
		}

		return null;
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
	 * Invoked when the payment gateway processing is failed.
	 * 
	 */
	public void failed() {
		setStatus(PaymentStatus.FAILED);
		for (PaymentInLine pl : getPaymentInLines()) {
			Invoice invoice = pl.getInvoice();
			invoice.setStatus(InvoiceStatus.PENDING);
			for (InvoiceLine il : invoice.getInvoiceLines()) {
				il.getEnrolment().setStatus(EnrolmentStatus.PENDING);
			}
			// FIXME should this dependency be removed?
			invoice.removeFromPaymentInLines(pl);
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

}
