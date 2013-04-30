package ish.oncourse.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ish.common.payable.TaxInterface;
import ish.math.Money;
import ish.oncourse.model.auto._InvoiceLine;
import ish.oncourse.utils.QueueableObjectUtils;

public class InvoiceLine extends _InvoiceLine implements Queueable {
	
	private static final long serialVersionUID = 8005646295584671217L;

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getInvoiceTax()
	 */
	@Override
	public TaxInterface getInvoiceTax() {
		return new FakeTax();
	}

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
	
	public void refund(final org.apache.cayenne.validation.ValidationResult result, final Invoice invoiceToRefund, final Invoice refundInvoice) {

		final InvoiceLine refundInvoiceLine = getObjectContext().newObject(InvoiceLine.class);

		refundInvoiceLine.setCollege(getCollege());
		refundInvoiceLine.setDescription("Refund for enrolment : " + getDescription()); // this gives the description for class (including
		// course name etc)
		refundInvoiceLine.setInvoice(refundInvoice);
		refundInvoiceLine.setSortOrder(getSortOrder());
		refundInvoiceLine.setTitle(getTitle());
		refundInvoiceLine.setUnit(getUnit());
		refundInvoiceLine.setQuantity(new BigDecimal("1.00"));
		refundInvoiceLine.setTaxEach(Money.ZERO.subtract(getTaxEach()));
		// calculate the refund amount based on this enrolment paid price, so include the discount:
		refundInvoiceLine.setPriceEachExTax(Money.ZERO.subtract(getPriceEachExTax()));
		refundInvoiceLine.setDiscountEachExTax(Money.ZERO.subtract(getDiscountEachExTax()));
	}
	
	/**
	 * Check if async replication is allowed on this object.
	 * @return
	 */
	public boolean isAsyncReplicationAllowed() {
		return getInvoice() != null && getInvoice().isAsyncReplicationAllowed();
	}
	
	public List<Voucher> getVouchers() {
		List<Voucher> vouchers = new ArrayList<>();
		for (ProductItem productItem : getProductItem()) {
			if (productItem instanceof Voucher) {
				vouchers.add((Voucher) productItem);
			}
		}
		return vouchers;
	}
	
	public List<Membership> getMemberShips() {
		List<Membership> memberships = new ArrayList<>();
		for (ProductItem productItem : getProductItem()) {
			if (productItem instanceof Membership) {
				memberships.add((Membership) productItem);
			}
		}
		return memberships;
	}
}
