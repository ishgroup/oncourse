/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.CustomFee;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;


public class CustomFeeExportLineBuilder extends AbstractExportLineBuilder {


	public CustomFeeExportLineBuilder(College college, Date from, CustomFee customFee) {
		super(college, from, customFee);
	}


	@Override
	protected String buildDetailedDescription() {
		return customFee.getName();
	}

	@Override
	protected String getStockCode() {
		return customFee.getCode();
	}

	@Override
	protected Long getQuantity() {
		return 1L;
	}

	@Override
	protected BigDecimal getUnitPrice() {
		return customFee.getFee();
	}

	@Override
	protected boolean skipLine() {

		Date paidUntil = customFee.getPaidUntil();

		if (paidUntil == null) {
			return false;
		}

		Calendar payMonth = Calendar.getInstance();
		payMonth.setTime(paidUntil);

		Calendar billingMonth = Calendar.getInstance();
		billingMonth.setTime(from);

		return DateUtils.truncate(payMonth, Calendar.MONTH).compareTo(DateUtils.truncate(billingMonth, Calendar.MONTH)) > 0;
	}

}
