/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.utils;

import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.WebSite;
import org.apache.cayenne.ObjectContext;

import java.math.BigDecimal;

public class LicenseFeeUtil {

	public static final String SUPPORT_FEE_CODE = "support";
	public static final String HOSTING_FEE_CODE = "hosting";
	public static final String SMS_FEE_CODE = "sms";
	public static final String CC_WEB_FEE_CODE = "cc-web";
	public static final String CC_OFFICE_FEE_CODE = "cc-office";
	public static final String ECOMMERCE_FEE_CODE = "ecommerce";

	/**
	 * Creates and returns LicenseFee record with specified values.
	 *
	 * @param context
	 * @param college
	 * @param webSite
	 * @param keyCode
	 * @return new LicenseFee record
	 */
	public static LicenseFee createFee(ObjectContext context, College college, WebSite webSite, String keyCode) {
		LicenseFee fee = context.newObject(LicenseFee.class);
		fee.setCollege(college);
		fee.setWebSite(webSite);
		fee.setKeyCode(keyCode);
		fee.setFreeTransactions(0);
		fee.setFee(new BigDecimal(0));
		return fee;
	}

}
