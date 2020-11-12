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
package ish.oncourse.cayenne.glue;

import ish.common.payable.PayableLineInterface;
import ish.math.Money;
import org.apache.cayenne.PersistentObject;

/**
 */
public abstract class PayableLine extends PersistentObject implements PayableLineInterface {

	/**
	 *
	 */


	public Money getDiscountEachExTax() {
		return null;
	}

	public Money getDiscountEachIncTax() {
		return null;
	}

	public Money getDiscountTotalExTax() {
		return null;
	}

	public Money getDiscountTotalIncTax() {
		return null;
	}

	public Money getPriceEachExTax() {
		return null;
	}

	public Money getPriceEachIncTax() {
		return null;
	}

	public Money getPriceTotalExTax() {
		return null;
	}

	public Money getPriceTotalIncTax() {
		return null;
	}

	public Money getTaxEach() {
		return null;
	}

}
