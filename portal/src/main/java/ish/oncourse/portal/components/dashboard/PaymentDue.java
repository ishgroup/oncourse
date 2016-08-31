/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.dashboard;

import ish.math.Money;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.text.Format;

public class PaymentDue {

	@Parameter
	@Property
	private Money paymentDue;

	public Format getMoneyFormat() {
		return FormatUtils.chooseMoneyFormat(paymentDue);
	}

}
