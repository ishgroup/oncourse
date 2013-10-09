package ish.oncourse.util;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.Membership;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherPaymentIn;

/**
 * Helper which should be used when abandon actions required.
 * This helper also detect what type of abandon should be used (abandon keep invoice or abandon reverse invoice)
 * @author vdavidovich
 *
 */
public class PaymentInAbandonHelper {
	private static final Logger logger = Logger.getLogger(PaymentInAbandonHelper.class);
	
	private PaymentIn paymentIn;
	
	private boolean shouldKeepInvoice;
	
	private PaymentIn reversePaymentIn;

	/**
	 * Constructor which check that paymentIn object set.
	 * @param paymentIn - entity which we want to abandon
	 * @param shouldKeepInvoice - proffered abandon action flag. If false abandon action will be requested if available, 
	 * if true - abandon keep invoice will be requested.
	 */
	public PaymentInAbandonHelper(PaymentIn paymentIn, boolean shouldKeepInvoice) {
		if (paymentIn == null) {
			String message = "PaymentIn object for helper should not be empty!";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		this.paymentIn = paymentIn;
		this.shouldKeepInvoice = shouldKeepInvoice;
	}
	
	/**
	 * Validate can we abandon this payment.
	 * This action should be called before {@link PaymentInAbandonHelper#makeAbandon()} call.
	 * @return true if we can, false if current payment not able for any abandon actions.
	 */
	public boolean validatePaymentInForAbandon() {
		if (PaymentStatus.SUCCESS.equals(paymentIn.getStatus())) {
			logger.error(String.format("We can't abandon success payment with id = %s !", paymentIn.getId()));
			return false;
		}
		if (!PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus())) {
			logger.error(String.format("We can't abandon payment with id = %s in status = %s !", paymentIn.getId(), 
				paymentIn.getStatus()));
			return false;
		}
		if (paymentIn.getPaymentInLines().isEmpty()) {
			logger.error(String.format("We can't abandon payment with id = %s because no linked paymentInLines exist!", paymentIn.getId()));
			return false;
		}
		boolean haveAnyInvoiceWithAmountOwing = false;
		for (PaymentInLine paymentInLine : paymentIn.getPaymentInLines()) {
			Invoice invoice = paymentInLine.getInvoice();
			invoice.updateAmountOwing();
			if (!(invoice.getAmountOwing()).isZero()) {
				haveAnyInvoiceWithAmountOwing = true;
				break;
			} else {
				if (canMakeRevertInvoice()) {
					haveAnyInvoiceWithAmountOwing = true;
					break;
				}
			}
		}
		if (!haveAnyInvoiceWithAmountOwing) {
			logger.error(String.format("No invoices with not zero owing for payment with id = %s . Nothing to abandon!", paymentIn.getId()));
			return false;
		}
		return true;
	}
	
