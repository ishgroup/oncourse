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
package ish.oncourse.cayenne;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;

import java.math.BigDecimal;

public interface DiscountInterface {

	public DiscountType getDiscountType();

	public BigDecimal getDiscountPercent();

	public Money getDiscountDollar();

	public Money getDiscountMin();

	public Money getDiscountMax();

	public MoneyRounding getRounding();

}
