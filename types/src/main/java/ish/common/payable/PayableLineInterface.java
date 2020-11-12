/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.common.payable;

import ish.math.Money;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * interface uniting InvoiceLine and PayLine between server and client
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