	/**
	 * Check is any in transaction enrollments linked with current payment.This check shows us can we abandon this payment or not.
	 * @return true if current payment have linked enrollments with in transaction status.
	 */
	boolean containEnrollmentsInTransactionStatus() {
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, paymentIn.getId());
		Expression enrollmentExpression = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.INVOICE_PROPERTY + "." + 
			Invoice.INVOICE_LINES_PROPERTY + "." + InvoiceLine.ENROLMENT_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.IN_TRANSACTION);
		SelectQuery checkQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(enrollmentExpression));
		@SuppressWarnings("unchecked")
		List<PaymentIn> result = paymentIn.getObjectContext().performQuery(checkQuery);
		return !result.isEmpty();
	}
	
	/**
	 * Check is any new productitems (currently only vouchers) linked with current payment.This check shows us can we abandon this payment or not.
	 * @return true if current payment have linked product items in corresponding statuses.
	 */
	boolean containNewProductItems() {
		Expression paymentProductItemsExpression = ExpressionFactory.matchDbExp(ProductItem.INVOICE_LINE_PROPERTY + "." + InvoiceLine.INVOICE_PROPERTY 
			+ "." + Invoice.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.PAYMENT_IN_PROPERTY + "." + PaymentIn.ID_PK_COLUMN, paymentIn.getId());
		SelectQuery checkQuery = new SelectQuery(ProductItem.class, paymentProductItemsExpression);
		@SuppressWarnings("unchecked")
		List<ProductItem> productItems = paymentIn.getObjectContext().performQuery(checkQuery);
		for (ProductItem productItem : productItems) {
			if (productItem instanceof Voucher && ProductStatus.NEW.equals(((Voucher) productItem).getStatus())) {
				return true;
			}
			if (productItem instanceof Membership) {
				//TODO: we also need to check memberships when they receive the status
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	boolean containInvoicesWithoutEnrolOrProductLinks() {
		Expression invoicesWithoutEnrollmentRelation = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + 
			PaymentInLine.INVOICE_PROPERTY + "." + Invoice.INVOICE_LINES_PROPERTY + "." + InvoiceLine.ENROLMENT_PROPERTY, null);
		Expression invoicesWithoutProductRelation = ExpressionFactory.matchDbExp(ProductItem.INVOICE_LINE_PROPERTY + "." + InvoiceLine.INVOICE_PROPERTY 
			+ "." + Invoice.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.PAYMENT_IN_PROPERTY + "." + PaymentIn.ID_PK_COLUMN, paymentIn.getId());
		Expression notZeroOwingInvoices = ExpressionFactory.noMatchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + 
			PaymentInLine.INVOICE_PROPERTY + "." + Invoice.AMOUNT_OWING_PROPERTY, Money.ZERO.toBigDecimal());
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, paymentIn.getId());
		SelectQuery checkEnrollmentsQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(notZeroOwingInvoices)
			.andExp(invoicesWithoutEnrollmentRelation));
		SelectQuery checkProductsQuery = new SelectQuery(ProductItem.class, invoicesWithoutProductRelation);
			
		List<PaymentIn> enrollmentsResult = paymentIn.getObjectContext().performQuery(checkEnrollmentsQuery);
		List<ProductItem> productsResult =  paymentIn.getObjectContext().performQuery(checkProductsQuery);
		return !enrollmentsResult.isEmpty() && productsResult.isEmpty();
	}
	
	/**
	 * Check is this payment have linked invoices with linked entities and we need to apply abandon payment action.
	 * @return false if should apply keep invoice, true if abandon should be used.
	 */
	boolean canMakeRevertInvoice() {
		return containEnrollmentsInTransactionStatus() || containNewProductItems() || containInvoicesWithoutEnrolOrProductLinks();
	}
	
	/**
	 * Process abandon action depends to passed shouldKeepInvoice param and valid for current payment object action.
	 * This mean that we can't process abandon action for payment which previously was abandoned with keep invoice property 
	 * and payments created with angel's PaymentIn window (this mean that if invoicelines haven't links to enrollments or productitems) 
	 * @return reverse paymentIn entity in the same context as the original after abandon payment and null if abandon payment keep invoice requested. 
	 */
	public PaymentIn makeAbandon() {
		try {
			logger.info(String.format("Abandon paymentIn with id: %s, created: %s and status: %s.", paymentIn.getId(), paymentIn.getCreated(),
				paymentIn.getStatus()));
			boolean shouldAbandon = !shouldKeepInvoice && canMakeRevertInvoice();
			if (shouldAbandon) {
				//if all enrollments in transaction we can just fail them
				reversePaymentIn = abandonPayment();
			} else {
				//we should not fail enrollments when college allow them to enroll with owing.
				reversePaymentIn = abandonPaymentKeepInvoice();
			}
			return reversePaymentIn;
		} catch (final CayenneRuntimeException ce) {
			logger.error(String.format("Unable to cancel payment with id: %s and status: %s.", paymentIn.getId(), paymentIn.getStatus()), ce);
			paymentIn.getObjectContext().rollbackChanges();
		}
		return null;
	}
	
	/**
	 * Fails payment, but does not override state if already FAILED.Sets the
	 * status of payment to {@link PaymentStatus#FAILED}, and sets the failed
	 * statuses to the related invoice ( {@link InvoiceStatus#FAILED} ) and
	 * enrollment ( {@link EnrolmentStatus#FAILED} ). Creates the refund invoice.
	 */
	PaymentIn abandonPayment() {
		paymentIn.setStatus(PaymentStatus.FAILED);
		Date today = new Date();
		paymentIn.setModified(today);
		
		//if this is a payment linked with vouchers we also should revert the vouchers
		if (PaymentType.VOUCHER.equals(paymentIn.getType())) {
			revertTheVoucherRedemption();
		}
		
		if (!paymentIn.getPaymentInLines().isEmpty()) {
			// Pick up the newest invoice for refund.
			PaymentInLine paymentInLineToRefund = null;
			Invoice invoiceToRefund = null;
			for (PaymentInLine line : paymentIn.getPaymentInLines()) {
				Invoice invoice = line.getInvoice();
				invoice.updateAmountOwing();
				if (invoiceToRefund == null) {
					paymentInLineToRefund = line;
					invoiceToRefund = invoice;
				} else {
					// For angel payments use invoiceNumber to determine the last invoice, since createdDate is very often the same
					// across several invoices
					if (paymentIn.getSource() == PaymentSource.SOURCE_ONCOURSE) {
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
				PaymentIn internalPayment = createRefundPaymentIn(paymentInLineToRefund, today);
				return internalPayment;
			} else {
				logger.error(String.format("Can not find invoice to refund on paymentIn:%s.", paymentIn.getId()));
			}
		} else {
			logger.error(String.format("Can not abandon paymentIn:%s, since it doesn't have paymentInLines.", paymentIn.getId()));
		}
		return null;
	}
	
	/**
	 * Fails payment but makes invoice and enrollment success.
	 * @return null because no reverse payments created by this action
	 */
	PaymentIn abandonPaymentKeepInvoice() {
		paymentIn.setStatus(PaymentStatus.FAILED);
		Invoice activeInvoice = findActiveInvoice();
		Date today = new Date();
		
		//if this is a payment linked with vouchers we also should revert the vouchers
		if (PaymentType.VOUCHER.equals(paymentIn.getType())) {
			revertTheVoucherRedemption();
		}
		
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
				List<Voucher> vouchers = il.getVouchers();
				for (Voucher voucher : vouchers) {
					//we should change new vouchers to active in this case
					voucher.setModified(today);
					if (ProductStatus.NEW.equals(voucher.getStatus())) {
						voucher.setStatus(ProductStatus.ACTIVE);
					}
				}
				//TODO: we should also add the memberships changes here when they receive the status
			}
		}
		return null;
	}
	
	/**
	 * Revert the voucher redemption attempts.
	 * Money vouchers and course vouchers linked with this paymentIn will be reverted to the state before the redemption attempt.
	 */
	void revertTheVoucherRedemption() {
		for (VoucherPaymentIn voucherPaymentIn : paymentIn.getVoucherPaymentIns()) {
			Voucher voucher = voucherPaymentIn.getVoucher();
			if (voucher.getVoucherProduct().getMaxCoursesRedemption() == null || 0 == voucher.getVoucherProduct().getMaxCoursesRedemption()) {
				voucher.setRedemptionValue(voucher.getRedemptionValue().add(paymentIn.getAmount()));
			} else {
				voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() - voucherPaymentIn.getEnrolmentsCount());
			}
			if (!voucher.isFullyRedeemed()) {
				voucher.setStatus(ProductStatus.ACTIVE);
			}
		}
	}
	
	/**
	 * Finds invoice which is current for selected paymentIn and is performed operation on.
	 * 
	 * @return active invoice
	 */
	Invoice findActiveInvoice() {
		SortedSet<Invoice> invoices = new TreeSet<>(new Comparator<Invoice>() {
			public int compare(Invoice o1, Invoice o2) {
				return (paymentIn.getSource() == PaymentSource.SOURCE_ONCOURSE) ? o2.getInvoiceNumber().compareTo(o1.getInvoiceNumber()) : o2.getId()
					.compareTo(o1.getId());
			}
		});

		for (PaymentInLine line : paymentIn.getPaymentInLines()) {
			Invoice invoice = line.getInvoice();
			invoices.add(invoice);
		}
		Invoice activeInvoice = invoices.first();
		return activeInvoice;
	}
	
	/**
	 * Create refund paymentIn and refund invoice to balance the amount owing for invoice to 0.
	 * @param paymentInLineToRefund - paymentInLine object which is a relation between paymentIn and invoice objects which need to be balanced.
	 * @param modifiedTime - date of modification which need to be set for this action.
	 * @return reverse paymentIn entity in the same context as the original after.
	 */
	public static PaymentIn createRefundPaymentIn(PaymentInLine paymentInLineToRefund, Date modifiedTime) {
		// Creating internal payment, with zero amount which will be
		// linked to invoiceToRefund, and refundInvoice.
		Invoice invoiceToRefund = paymentInLineToRefund.getInvoice();
		invoiceToRefund.updateAmountOwing();
		PaymentIn reversePayment;
		//if the owing already balanced, no reason to create any refund invoice
		if (!Money.isZeroOrEmpty(invoiceToRefund.getAmountOwing())){
			reversePayment = paymentInLineToRefund.getPaymentIn().makeShallowCopy();
			reversePayment.setAmount(Money.ZERO);
			reversePayment.setType(PaymentType.REVERSE);
			reversePayment.setStatus(PaymentStatus.SUCCESS);
			String sessionId = paymentInLineToRefund.getPaymentIn().getSessionId();
			if (StringUtils.trimToNull(sessionId) != null) {
				reversePayment.setSessionId(sessionId);
			}

			// Creating refund invoice
			Invoice refundInvoice = invoiceToRefund.createRefundInvoice();
			logger.info(String.format("Created refund invoice with amount:%s for invoice:%s.", refundInvoice.getAmountOwing(),
				invoiceToRefund.getId()));

			PaymentInLine refundPL = paymentInLineToRefund.getObjectContext().newObject(PaymentInLine.class);
			refundPL.setAmount(Money.ZERO.subtract(paymentInLineToRefund.getAmount()));
			refundPL.setCollege(paymentInLineToRefund.getCollege());
			refundPL.setInvoice(refundInvoice);
			refundPL.setPaymentIn(reversePayment);

			PaymentInLine paymentInLineToRefundCopy = paymentInLineToRefund.makeCopy();
			paymentInLineToRefundCopy.setPaymentIn(reversePayment);
		} else {
			reversePayment = paymentInLineToRefund.getPaymentIn();
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
			List<Voucher> vouchers = il.getVouchers();
			for (Voucher voucher : vouchers) {
				//we should change new vouchers to canceled in this case
				voucher.setModified(modifiedTime);
				if (ProductStatus.NEW.equals(voucher.getStatus())) {
					voucher.setStatus(ProductStatus.CANCELLED);
				}
			}
			//TODO: we should also add the memberships changes here when they receive the status
		}
		return reversePayment;
	}
	
}
