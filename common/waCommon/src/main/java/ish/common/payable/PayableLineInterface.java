/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.payable;

import ish.math.Money;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * interface uniting InvoiceLine and PayLine between server and client
 * 
 * @author marcin
 */
public interface PayableLineInterface extends Serializable {

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
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - reading of the quantity property
	 * 
	 * @return price per unit without tax
	 */
	Money getPriceEachExTax();

	/**
	 * this method is supposed to be overridden by cayenne-generated _InvoiceLine - reading of the taxEach property
	 * 
	 * @return
	 */
	Money getTaxEach();

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

	Money getDiscountEachIncTax();

	Money getDiscountTotalIncTax();

	Money getPriceTotalExTax();

	Money getPriceEachIncTax();

	Money getPriceTotalIncTax();

}
