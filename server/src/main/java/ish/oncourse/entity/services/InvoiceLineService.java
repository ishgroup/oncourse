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

package ish.oncourse.entity.services;

import ish.oncourse.server.cayenne.Discount;
import ish.oncourse.server.cayenne.InvoiceLine;

public class InvoiceLineService {

	/**
	 * Returns names of all discounts applied to invoiceLine in single string delimited by "/".
	 *
	 * @param invoiceLine
	 * @return
	 */
	public String getDiscountNames(InvoiceLine invoiceLine) {
		StringBuilder discountNames = new StringBuilder();

		for (Discount discount : invoiceLine.getDiscounts()) {
			if (discountNames.length() > 0) {
				discountNames.append("/");
			}

			discountNames.append(discount.getName());
		}

		return discountNames.length() > 0 ? discountNames.toString() : " Manual discount";
	}

}
