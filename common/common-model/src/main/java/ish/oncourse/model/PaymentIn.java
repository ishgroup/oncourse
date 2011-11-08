package ish.oncourse.model;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.util.ExternalValidation;
import ish.math.Money;
import ish.oncourse.model.auto._PaymentIn;
import ish.util.CreditCardUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.log4j.Logger;

public class PaymentIn extends _PaymentIn implements Queueable {

	private static final Logger LOG = Logger.getLogger(PaymentIn.class);

	public static final String PAYMENT_PROCESSED_PARAM = "payment_processed";

	/**
	 * Returns the primary key property - id of {@link PaymentIn}.
	 * 
	 * @return
	 */
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	/**
	 * Validation to prevent saving unbalanced PaymentIn into database.
	 */
	@Override
	protected void validateForSave(ValidationResult result) {

		super.validateForSave(result);

		// Amount - mandatory field
		if (getAmount() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, _PaymentIn.AMOUNT_PROPERTY, "The payment amount cannot be empty."));
			return;
		}

		Money amount = new Money(getAmount());

		if (amount.compareTo(Money.ZERO) < 0) {
			result.addFailure(ValidationFailure.validationFailure(this, _PaymentIn.AMOUNT_PROPERTY,
					"The payment-in must have non negative amount."));
			return;
		}

		Money sum = Money.ZERO;
		List<PaymentInLine> list = getPaymentInLines();
		if (list != null)
			for (PaymentInLine pinl : list)
				sum = sum.add(pinl.getAmount());

		if (!amount.equals(sum)) {
			result.addFailure(ValidationFailure.validationFailure(this, _PaymentIn.AMOUNT_PROPERTY, String.format(
					"The payment willowId:%s angelId:%s amount does not match the sum of amounts allocated for invoices/credit notes.",
					getId(), getAngelId())));
		}

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
	 * Validates credit card cvv, expecting maximum 4 digits.
	 * 
	 * @return true if valid
	 */
	public boolean validateCVV() {
		boolean isValid = true;

		if (getCreditCardCVV() != null) {
			isValid = getCreditCardCVV().matches("\\d{1,4}");
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
				|| (creditCardType != null && !ExternalValidation.validateCreditCardNumber(creditCardNumber, creditCardType))) {
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
		if (dateParts.length != 2 || !dateParts[0].matches("\\d{1,2}") && !dateParts[0].matches("\\d{4}")) {
			LOG.warn("The credit card expiry date " + creditCardExpiry + " has invalid format");
			return false;
		}
		int ccExpiryMonth = Integer.parseInt(dateParts[0]) - 1;
		int ccExpiryYear = Integer.parseInt(dateParts[1]);
		Calendar today = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, ccExpiryMonth);
		cal.set(Calendar.YEAR, ccExpiryYear);

		if (cal.getTime().before(today.getTime())) {
			LOG.warn("The credit card has expired: the date " + creditCardExpiry + " is in past");
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

		Invoice activeInvoice = findActiveInvoice();

		if (activeInvoice != null) {
			activeInvoice.setStatus(InvoiceStatus.SUCCESS);
			for (InvoiceLine il : activeInvoice.getInvoiceLines()) {
				Enrolment enrol = il.getEnrolment();
				if (enrol != null) {
					enrol.setStatus(EnrolmentStatus.SUCCESS);
				}
			}
		}
	}
	
	/**
	 * Finds invoice which is current and is performed operation on.
	 * @return active invoice
	 */
	private Invoice findActiveInvoice() {
		
		SortedSet<Invoice> invoices = new TreeSet<Invoice>(new Comparator<Invoice>() {
			public int compare(Invoice o1, Invoice o2) {
				return (getSource() == PaymentSource.SOURCE_ONCOURSE) ? o2.getInvoiceNumber().compareTo(o1.getInvoiceNumber()) : o2.getId().compareTo(o1.getId()) ;
			}
		});
		
		for (PaymentInLine line : getPaymentInLines()) {
			Invoice invoice = line.getInvoice();
			invoices.add(invoice);
		}
		
		Invoice activeInvoice = invoices.first();
		return activeInvoice;
	}

	/**
	 * Fails payment, but does not override state if already FAILED.Sets the
	 * status of payment to {@link PaymentStatus#FAILED}, and sets the failed
	 * statuses to the related invoice ( {@link InvoiceStatus#FAILED} ) and
	 * enrolment ( {@link EnrolmentStatus#FAILED} ). Creates the refund invoice.
	 */
	public PaymentIn abandonPayment() {

		switch (getStatus()) {
		case FAILED:
		case FAILED_CARD_DECLINED:
		case FAILED_NO_PLACES:
			break;
		default:
			setStatus(PaymentStatus.FAILED);
		}

		if (!getPaymentInLines().isEmpty()) {

			// Pick up the newest invoice for refund.
			PaymentInLine paymentInLineToRefund = null;
			Invoice invoiceToRefund = null;

			for (PaymentInLine line : getPaymentInLines()) {
				Invoice invoice = line.getInvoice();

				if (invoiceToRefund == null) {
					paymentInLineToRefund = line;
					invoiceToRefund = invoice;
				} else {
					// For angel payments use angelId to determine the last
					// invoice, since createdDate is very often the same
					// accross several invoices
					if (getSource() == PaymentSource.SOURCE_ONCOURSE) {
						if (invoice.getInvoiceNumber() > invoiceToRefund.getInvoiceNumber()) {
							paymentInLineToRefund = line;
							invoiceToRefund = invoice;
						}
					} else {
						// For willow payments, use willowId to determine
						// the newest invoice.
						if (invoice.getId() > invoiceToRefund.getId()) {
							paymentInLineToRefund = line;
							invoiceToRefund = invoice;
						}
					}
				}
			}

			if (invoiceToRefund != null) {
				invoiceToRefund.setStatus(InvoiceStatus.FAILED);
				// Creating internal payment, with zero amount which will be
				// linked to invoiceToRefund, and refundInvoice.
				PaymentIn internalPayment = makeShallowCopy();
				internalPayment.setAmount(BigDecimal.ZERO);
				internalPayment.setType(PaymentType.INTERNAL);
				internalPayment.setStatus(PaymentStatus.SUCCESS);

				invoiceToRefund.setStatus(InvoiceStatus.FAILED);

				// Creating refund invoice
				Invoice refundInvoice = invoiceToRefund.createRefundInvoice();
				LOG.info(String.format("Created refund invoice with amount:%s for invoice:%s.", refundInvoice.getAmountOwing(),
						invoiceToRefund.getId()));

				PaymentInLine refundPL = getObjectContext().newObject(PaymentInLine.class);
				refundPL.setAmount(BigDecimal.ZERO.subtract(paymentInLineToRefund.getAmount()));
				refundPL.setCollege(getCollege());
				refundPL.setInvoice(refundInvoice);
				refundPL.setPaymentIn(internalPayment);

				PaymentInLine paymentInLineToRefundCopy = paymentInLineToRefund.makeCopy();
				paymentInLineToRefundCopy.setPaymentIn(internalPayment);

				// Fail enrolments on invoiceToRefund
				for (InvoiceLine il : invoiceToRefund.getInvoiceLines()) {
					Enrolment enrol = il.getEnrolment();
					if (enrol != null) {
						boolean shouldFailEnrolment = enrol.getStatus() == null || enrol.getStatus() == EnrolmentStatus.PENDING
								|| enrol.getStatus() == EnrolmentStatus.IN_TRANSACTION;
						if (shouldFailEnrolment) {
							enrol.setStatus(EnrolmentStatus.FAILED);
						}
					}
				}

				return internalPayment;
			} else {
				LOG.error(String.format("Can not find invoice to refund on paymentIn:%s.", getId()));
			}
		} else {
			LOG.error(String.format("Can not abandon paymentIn:%s, since it doesn't have paymentInLines.", getId()));
		}

		return null;
	}

	/**
	 * Fails payment but makes invoice and enrolment sucess.
	 * 
	 * @return
	 */
	public void abandonPaymentKeepInvoice() {

		switch (getStatus()) {

		case FAILED:
		case FAILED_CARD_DECLINED:
		case FAILED_NO_PLACES:
			break;
		default:
			setStatus(PaymentStatus.FAILED);
		}
		
		Invoice activeInvoice = findActiveInvoice();
		
		if (activeInvoice != null) {
			activeInvoice.setStatus(InvoiceStatus.SUCCESS);
			for (InvoiceLine il : activeInvoice.getInvoiceLines()) {
				Enrolment enrol = il.getEnrolment();
				if (enrol != null) {
					if (enrol.getStatus() == EnrolmentStatus.PENDING || enrol.getStatus() == EnrolmentStatus.IN_TRANSACTION) {
						enrol.setStatus(EnrolmentStatus.SUCCESS);
					}
				}
			}
		}
	}

	/**
	 * Fails payment, but does not override state if already FAILED. Refreshes
	 * all the statuses of dependent entities to allow user to reuse them.
	 */
	public void failPayment() {

		switch (getStatus()) {
		case FAILED:
		case FAILED_CARD_DECLINED:
		case FAILED_NO_PLACES:
			break;
		default:
			setStatus(PaymentStatus.FAILED);
		}
		
		Invoice activeInvoice = findActiveInvoice();
		
		if (activeInvoice != null) {
			activeInvoice.setStatus(InvoiceStatus.PENDING);
			for (InvoiceLine il : activeInvoice.getInvoiceLines()) {
				Enrolment enrol = il.getEnrolment();
				if (enrol != null && enrol.getStatus() != EnrolmentStatus.SUCCESS) {
					enrol.setStatus(EnrolmentStatus.PENDING);
				}
			}
		}
	}

	/**
	 * Puts all objects related to current payment in transaction state.
	 */
	public void putInCardDetailsRequiredState() {
		setStatus(PaymentStatus.CARD_DETAILS_REQUIRED);
		for (PaymentInLine pl : getPaymentInLines()) {
			Invoice invoice = pl.getInvoice();
			invoice.setStatus(InvoiceStatus.IN_TRANSACTION);
			for (InvoiceLine il : invoice.getInvoiceLines()) {
				Enrolment enrol = il.getEnrolment();
				if (enrol != null) {
					enrol.setStatus(EnrolmentStatus.IN_TRANSACTION);
				}
			}
		}
	}

	/**
	 * Gets currently active transaction of payment in object.
	 * 
	 * @return payment transaction
	 */
	public PaymentTransaction getActiveTransaction() {
		List<PaymentTransaction> transactions = getPaymentTransactions();
		Expression finalisedExpr = ExpressionFactory.matchExp(PaymentTransaction.IS_FINALISED_PROPERTY, false);
		finalisedExpr = finalisedExpr.orExp(ExpressionFactory.matchExp(PaymentTransaction.IS_FINALISED_PROPERTY, null));
		List<PaymentTransaction> activeTransactions = finalisedExpr.filterObjects(transactions);
		return (activeTransactions.isEmpty()) ? null : activeTransactions.get(0);
	}

	/**
	 * Makes shallow copy of current paymentIn object.
	 * 
	 * @return
	 */
	public PaymentIn makeShallowCopy() {

		PaymentIn paymentIn = getObjectContext().newObject(PaymentIn.class);
		paymentIn.setAmount(getAmount());

		paymentIn.setCollege(getCollege());
		paymentIn.setContact(getContact());

		Date today = new Date();
		paymentIn.setCreated(today);
		paymentIn.setModified(today);

		paymentIn.setSource(getSource());
		paymentIn.setStudent(getStudent());

		return paymentIn;
	}

	/**
	 * Makes a copy of current paymentIn object.
	 * 
	 * @return payment in.
	 */
	public PaymentIn makeCopy() {

		PaymentIn paymentIn = makeShallowCopy();
		paymentIn.setSessionId(getSessionId());

		for (PaymentInLine line : getPaymentInLines()) {
			PaymentInLine pl = line.makeCopy();
			pl.setPaymentIn(paymentIn);
			pl.setInvoice(line.getInvoice());
		}

		return paymentIn;
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
		if (contact != null) {
			Student student = contact.getStudent();
			if (student != null) {
				setStudent(student);
			}
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

	@Override
	protected void onPostAdd() {
		if (getStatus() == null) {
			setStatus(PaymentStatus.NEW);
		}

		if (getType() == null) {
			setType(PaymentType.CREDIT_CARD);
		}
	}

	@Override
	protected void onPreUpdate() {
		setModified(new Date());

		if (getStatus() != PaymentStatus.IN_TRANSACTION && getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED) {
			String cardNumber = CreditCardUtil.obfuscateCCNumber(getCreditCardNumber());
			String cvv = CreditCardUtil.obfuscateCVVNumber(getCreditCardCVV());
			setCreditCardNumber(cardNumber);
			setCreditCardCVV(cvv);
		}
	}

	@Override
	protected void onPrePersist() {
		onPostAdd();
		Date today = new Date();
		setCreated(today);
		setModified(today);
	}

	@Override
	public void setStatus(PaymentStatus status) {
		super.setStatus(status);
		Date now = new Date();
		for (PaymentInLine line : getPaymentInLines()) {
			line.setModified(now);
		}
	}

}
