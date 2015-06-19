/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
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
