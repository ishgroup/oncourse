package ish.oncourse.services.voucher;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.VoucherPaymentStatus;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.model.VoucherProduct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

/**
 * Helper which generate the required payment and voucher objects when student try to redeem the voucher for enrolments.
 * @author vdavidovich
 *
 */
public class VoucherRedemptionHelper {
	private static final Expression COURSE_VOUCHER_QUALIFIER = ExpressionFactory.greaterExp(Voucher.PRODUCT_PROPERTY + "." 
		+ VoucherProduct.MAX_COURSES_REDEMPTION_PROPERTY, 0);
	private static final Expression MONEY_VOUCHER_QUALIFIER = COURSE_VOUCHER_QUALIFIER.notExp();
	private static final Expression ACTIVE_VOUCHER_QUALIFIER = ExpressionFactory.matchExp(Voucher.STATUS_PROPERTY, ProductStatus.ACTIVE);
	private List<Voucher> vouchers;
	private Invoice invoice;
	private List<PaymentIn> payments;
	
	/**
	 * Default constructor.
	 */
	public VoucherRedemptionHelper() {
		this.vouchers = new ArrayList<Voucher>();
	}

	/**
	 * Constructor with data prepared for calculation.
	 * @param invoice - invoice for enrolments which will be calculated.
	 * @param vouchers - vouchers which user wants to redeem for enrolments.
	 */
	public VoucherRedemptionHelper(Invoice invoice, List<Voucher> vouchers) {
		this.vouchers = vouchers;
		this.invoice = invoice;
	}
	
	/**
	 * @return the payments which were generated for voucher redemption.
	 */
	public List<PaymentIn> getPayments() {
		if (payments == null) {
			payments = new ArrayList<PaymentIn>();
		}
		return payments;
	}
	
	/**
	 * @return the original invoice used for calculation.
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * @return the vouchers list used for calculation ordered by status, courses and money.
	 */
	public List<Voucher> getVouchers() {
		if (vouchers == null) {
			vouchers = new ArrayList<Voucher>();
		}
		Collections.sort(vouchers, new VoucherComparator());
		return vouchers;
	}
	
	public boolean addVoucher(Voucher voucher) {
		if (voucher != null && !voucher.isInUse() && !vouchers.contains(voucher)) {
			vouchers.add(voucher);
			return true;
		}
		return false;
	}
	
	public boolean removeVoucher(Voucher voucher) {
		return vouchers.remove(voucher);
	}
	
	private VoucherPaymentIn getActiveVoucherPayment(Voucher voucher) {
		for (VoucherPaymentIn voucherPaymentIn : voucher.getVoucherPaymentIns()) {
			PaymentStatus status = voucherPaymentIn.getPayment().getStatus();
			//for the helper execution we need to check the new status is equal, but in final integration also this statuses can be available
			if (PaymentStatus.NEW.equals(status) || PaymentStatus.IN_TRANSACTION.equals(status) || PaymentStatus.FAILED_CARD_DECLINED.equals(status)) {
				return voucherPaymentIn;
			}
		}
		return null;
	}
	
