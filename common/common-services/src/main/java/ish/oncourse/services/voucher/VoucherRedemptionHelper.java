package ish.oncourse.services.voucher;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private static final Expression ACTIVE_VOUCHER_QUALIFIER = ExpressionFactory.matchExp(Voucher.STATUS_PROPERTY, VoucherStatus.ACTIVE);
	private List<Voucher> vouchers;
	private Invoice invoice;
	private List<PaymentIn> payments;
	
	/**
	 * Default constructor.
	 */
	public VoucherRedemptionHelper() {}

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
		if (voucher != null) {
			return vouchers.add(voucher);
		}
		return false;
	}
	
	public boolean removeVoucher(Voucher voucher) {
		if (voucher != null && getVouchers().contains(voucher)) {
			return vouchers.remove(voucher);
		}
		return false;
	}
	
	/**
	 * Discard the changes made from previous VoucherRedemptionHelper#calculateVouchersRedeemPayment() execution.
	 */
	public void discardChanges() {
		for (PaymentIn paymentIn : getPayments()) {
			ObjectContext context = paymentIn.getObjectContext();
			context.deleteObjects(paymentIn.getPaymentInLines());
			Voucher voucher = paymentIn.getRedeemedVoucher();
			context.deleteObject(paymentIn);
			if (checkIsOriginalVoucher(getVouchers(), voucher)) {
				voucher.setStatus(VoucherStatus.ACTIVE);
				for (Voucher relatedVouchers : voucher.getInvoiceLine().getVouchers()) {
					if (!checkIsOriginalVoucher(getVouchers(), relatedVouchers)) {
						if (relatedVouchers.getRedemptionPayment() != null) {
							//we cancel the cloned voucher which not linked with this payment, but should be canceled
							relatedVouchers.setRedemptionPayment(null);
						}
						relatedVouchers.setStatus(VoucherStatus.CANCELLED);
						context.deleteObject(relatedVouchers);
					}
				}
			} else {
				if (voucher != null) {
					//delete the cloned voucher if already linked
					voucher.setStatus(VoucherStatus.CANCELLED);
					//voucher.setInvoiceLine(null);
					context.deleteObject(voucher);
				}
			}
		}
		getPayments().clear();
	}
	
	private boolean checkIsOriginalVoucher(List<Voucher> originalVouchers, Voucher voucherForCheck) {
		for (Voucher voucher : originalVouchers) {
			if (voucher.equals(voucherForCheck)) {
				return true;
			}
		}
		return false;
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
						&& courseVoucher.getVoucherProduct().getRedemptionCourses().contains(invoiceLine.getEnrolment().getCourseClass().getCourse())) {
						//we need to check that this voucher not fully redeemed 
						Voucher activeVoucher = takeFirstActiveVoucher(courseVoucher);
						if (activeVoucher != null) {
							getPayments().add(redeemVoucherForCourse(activeVoucher, invoiceLine));
							redeemedInvoiceLines.add(invoiceLine);
						}
					}
				}
			}
			//check available vouchers with money
			List<PartiallyRedeemedInvoiceLine> partiallyRedeemedInvoiceLines = new ArrayList<PartiallyRedeemedInvoiceLine>();
			List<Voucher> moneyVouchers = ACTIVE_VOUCHER_QUALIFIER.filterObjects(MONEY_VOUCHER_QUALIFIER.filterObjects(getVouchers()));
			for (Voucher moneyVoucher : moneyVouchers) {
				for (InvoiceLine invoiceLine : getInvoice().getInvoiceLines()) {
					if (takePartiallyRedeemedInvoiceLine(partiallyRedeemedInvoiceLines, invoiceLine) == null 
						&& !redeemedInvoiceLines.contains(invoiceLine) 
						&& invoiceLine.getEnrolment() != null) {
						//we need to check that this voucher not fully redeemed 
						Voucher activeVoucher = takeFirstActiveVoucher(moneyVoucher);
						if (activeVoucher != null) {
							PaymentIn payment = redeemVoucherForMoney(activeVoucher, invoiceLine);
							getPayments().add(payment);
							if (payment.getAmount().equals(invoiceLine.getDiscountedPriceTotalIncTax().toBigDecimal())) {
								redeemedInvoiceLines.add(invoiceLine);
							} else {
								partiallyRedeemedInvoiceLines.add(new PartiallyRedeemedInvoiceLine(invoiceLine, payment.getAmount()));
							}
						}
					}
				}
			}
			//now we need to check is any partially payed invoiceLines linked with money vouchers
			if (!partiallyRedeemedInvoiceLines.isEmpty()) {
				for (Voucher moneyVoucher : moneyVouchers) {
					for (InvoiceLine invoiceLine : getInvoice().getInvoiceLines()) {
						PartiallyRedeemedInvoiceLine partiallyRedeemedInvoiceLine = takePartiallyRedeemedInvoiceLine(partiallyRedeemedInvoiceLines, 
							invoiceLine);
						if (partiallyRedeemedInvoiceLine != null) {
							//we need to check that this voucher not fully redeemed 
							Voucher activeVoucher = takeFirstActiveVoucher(moneyVoucher);
							if (activeVoucher != null) {
								PaymentIn payment = redeemVoucherForMoney(activeVoucher, invoiceLine, 
									partiallyRedeemedInvoiceLine.getAlreadyRedeemedAmount());
								getPayments().add(payment);
								boolean fullyRedeemed = isFullyRedeemed(payment.getAmount(), partiallyRedeemedInvoiceLine.getAlreadyRedeemedAmount(), 
									invoiceLine.getDiscountedPriceTotalIncTax().toBigDecimal());
								if (fullyRedeemed) {
									partiallyRedeemedInvoiceLines.remove(partiallyRedeemedInvoiceLine);
									redeemedInvoiceLines.add(invoiceLine);
								} else {
									partiallyRedeemedInvoiceLine.setAlreadyRedeemedAmount(
										partiallyRedeemedInvoiceLine.getAlreadyRedeemedAmount().add(payment.getAmount()));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean isFullyRedeemed(BigDecimal lastPaymentAmount, BigDecimal previousPaymentsAmount, BigDecimal fullPrice) {
		return fullPrice.subtract(previousPaymentsAmount).subtract(lastPaymentAmount).compareTo(BigDecimal.ZERO) == 0;
	}
	
	private PartiallyRedeemedInvoiceLine takePartiallyRedeemedInvoiceLine(List<PartiallyRedeemedInvoiceLine> partiallyRedeemedInvoiceLines, 
		InvoiceLine invoiceLine) {
		if (invoiceLine != null && partiallyRedeemedInvoiceLines != null && !partiallyRedeemedInvoiceLines.isEmpty()) {
			for (PartiallyRedeemedInvoiceLine partiallyRedeemedInvoiceLine : partiallyRedeemedInvoiceLines) {
				if (partiallyRedeemedInvoiceLine.getInvoiceLine().equals(invoiceLine)) {
					return partiallyRedeemedInvoiceLine;
				}
			}
		}
		return null;
	}
	
	/**
	 * Take the first active voucher linked with the same invoiceline as the original voucher.
	 * @param originalVoucher
	 * @return
	 */
	Voucher takeFirstActiveVoucher(Voucher originalVoucher) {
		if (originalVoucher == null) {
			return null;
		}
		if (VoucherStatus.ACTIVE.equals(originalVoucher.getStatus())) {
			return originalVoucher;
		} else {
			List<Voucher> invoiceLineVouchers = originalVoucher.getInvoiceLine().getVouchers();
			Collections.sort(invoiceLineVouchers, new VoucherComparator());
			for (Voucher voucher : invoiceLineVouchers) {
				if (VoucherStatus.ACTIVE.equals(voucher.getStatus())) {
					return voucher;
				}
			}
		}
		return null;
	}
	
	/**
	 * Redeem money voucher for invoiceline if no additional payments exist for them.
	 * @param moneyVoucher - money voucher for use
	 * @param invoiceLine - invoiceline for payment generation.
	 * @return payment linked with voucher.
	 */
	PaymentIn redeemVoucherForMoney(Voucher moneyVoucher, InvoiceLine invoiceLine) {
		return redeemVoucherForMoney(moneyVoucher, invoiceLine, BigDecimal.ZERO);
	}
	
	/**
	 * Redeem money voucher for invoiceline if any additional payments exist for them.
	 * @param moneyVoucher - money voucher for use.
	 * @param invoiceLine - invoiceline for payment generation.
	 * @param alreadyRedeemedAmount - already redeemed amount of invoiceline
	 * @return payment linked with voucher.
	 */
	PaymentIn redeemVoucherForMoney(Voucher moneyVoucher, InvoiceLine invoiceLine, BigDecimal alreadyRedeemedAmount) {
		PaymentIn paymentIn = getInvoice().getObjectContext().newObject(PaymentIn.class);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setType(PaymentType.VOUCHER);
		paymentIn.setContact(getInvoice().getContact());
		//evaluate amount for the payment
		Money leftToPay = new Money(invoiceLine.getDiscountedPriceTotalIncTax().toBigDecimal().subtract(alreadyRedeemedAmount));
		Money amount = moneyVoucher.getRedemptionValue().isLessThan(leftToPay) ? moneyVoucher.getRedemptionValue() : leftToPay;
		paymentIn.setAmount(amount.toBigDecimal());
		paymentIn.setSource(getInvoice().getSource());
		paymentIn.setCollege(getInvoice().getCollege());
		//fill paymentInLine
		PaymentInLine paymentInLine  = paymentIn.getObjectContext().newObject(PaymentInLine.class);
		paymentInLine.setAmount(paymentIn.getAmount());
		paymentInLine.setCollege(paymentIn.getCollege());
		paymentInLine.setInvoice(getInvoice());
		paymentInLine.setPaymentIn(paymentIn);
		//check is voucher fully redeemed
		moneyVoucher = (Voucher) getInvoice().getObjectContext().localObject(moneyVoucher.getObjectId(), null);
		Voucher newVoucher = moneyVoucher.makeShallowCopy();
		newVoucher.setRedemptionValue(moneyVoucher.getRedemptionValue().subtract(amount));
		if (newVoucher.isFullyRedeemed()) {
			getInvoice().getObjectContext().deleteObject(newVoucher);
		}
		moneyVoucher.setStatus(VoucherStatus.REDEEMED);
		moneyVoucher.setRedeemedInvoiceLine(invoiceLine);
		moneyVoucher.setRedemptionPayment(paymentIn);
		return paymentIn;
	}
	
	/**
	 * Redeem course voucher for invoiceline.
	 * @param courseVoucher - course voucher for use
	 * @param invoiceLine - invoiceline for payment generation.
	 * @return payment linked with voucher.
	 */
	PaymentIn redeemVoucherForCourse(Voucher courseVoucher, InvoiceLine invoiceLine) {
		PaymentIn paymentIn = getInvoice().getObjectContext().newObject(PaymentIn.class);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setType(PaymentType.VOUCHER);
		paymentIn.setContact(getInvoice().getContact());
		paymentIn.setAmount(invoiceLine.getDiscountedPriceTotalIncTax().toBigDecimal());
		paymentIn.setSource(getInvoice().getSource());
		paymentIn.setCollege(getInvoice().getCollege());
		//fill paymentInLine
		PaymentInLine paymentInLine  = paymentIn.getObjectContext().newObject(PaymentInLine.class);
		paymentInLine.setAmount(paymentIn.getAmount());
		paymentInLine.setCollege(paymentIn.getCollege());
		paymentInLine.setInvoice(getInvoice());
		paymentInLine.setPaymentIn(paymentIn);
		//check is voucher fully redeemed
		courseVoucher = (Voucher) getInvoice().getObjectContext().localObject(courseVoucher.getObjectId(), null);
		Voucher newVoucher = courseVoucher.makeShallowCopy();
		newVoucher.setRedeemedCoursesCount(courseVoucher.getRedeemedCoursesCount() + 1);
		if (newVoucher.isFullyRedeemed()) {
			getInvoice().getObjectContext().deleteObject(newVoucher);
		}
		courseVoucher.setStatus(VoucherStatus.REDEEMED);
		courseVoucher.setRedeemedInvoiceLine(invoiceLine);
		courseVoucher.setRedemptionPayment(paymentIn);
		return paymentIn;
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
						if (voucher1.getRedemptionValue() != null) {
							if (voucher2.getRedemptionValue() != null) {
								return voucher1.getRedemptionValue().compareTo(voucher2.getRedemptionValue());
							} else {
								return 1;
							}
						}
					} else {
						return courseCountResult;
					}
				} else if (voucher1.getRedemptionValue() != null) {
					if (voucher2.getRedemptionValue() != null) {
						return voucher1.getRedemptionValue().compareTo(voucher2.getRedemptionValue());
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
	private class PartiallyRedeemedInvoiceLine {
		private final InvoiceLine invoiceLine;
		private BigDecimal alreadyRedeemedAmount;
		
		protected PartiallyRedeemedInvoiceLine(InvoiceLine invoiceLine, BigDecimal alreadyRedeemedAmount) {
			this.invoiceLine = invoiceLine;
			this.alreadyRedeemedAmount = alreadyRedeemedAmount;
		}

		/**
		 * @return the alreadyRedeemedAmount
		 */
		BigDecimal getAlreadyRedeemedAmount() {
			return alreadyRedeemedAmount;
		}

		/**
		 * @param alreadyRedeemedAmount the alreadyRedeemedAmount to set
		 */
		private void setAlreadyRedeemedAmount(BigDecimal alreadyRedeemedAmount) {
			this.alreadyRedeemedAmount = alreadyRedeemedAmount;
		}

		/**
		 * @return the invoiceLine
		 */
		InvoiceLine getInvoiceLine() {
			return invoiceLine;
		}		
	}

}
