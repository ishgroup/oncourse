package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SupportExportLineBuilder extends PlanExportLineBuilder {
	
	public static final String SUPPORT_PLAN_TYPE = "support";
	public static final String SUPPORT_PLAN_KEY = "support-plan";
	public static final String SUPPORT_PAID_UNTIL_KEY = "support-paidUntil";
	public static final String SUPPORT_RENEWAL_DATE_KEY = "support-renewalDate";
	
	public SupportExportLineBuilder(College college, Date from, Map<Long, Map<String, Object>> billingData, 
			Map<Long, Map<String, Object>> licenseData) {
		super(college, from, billingData, licenseData);
	}

	@Override
	protected String getPlanType() {
		return SUPPORT_PLAN_TYPE;
	}

	@Override
	protected String getBillingPlan() {
		return String.valueOf(licenseData.get(college.getId()).get(SUPPORT_PLAN_KEY));
	}

	@Override
	protected boolean skipLine() {
		return !isSupportBillingMonth();
	}
	
	@Override
	protected Date getRenewalDate() {
		return (Date) licenseData.get(college.getId()).get(SUPPORT_RENEWAL_DATE_KEY);
	}
	
	private boolean isSupportBillingMonth() {
		String billingPlan = (String) licenseData.get(college.getId()).get(SUPPORT_PLAN_KEY);
		
		if (billingPlan == null) {
			return false;
		}
		
		Date paidUntil = (Date) licenseData.get(college.getId()).get(SUPPORT_PAID_UNTIL_KEY);
		
		if (paidUntil == null) {
			return true;
		}
		
		Calendar payMonth = Calendar.getInstance();
		payMonth.setTime(paidUntil);
		
		Calendar billingMonth = Calendar.getInstance();
		billingMonth.setTime(from);
		
		return (payMonth.get(Calendar.YEAR) <= billingMonth.get(Calendar.YEAR) 
				&& payMonth.get(Calendar.MONTH) <= billingMonth.get(Calendar.MONTH));
	}

}
