/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * User: akoiro
 * Date: 5/07/2016
 */
public class PaymentInRow {
	@Inject
	private IPortalService portalService;

	@Parameter
	@Property
	private PaymentIn paymentIn;

	@Parameter
	@Property
	private boolean isNew;

	public Format moneyFormat(Money money) {
		return FormatUtils.chooseMoneyFormat(money);
	}

	public Format dateFormat() {
		return new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy);
	}

	public boolean isNew() {
		return portalService.isNew(paymentIn);
	}

	public String getPaymentType() {
		return paymentIn.getType().getDisplayName();
	}
}
