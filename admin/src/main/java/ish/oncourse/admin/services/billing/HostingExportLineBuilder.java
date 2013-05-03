package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;


public class HostingExportLineBuilder extends PlanExportLineBuilder {
	
	public static final String HOSTING_PLAN_TYPE = "hosting";
	public static final String HOSTING_PLAN_KEY = "hosting-plan";
	public static final String HOSTING_PAID_UNTIL_KEY = "hosting-paidUntil";
	public static final String HOSTING_RENEWAL_DATE_KEY = "hosting-renewalDate";

	public static final String HOSTING_PLAN_DESCRIPTION_TEMPLATE = "{0} {1} web plan: 1 month (contract until {2})";
	
	public HostingExportLineBuilder(
			College college,
			WebSite webSite,
			Date from,
			Map<Long, Map<Long, Map<String, Object>>> billingData,
			Map<Long, Map<Long, Map<String, Object>>> licenseData) {
		super(college, webSite, from, billingData, licenseData);
	}

	@Override
	protected String buildDetailedDescription() {
		return MessageFormat.format(
				HOSTING_PLAN_DESCRIPTION_TEMPLATE,
				webSite.getName(),
				getBillingPlan(),
				MONTH_FORMATTER.format(getRenewalDate()));
	}

	@Override
	protected String getPlanType() {
		return HOSTING_PLAN_TYPE;
	}

	@Override
	protected String getBillingPlan() {
		return String.valueOf(licenseData.get(college.getId()).get(getWebSiteId()).get(HOSTING_PLAN_KEY));
	}

	@Override
	protected boolean skipLine() {
		return !isPlanBillingMonth(HOSTING_PLAN_KEY, HOSTING_PAID_UNTIL_KEY);
	}
	
	@Override
	protected Date getRenewalDate() {
		return (Date) licenseData.get(college.getId()).get(getWebSiteId()).get(HOSTING_RENEWAL_DATE_KEY);
	}
}
