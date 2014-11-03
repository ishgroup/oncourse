package ish.oncourse.model;

import ish.common.types.*;
import ish.common.util.ExternalValidation;
import ish.math.Money;
import ish.oncourse.model.auto._PaymentIn;
import ish.oncourse.utils.PaymentInUtil;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.CreditCardUtil;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

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

		//#18653 we may pass only contra payments with negative amount
		if (amount.isLessThan(Money.ZERO) && !PaymentType.CONTRA.equals(getType())) {
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
	 * success statuses to the related invoice and enrolment ( {@link EnrolmentStatus#SUCCESS} ).
	 *
	 * Invoked when the payment gateway processing is succeed.
	 *
	 */
	public void succeed() {
		setStatus(PaymentStatus.SUCCESS);

		// succeed all related voucher payments
		for (PaymentIn voucherPayment : PaymentInUtil.getRelatedVoucherPayments(this)) {
			if (!PaymentStatus.STATUSES_FINAL.contains(voucherPayment.getStatus())) {
				voucherPayment.setStatus(PaymentStatus.SUCCESS);

                //we need the code to be sure that all entities which a related to
                // the voucher payment are added to the replication.
                List<VoucherPaymentIn> voucherPaymentIns = voucherPayment.getVoucherPaymentIns();
                for (VoucherPaymentIn voucherPaymentIn : voucherPaymentIns) {
                    voucherPaymentIn.setModified(new Date());
                }
                voucherPayment.getVoucher().setModified(new Date());
            }
		}

		Invoice activeInvoice = findActiveInvoice();
		Date today = new Date();

		if (activeInvoice != null) {
			activeInvoice.setModified(today);
			PaymentInUtil.makeSuccess(activeInvoice.getInvoiceLines());
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

    /**
     * The method creates one REVERSE payment and two payment lines
     * to relate direct and reverse invoice and to balance amount owing.
     * One of payment lines is linked to direct invoice and has the same amount as direct invoice,
     * other one is linked to reverse invoice and has negative amount.
     */
    private Collection<PaymentIn> createRefundInvoice(Invoice invoiceToRefund) {
		invoiceToRefund.updateAmountOwing();

		Set<PaymentIn> refundPayments = new HashSet<>();
        List<PaymentInLine> paymentInLinesToRefund = new ArrayList<>(invoiceToRefund.getPaymentInLines());

		//if the owing already balanced, no reason to create any refund invoice
		if (!Money.isZeroOrEmpty(invoiceToRefund.getAmountOwing()) &&
                paymentInLinesToRefund.size() > 0) {
			// Creating refund invoice
			Invoice refundInvoice = invoiceToRefund.createRefundInvoice();
			LOG.info(String.format("Created refund invoice with amount:%s for invoice:%s.", refundInvoice.getAmountOwing(),
					invoiceToRefund.getId()));

            PaymentIn internalPayment = this.makeShallowCopy();
            internalPayment.setAmount(Money.ZERO);
            internalPayment.setType(PaymentType.REVERSE);
            internalPayment.setStatus(PaymentStatus.SUCCESS);

            String sessionId = this.getSessionId();
            if (StringUtils.trimToNull(sessionId) != null) {
                internalPayment.setSessionId(sessionId);
            }

            PaymentInLine refundPL = internalPayment.getObjectContext().newObject(PaymentInLine.class);
            refundPL.setAmount(Money.ZERO.subtract(invoiceToRefund.getTotalGst()));
            refundPL.setCollege(this.getCollege());
            refundPL.setInvoice(refundInvoice);
            refundPL.setPaymentIn(internalPayment);

            PaymentInLine paymentInLineToRefundCopy = internalPayment.getObjectContext().newObject(PaymentInLine.class);
            paymentInLineToRefundCopy.setAmount(invoiceToRefund.getTotalGst());
            paymentInLineToRefundCopy.setCollege(this.getCollege());
            paymentInLineToRefundCopy.setInvoice(invoiceToRefund);
            paymentInLineToRefundCopy.setPaymentIn(internalPayment);

            invoiceToRefund.setModified(getModified());
            refundPayments.add(internalPayment);
		}

        for (PaymentInLine paymentInLine : paymentInLinesToRefund) {
            refundPayments.add(paymentInLine.getPaymentIn());
        }

		// Fail enrollments on invoiceToRefund
		PaymentInUtil.makeFail(invoiceToRefund.getInvoiceLines());

		return refundPayments;
	}

	/**
	 * Fails payment, but does not override state if already FAILED.Sets the
	 * status of payment to {@link PaymentStatus#FAILED}, and sets the failed
	 * statuses to the related invoice and enrolment ( {@link EnrolmentStatus#FAILED} ).
	 * Creates the refund invoice.
	 */
	public Collection<PaymentIn> abandonPayment() {

		switch (getStatus()) {
		case FAILED:
		case FAILED_CARD_DECLINED:
		case FAILED_NO_PLACES:
			break;
		default:
			setStatus(PaymentStatus.FAILED);
		}

		for (PaymentIn voucherPayment : PaymentInUtil.getRelatedVoucherPayments(this)) {
			if (!PaymentStatus.STATUSES_FINAL.contains(voucherPayment.getStatus())) {
				PaymentInUtil.reverseVoucherPayment(voucherPayment);
			}
		}

		Date today = new Date();
		setModified(today);

		if (!getPaymentInLines().isEmpty()) {

			// Pick up the newest invoice for refund.
			Invoice invoiceToRefund = null;

			for (PaymentInLine line : getPaymentInLines()) {
				Invoice invoice = line.getInvoice();
				invoice.updateAmountOwing();
				if (invoiceToRefund == null) {
					invoiceToRefund = invoice;
				} else {
					// For angel payments use invoiceNumber to determine the
					// last
					// invoice, since createdDate is very often the same
					// across several invoices
					if (getSource() == PaymentSource.SOURCE_ONCOURSE) {
						if (invoice.getInvoiceNumber() > invoiceToRefund.getInvoiceNumber()) {
							invoiceToRefund = invoice;
						}
					} else {
						// For willow payments, use willowId to determine
						// the newest invoice.
						if (invoice.getId() > invoiceToRefund.getId()) {
							invoiceToRefund = invoice;
						}
					}
				}
			}
			if (invoiceToRefund != null) {
				return createRefundInvoice(invoiceToRefund);
			} else {
				LOG.error(String.format("Can not find invoice to refund on paymentIn:%s.", getId()));
			}
		} else {
			LOG.error(String.format("Can not abandon paymentIn:%s, since it doesn't have paymentInLines.", getId()));
		}
		return Collections.emptyList();
	}

	/**
	 * Fails payment but makes invoice and enrolment sucess.
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

		for (PaymentIn voucherPayment : PaymentInUtil.getRelatedVoucherPayments(this)) {
			if (!PaymentStatus.STATUSES_FINAL.contains(voucherPayment.getStatus())) {
				PaymentInUtil.reverseVoucherPayment(voucherPayment);
			}
		}

		Invoice activeInvoice = findActiveInvoice();
		Date today = new Date();

		if (activeInvoice != null) {
			activeInvoice.setModified(today);
			PaymentInUtil.makeSuccess(activeInvoice.getInvoiceLines());
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
		if (getCreated() == null) {
			setCreated(new Date());
		}
		if (getModified() == null) {
			setModified(getCreated());
		}
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
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

	/**
	 * Returns voucher linked to the payment of {@link PaymentType#VOUCHER} type. Returns null for
	 * payments of other types.
	 */
	public Voucher getVoucher() {
		if (PaymentType.VOUCHER.equals(getType()) && !getVoucherPaymentIns().isEmpty()) {
			return getVoucherPaymentIns().get(0).getVoucher();
		}

		return null;
	}
}
