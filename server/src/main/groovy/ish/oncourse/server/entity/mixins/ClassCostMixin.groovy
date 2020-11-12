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

package ish.oncourse.server.entity.mixins

import ish.budget.ClassCostUtil
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.ClassCost

//TODO docs
class ClassCostMixin {


	@Deprecated
	static Money getPerUnitAmountExTax(ClassCost self) {
		return ClassCostUtil.getPerUnitAmountExTax(self)
	}

	@API
	static Money getBudgetedCost(ClassCost self) {
		return ClassCostUtil.getBudgetedCost(self)
	}

	static Money getActualCost(ClassCost self) {
		return ClassCostUtil.getActualCost(self)
	}

	@API
	static String getUnit2(ClassCost self) {
		return ClassCostUtil.getUnit2(self)
	}

	@API
	static boolean isAmountOverride(ClassCost self) {
		return ClassCostUtil.isAmountOverride(self)
	}

}