	private VoucherPaymentIn createVoucherPaymentIn(Voucher voucher, Contact payer) {
		ObjectContext context = getInvoice().getObjectContext();
		VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class);
		PaymentIn payment = context.newObject(PaymentIn.class);
		payment.setType(PaymentType.VOUCHER);
		payment.setContact((Contact) context.localObject(payer.getObjectId(), payer));
		payment.setAmount(Money.ZERO);
		//payment.setStatus(PaymentStatus.NEW);
		payment.setCollege(getInvoice().getCollege());
		payment.setSource(getInvoice().getSource());
		voucherPaymentIn.setCollege(getInvoice().getCollege());
		voucherPaymentIn.setPayment(payment);
		voucherPaymentIn.setVoucher((Voucher) context.localObject(voucher.getObjectId(), voucher));
		voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED);
		if (voucherPaymentIn.getEnrolmentsCount() == null && voucher.getVoucherProduct().getMaxCoursesRedemption() != null) {
			voucherPaymentIn.setEnrolmentsCount(0);
		}
		getPayments().add(payment);
		return voucherPaymentIn;
	}
	
	private PaymentInLine paymentLineForInvoiceAndPayment(PaymentIn paymentIn) {
		for (PaymentInLine paymentInLine : paymentIn.getPaymentInLines()) {
			if (isEqualIgnoreContext(paymentInLine.getInvoice(), getInvoice())) {
				return paymentInLine;
			}
		}
		ObjectContext context = getInvoice().getObjectContext();
		PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
		paymentInLine.setCollege(getInvoice().getCollege());
		paymentInLine.setPaymentIn(paymentIn);
		paymentInLine.setInvoice(getInvoice());
		paymentInLine.setAmount(Money.ZERO);
		return paymentInLine;
	}
	
	private boolean isEqualIgnoreContext(CayenneDataObject objectForCheck, CayenneDataObject dataObject) {
		if (objectForCheck instanceof CayenneDataObject && dataObject instanceof CayenneDataObject) {
			return objectForCheck.equals(dataObject) || objectForCheck.getObjectId().equals(dataObject.getObjectId());
		}
		return false;
	}
	
	/**
	 * Discard the changes made from previous VoucherRedemptionHelper#calculateVouchersRedeemPayment() execution.
	 */
	public void discardChanges() {
		for (Voucher voucher : getVouchers()) {
			VoucherPaymentIn voucherPaymentIn = getActiveVoucherPayment(voucher);
			if (voucherPaymentIn != null) {
				PaymentIn paymentIn = voucherPaymentIn.getPayment();
				if (voucher.getVoucherProduct().getMaxCoursesRedemption() == null || 0 == voucher.getVoucherProduct().getMaxCoursesRedemption()) {
					voucher.setRedemptionValue(voucher.getRedemptionValue().add(paymentIn.getAmount()));
				} else {
					voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() - voucherPaymentIn.getEnrolmentsCount());
				}
				if (!voucher.isFullyRedeemed()) {
					voucher.setStatus(ProductStatus.ACTIVE);
				}
				ObjectContext context = paymentIn.getObjectContext();
				context.deleteObject(voucherPaymentIn);
				context.deleteObjects(paymentIn.getPaymentInLines());
				context.deleteObject(paymentIn);
			}
		}
		getPayments().clear();
	}
	
	/**
	 * Calculate the voucher redemption and create the in transaction payments for vouchers which can be applied for.
	 */
	public void calculateVouchersRedeemPayment() {
		discardChanges();
		if (!getVouchers().isEmpty() && !getInvoice().getInvoiceLines().isEmpty()) {
			//check available vouchers for courses first
			List<InvoiceLine> redeemedInvoiceLines = new ArrayList<InvoiceLine>();
			List<Voucher> courseVouchers = ACTIVE_VOUCHER_QUALIFIER.filterObjects(COURSE_VOUCHER_QUALIFIER.filterObjects(getVouchers()));
			for (Voucher courseVoucher : courseVouchers) {
				for (InvoiceLine invoiceLine : getInvoice().getInvoiceLines()) {
					if (!redeemedInvoiceLines.contains(invoiceLine) && invoiceLine.getEnrolment() != null 
						&& courseVoucher.isApplicableTo(invoiceLine.getEnrolment())) {
						redeemVoucherForCourse(courseVoucher, invoiceLine);
						redeemedInvoiceLines.add(invoiceLine);
						if (ProductStatus.REDEEMED.equals(courseVoucher.getStatus())) {
							//if the voucher already redeemed we need to switch to another available voucher
							break;
						}
					}
				}
			}
			//check available vouchers with money
			List<RedeemedInvoiceLine> partiallyRedeemedInvoiceLines = new ArrayList<RedeemedInvoiceLine>();
			//fill amount which need to be redeemed
			for (InvoiceLine invoiceLine : getInvoice().getInvoiceLines()) {
				if (!redeemedInvoiceLines.contains(invoiceLine)) {
					partiallyRedeemedInvoiceLines.add(new RedeemedInvoiceLine(invoiceLine, 
						invoiceLine.getDiscountedPriceTotalIncTax()));
				}
			}
			List<Voucher> moneyVouchers = ACTIVE_VOUCHER_QUALIFIER.filterObjects(MONEY_VOUCHER_QUALIFIER.filterObjects(getVouchers()));
			for (Voucher moneyVoucher : moneyVouchers) {
				for (InvoiceLine invoiceLine : getInvoice().getInvoiceLines()) {
					RedeemedInvoiceLine redeemedInvoiceLine = takeRedeemedInvoiceLine(partiallyRedeemedInvoiceLines, 
						invoiceLine);
					if (redeemedInvoiceLine != null && !redeemedInvoiceLines.contains(invoiceLine) && invoiceLine.getEnrolment() != null) {
						Money amount = redeemVoucherForMoney(moneyVoucher, invoiceLine, redeemedInvoiceLine.getAmount());
						if (amount.equals(redeemedInvoiceLine.getAmount())) {
							redeemedInvoiceLines.add(invoiceLine);
							partiallyRedeemedInvoiceLines.remove(redeemedInvoiceLine);
						} else {
							redeemedInvoiceLine.setAmount(redeemedInvoiceLine.getAmount().subtract(amount));
						}
						if (ProductStatus.REDEEMED.equals(moneyVoucher.getStatus())) {
							//if the voucher already redeemed we need to switch to another available voucher
							break;
						}
					}
				}
			}
		}
	}
		
	private RedeemedInvoiceLine takeRedeemedInvoiceLine(List<RedeemedInvoiceLine> redeemedInvoiceLines, InvoiceLine invoiceLine) {
		if (invoiceLine != null && redeemedInvoiceLines != null && !redeemedInvoiceLines.isEmpty()) {
			for (RedeemedInvoiceLine redeemedInvoiceLine : redeemedInvoiceLines) {
				if (redeemedInvoiceLine.getInvoiceLine().equals(invoiceLine)) {
					return redeemedInvoiceLine;
				}
			}
		}
		return null;
	}
			
	/**
	 * Redeem money voucher for invoiceline if any additional payments exist for them.
	 * @param moneyVoucher - money voucher for use.
	 * @param invoiceLine - invoiceline for payment generation.
	 * @param leftToPay - amount left to pay for invoiceline
	 * @return money allocated from voucher for this invoiceline.
	 */
	Money redeemVoucherForMoney(Voucher moneyVoucher, InvoiceLine invoiceLine, Money leftToPay) {
		//evaluate amount for the payment
		Money amount = moneyVoucher.getRedemptionValue().isLessThan(leftToPay) ? moneyVoucher.getRedemptionValue() : leftToPay;
		VoucherPaymentIn voucherPaymentIn = getActiveVoucherPayment(moneyVoucher);
		if (voucherPaymentIn == null) {
			//no new payment linked to the voucher yet, need to create
			voucherPaymentIn = createVoucherPaymentIn(moneyVoucher, getInvoice().getContact());
		}
		PaymentIn paymentIn = voucherPaymentIn.getPayment();
		paymentIn.setAmount(paymentIn.getAmount().add(amount));
		PaymentInLine paymentInLine = paymentLineForInvoiceAndPayment(paymentIn);
		paymentInLine.setAmount(paymentInLine.getAmount().add(amount));
		moneyVoucher.setRedemptionValue(moneyVoucher.getRedemptionValue().subtract(amount));
		if (moneyVoucher.isFullyRedeemed()) {
			moneyVoucher.setStatus(ProductStatus.REDEEMED);
		}
		return amount;
	}
	
	/**
	 * Redeem course voucher for invoiceline.
	 * @param courseVoucher - course voucher for use
	 * @param invoiceLine - invoiceline for payment generation.
	 */
	void redeemVoucherForCourse(Voucher courseVoucher, InvoiceLine invoiceLine) {
		VoucherPaymentIn voucherPaymentIn = getActiveVoucherPayment(courseVoucher);
		if (voucherPaymentIn == null) {
			//no new payment linked to the voucher yet, need to create
			voucherPaymentIn = createVoucherPaymentIn(courseVoucher, getInvoice().getContact());
		}
		PaymentIn paymentIn = voucherPaymentIn.getPayment();
		paymentIn.setAmount(paymentIn.getAmount().add(invoiceLine.getPriceTotalIncTax()));
		PaymentInLine paymentInLine = paymentLineForInvoiceAndPayment(paymentIn);
		paymentInLine.setAmount(paymentInLine.getAmount().add(invoiceLine.getPriceTotalIncTax()));
		courseVoucher.setRedeemedCoursesCount(courseVoucher.getRedeemedCoursesCount() + 1);
		voucherPaymentIn.setEnrolmentsCount(voucherPaymentIn.getEnrolmentsCount() + 1);
		if (courseVoucher.isFullyRedeemed()) {
			courseVoucher.setStatus(ProductStatus.REDEEMED);
		}
	}
	
	/**
	 * Voucher comparator which order the vouchers by status (active should be the first), redeemed course count and redemption value 
	 * (vouchers with the less redemption value should be redeemed firstly)
	 * @author vdavidovich
	 *
	 */
	public static class VoucherComparator implements Comparator<Voucher> {

		@Override
		public int compare(Voucher voucher1, Voucher voucher2) {
			int statusCompare = voucher1.getStatus().compareTo(voucher2.getStatus());
			if (statusCompare == 0) {
				//first we need to check that course vouchers will be in the top
				if (voucher1.getRedeemedCoursesCount() != null) {
					int courseCountResult = voucher1.getRedeemedCoursesCount().compareTo(voucher2.getRedeemedCoursesCount());
					if (courseCountResult == 0) {
						if (voucher1.getValueRemaining() != null) {
							if (voucher2.getValueRemaining() != null) {
								return voucher1.getValueRemaining().compareTo(voucher2.getValueRemaining());
							} else {
								return 1;
							}
						}
					} else {
						return courseCountResult;
					}
				} else if (voucher1.getValueRemaining() != null) {
					if (voucher2.getValueRemaining() != null) {
						return voucher1.getValueRemaining().compareTo(voucher2.getValueRemaining());
					} else {
						return 1;
					}
				}
			}
			return 0;
		}
		
	}
	/**
	 * Container to store the information about invoicelines with amount owing.
	 * @author vdavidovich
	 *
	 */
	private class RedeemedInvoiceLine {
		private final InvoiceLine invoiceLine;
		private Money amount;
		
		protected RedeemedInvoiceLine(InvoiceLine invoiceLine, Money amount) {
			this.invoiceLine = invoiceLine;
			this.amount = amount;
		}

		/**
		 * @return the amount
		 */
		Money getAmount() {
			return amount;
		}

		/**
		 * @param amount the amount to set
		 */
		private void setAmount(Money amount) {
			this.amount = amount;
		}

		/**
		 * @return the invoiceLine
		 */
		InvoiceLine getInvoiceLine() {
			return invoiceLine;
		}		
	}

}
