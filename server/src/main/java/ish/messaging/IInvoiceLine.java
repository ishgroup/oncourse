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
package ish.messaging;

import ish.math.Money;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.Tax;

import java.util.List;

/**
 */
public interface IInvoiceLine extends PersistentObjectI {

    String TOTAL_PRICE_INC_TAX_PROPERTY = "total_price";
	String TOTAL_PRICE_EX_TAX_PROPERTY = "total_price_ex_tax";

    String DISCOUNT_EX_TAX_PROPERTY = "discount_applied_ex_tax";

    String getDescription();

    Money getDiscountTotalExTax();

	Money getDiscountTotalIncTax();

	Money getDiscountEachExTax();

	Money getDiscountedPriceTotalIncTax();

	IEnrolment getEnrolment();

	IInvoice getInvoice();

	Tax getTax();

	Money getPriceTotalExTax();

	List<? extends IDiscount> getDiscounts();
}
