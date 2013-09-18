package ish.oncourse.model;

import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.common.types.VoucherPaymentStatus;
import ish.common.util.ExternalValidation;
import ish.math.Money;
import ish.oncourse.model.auto._PaymentIn;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.CreditCardUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PaymentIn extends _PaymentIn implements Queueable {

	private static final long serialVersionUID = -2372029086420124878L;

	/**
	 * Logger
	 */
	private static final Logger LOG = Logger.getLogger(PaymentIn.class);

	/**
	 * Payment processed session attribute.
	 */
	public static final String PAYMENT_PROCESSED_PARAM = "payment_processed";

	/**
	 * Failed payment session attribute.
	 */
	public static final String FAILED_PAYMENT_PARAM = "failedPayment";

	/**
	 * Payment expire interval in minutes
	 */
	public static final int EXPIRE_INTERVAL = 20;

	// In order not to query the whole paymentIn
	// table we limit time window to 3 month
	public static final int EXPIRE_TIME_WINDOW = 3;

	/**
	 * Returns the primary key property - id of {@link PaymentIn}.
	 * 
	 * @return
	 */
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * Validation to prevent saving unbalanced PaymentIn into database.
	 */
	@Override
	public void validateForSave(ValidationResult result) {

		super.validateForSave(result);

		// Amount - mandatory field
		if (getAmount() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, _PaymentIn.AMOUNT_PROPERTY, "The payment amount cannot be empty."));
			return;
		}

		Money amount = getAmount();

		if (amount.compareTo(Money.ZERO) < 0) {
			result.addFailure(ValidationFailure.validationFailure(this, _PaymentIn.AMOUNT_PROPERTY,
					"The payment-in must have non negative amount."));
			return;
		}

		Money sum = Money.ZERO;
		List<PaymentInLine> list = getPaymentInLines();

		if (list != null) {
			for (PaymentInLine pinl : list) {
				sum = sum.add(pinl.getAmount());
			}
		}

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
		Money amount = getAmount();
		boolean isValid = amount != null && amount.compareTo(Money.ZERO) != -1;
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
		Date today = new Date();

		if (activeInvoice != null) {
			activeInvoice.setModified(today);
			for (InvoiceLine il : activeInvoice.getInvoiceLines()) {
				il.setModified(today);
				for (InvoiceLineDiscount ilDiscount : il.getInvoiceLineDiscounts()) {
					ilDiscount.setModified(today);
				}
				Enrolment enrol = il.getEnrolment();
				if (enrol != null) {
					enrol.setModified(today);
					if (!enrol.isInFinalStatus()) {
						enrol.setStatus(EnrolmentStatus.SUCCESS);
					}
				}
			}
		}
	}
		
	/**
	 * Finds invoice which is current and is performed operation on.
	 * 
	 * @return active invoice
	 */
	private Invoice findActiveInvoice() {

		SortedSet<Invoice> invoices = new TreeSet<>(new Comparator<Invoice>() {
			public int compare(Invoice o1, Invoice o2) {
				return (getSource() == PaymentSource.SOURCE_ONCOURSE) ? o2.getInvoiceNumber().compareTo(o1.getInvoiceNumber()) : o2.getId()
						.compareTo(o1.getId());
			}
		});

		for (PaymentInLine line : getPaymentInLines()) {
			Invoice invoice = line.getInvoice();
			invoices.add(invoice);
		}

		Invoice activeInvoice = invoices.first();
		return activeInvoice;
	}
	
	public static PaymentIn createRefundInvoice(PaymentInLine paymentInLineToRefund, Date modifiedTime) {
		// Creating internal payment, with zero amount which will be
		// linked to invoiceToRefund, and refundInvoice.
		Invoice invoiceToRefund = paymentInLineToRefund.getInvoice();
		invoiceToRefund.updateAmountOwing();
		PaymentIn internalPayment = null;
		//if the owing already balanced, no reason to create any refund invoice
		if (!Money.isZeroOrEmpty(invoiceToRefund.getAmountOwing())) {
			internalPayment = paymentInLineToRefund.getPaymentIn().makeShallowCopy();
			internalPayment.setAmount(Money.ZERO);
			internalPayment.setType(PaymentType.INTERNAL);
			internalPayment.setStatus(PaymentStatus.SUCCESS);
			String sessionId = paymentInLineToRefund.getPaymentIn().getSessionId();
			if (StringUtils.trimToNull(sessionId) != null) {
				internalPayment.setSessionId(sessionId);
			}
			
			// Creating refund invoice
			Invoice refundInvoice = invoiceToRefund.createRefundInvoice();
			LOG.info(String.format("Created refund invoice with amount:%s for invoice:%s.", refundInvoice.getAmountOwing(),
				invoiceToRefund.getId()));

			PaymentInLine refundPL = paymentInLineToRefund.getObjectContext().newObject(PaymentInLine.class);
			refundPL.setAmount(Money.ZERO.subtract(paymentInLineToRefund.getAmount()));
			refundPL.setCollege(paymentInLineToRefund.getCollege());
			refundPL.setInvoice(refundInvoice);
			refundPL.setPaymentIn(internalPayment);

			PaymentInLine paymentInLineToRefundCopy = paymentInLineToRefund.makeCopy();
			paymentInLineToRefundCopy.setPaymentIn(internalPayment);
		} else {
			internalPayment = paymentInLineToRefund.getPaymentIn();
		}
		invoiceToRefund.setModified(modifiedTime);
		paymentInLineToRefund.setModified(modifiedTime);
		
		// Fail enrollments on invoiceToRefund
		for (InvoiceLine il : invoiceToRefund.getInvoiceLines()) {
			il.setModified(modifiedTime);
			for (InvoiceLineDiscount ilDiscount : il.getInvoiceLineDiscounts()) {
				ilDiscount.setModified(modifiedTime);
			}
			Enrolment enrol = il.getEnrolment();
			if (enrol != null) {
				enrol.setModified(modifiedTime);
				boolean shouldFailEnrolment = enrol.getStatus() == null || enrol.getStatus() == EnrolmentStatus.IN_TRANSACTION;
				if (shouldFailEnrolment) {
					enrol.setStatus(EnrolmentStatus.FAILED);
				}
			}
		}
		
		return internalPayment;
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

		Date today = new Date();
		setModified(today);

		if (!getPaymentInLines().isEmpty()) {

			// Pick up the newest invoice for refund.
			PaymentInLine paymentInLineToRefund = null;
			Invoice invoiceToRefund = null;

			for (PaymentInLine line : getPaymentInLines()) {
				Invoice invoice = line.getInvoice();
				invoice.updateAmountOwing();
				if (invoiceToRefund == null) {
					paymentInLineToRefund = line;
					invoiceToRefund = invoice;
				} else {
					// For angel payments use invoiceNumber to determine the
					// last
					// invoice, since createdDate is very often the same
					// across several invoices
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
				PaymentIn internalPayment = createRefundInvoice(paymentInLineToRefund, today);
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
	 * Deprecated functionality
	 * We should use PaymentInAbandonHelper#makeAbandon() instead of abandon.
	 */
	@Deprecated
	private void revertTheVoucherRedemption() {
		//also check that vouchers linked with the payment to avoid the state when vouchers will be partially used with abandoned payment.
		final List<VoucherPaymentIn> objectsForDelete = new ArrayList<>();
		for (VoucherPaymentIn voucherPaymentIn : getVoucherPaymentIns()) {
			if (!PaymentType.VOUCHER.equals(getType())) {
				LOG.error(String.format("Not voucher paymentIn with id %s have linked vouchers!", getId()));
			}
			if (VoucherPaymentStatus.APPROVED.equals(voucherPaymentIn.getStatus())) {
				LOG.debug(String.format("We request abandon of paymentIn with id %s which contain the VoucherPaymentIn with id %s in %s status!", 
					getId(), voucherPaymentIn.getId(), voucherPaymentIn.getStatus()));
			}
			Voucher voucher = voucherPaymentIn.getVoucher();
			if (voucher.getVoucherProduct().getMaxCoursesRedemption() == null || 0 == voucher.getVoucherProduct().getMaxCoursesRedemption()) {
				voucher.setRedemptionValue(voucher.getRedemptionValue().add(this.getAmount()));
			} else {
				voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() - voucherPaymentIn.getEnrolmentsCount());
			}
			if (!voucher.isFullyRedeemed()) {
				voucher.setStatus(ProductStatus.ACTIVE);
			}
			objectsForDelete.add(voucherPaymentIn);
		}
		if (!objectsForDelete.isEmpty()) {
			//delete the relation after we successfully revert the voucher 
			getObjectContext().deleteObjects(objectsForDelete);
		}
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
		Date today = new Date();

		if (activeInvoice != null) {
			activeInvoice.setModified(today);
			for (InvoiceLine il : activeInvoice.getInvoiceLines()) {
				il.setModified(today);
				for (InvoiceLineDiscount ilDiscount : il.getInvoiceLineDiscounts()) {
					ilDiscount.setModified(today);
				}
				Enrolment enrol = il.getEnrolment();
				if (enrol != null) {
					enrol.setModified(today);
					if (enrol.getStatus() == EnrolmentStatus.IN_TRANSACTION) {
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
		Date today = new Date();

		if (activeInvoice != null) {
			activeInvoice.setModified(today);
			for (InvoiceLine il : activeInvoice.getInvoiceLines()) {
				il.setModified(today);
				for (InvoiceLineDiscount ilDiscount : il.getInvoiceLineDiscounts()) {
					ilDiscount.setModified(today);
				}
				Enrolment enrol = il.getEnrolment();
				if (enrol != null) {
					enrol.setModified(today);
					if (!enrol.isInFinalStatus()) {
						enrol.setStatus(EnrolmentStatus.IN_TRANSACTION);
					}
				}
			}
		}
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

        //source should be the same as in the original #13955
		paymentIn.setSource(this.getSource());
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
	 * Retrieves the "client" identificator, ie with the "W" if no value passed.
	 * 
	 * @return client identificator string.
	 */
	public String getClientReference() {
		PaymentSource source = getSource();
		if (source == null) {
			source = PaymentSource.SOURCE_WEB;// If no source set we pass Web
		}
		return source.getDatabaseValue() + getId();
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
		for (final PaymentInLine paymentInLine : getPaymentInLines()) {
			paymentInLine.setModified(new Date());
		}

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
        if (getCreated() == null)
		    setCreated(today);
		setModified(today);
		for (final PaymentInLine paymentInLine : getPaymentInLines()) {
			paymentInLine.setModified(today);
		}
	}
	
	@Override
	public void setStatus(final PaymentStatus status) {
		if (getStatus() == null) {
			//nothing to check
		} else {
			switch (getStatus()) {
			case NEW:
				if (status == null) {
					throw new IllegalArgumentException(String.format("Can't set the empty paymentin status for payment with id = %s !", getId()));
				}
				break;
			case QUEUED:
				if (status == null || PaymentStatus.NEW.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentin with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			case IN_TRANSACTION:
			case CARD_DETAILS_REQUIRED:
				if (status == null || PaymentStatus.NEW.equals(status) || PaymentStatus.QUEUED.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentin with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			case SUCCESS:
				if (!PaymentStatus.SUCCESS.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentin with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			case FAILED:
			case FAILED_CARD_DECLINED:
			case FAILED_NO_PLACES:
				//TODO: we should be able to set the in transaction status here
				//break;
				if (!(getStatus().equals(status))) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentin with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			default:
				throw new IllegalArgumentException(String.format("Unsupported status %s found for paymentin with id = %s ", getStatus(), getId()));
			}
		}
		super.setStatus(status);
		//this is an old workaround to prevent replication of PaymentIn entities without linked PaymentInLine entities
		Date now = new Date();
		for (PaymentInLine line : getPaymentInLines()) {
			line.setModified(now);
		}
	}

	public boolean isAsyncReplicationAllowed() {
		return getStatus() != null && getStatus() != PaymentStatus.IN_TRANSACTION && getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED;
	}

	/**
	 * Checks if payment is zero payment.
	 * 
	 * @return
	 */
	public boolean isZeroPayment() {
		return getAmount().isZero();
	}
}
