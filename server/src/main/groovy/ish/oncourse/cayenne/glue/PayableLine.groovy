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
package ish.oncourse.cayenne.glue

import ish.common.payable.PayableLineInterface
import ish.math.Money
import org.apache.cayenne.PersistentObject

/**
 */
abstract class PayableLine extends PersistentObject implements PayableLineInterface {

	/**
	 *
	 */


	Money getDiscountEachExTax() {
		return null
	}

	Money getDiscountEachIncTax() {
		return null
	}

	Money getDiscountTotalExTax() {
		return null
	}

	Money getDiscountTotalIncTax() {
		return null
	}

	Money getPriceEachExTax() {
		return null
	}

	Money getPriceEachIncTax() {
		return null
	}

	Money getPriceTotalExTax() {
		return null
	}

	Money getPriceTotalIncTax() {
		return null
	}

	Money getTaxEach() {
		return null
	}

}
