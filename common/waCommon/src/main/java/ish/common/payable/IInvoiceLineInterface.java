package ish.common.payable;

import ish.math.Money;

import java.math.BigDecimal;

/**
 * Common interface for angel and willow, that introduce common InvoiceLine features. <br/>
 * Provides definitions of methods used to calculate various types of totals and subtotals associated with price, discount and taxes for a invoice line (payable
 * line in the future).
 * 
 * @author ksenia, marcin
 */
public interface IInvoiceLineInterface extends PayableLineInterface {

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - reading of the quantity property
	 * 
	 * @return quantity
	 */
	BigDecimal getQuantity();

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - writing of the discountEachExTax property
	 * 
	 * @return discount per unit wihout tax
	 */
	Money getDiscountEachExTax();

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - writing of the discountEachExTax property
	 */
	void setDiscountEachExTax(Money discountEachExTax);

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - reading of the priceEachExTax property
	 * 
	 * @return price per unit without tax
	 */
	Money getPriceEachExTax();

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - writing of the priceEachExTax property
	 */
	void setPriceEachExTax(Money priceEachExTax);

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - reading of the taxEach property
	 * 
	 * @return tax per unit
	 */
	Money getTaxEach();

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - writing of the taxEach property
	 */
	void setTaxEach(Money taxEach);

	/**
	 * this method is supposed to be overridden in InvoiceLine and return "getTax()"
	 * 
	 * @return
	 */
	TaxInterface getInvoiceTax();

	/**
	 * @see common/ish.messaging.IInvoiceLine.getDiscountedPriceTotalTax()
	 * @return
	 */
	Money getDiscountedPriceTotalTax();

	/**
	 * @see common/ish.messaging.IInvoiceLine.getDiscountTotalExTax()
	 * @return
	 */
	Money getDiscountTotalExTax();

	/**
	 * @see common/ish.messaging.IInvoiceLine.getDiscountedPriceTotalIncTax()
	 * @return
	 */
	Money getDiscountedPriceTotalIncTax();

	Money getDiscountedTaxOnPriceEach();

	Money getDiscountedPriceTotalExTax();

	/**
	 * calculates the actual total tax to pay. Used to summarise amount owing and print invoices.
	 * 
	 * @return the final total tax
	 */
	Money getTotalTax();

	/**
	 * calculates the actual result price to pay. Used to summarise amount owing and print invoices.
	 * 
	 * @return the final total price with tax
	 */
	Money getFinalPriceToPayIncTax();

	/**
	 * calculates the actual result price to pay. Used to summarise amount owing and print invoices.
	 * 
	 * @return the final total price without tax
	 */
	Money getFinalPriceToPayExTax();

	/**
	 * InvoiceLine#getEnrolment() definition
	 * 
	 * @return linked to invoiceline enrollment.
	 */
	EnrolmentInterface getEnrolment();

}
