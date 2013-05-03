package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import org.codehaus.plexus.util.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public class SupportExportLineBuilder extends PlanExportLineBuilder {
	
	public static final String SUPPORT_PLAN_TYPE = "support";
	public static final String SUPPORT_PLAN_KEY = "support-plan";
	public static final String SUPPORT_PAID_UNTIL_KEY = "support-paidUntil";
	public static final String SUPPORT_RENEWAL_DATE_KEY = "support-renewalDate";

	public static final String SUPPORT_PLAN_DESCRIPTION_TEMPLATE = "{0} plan for {1}: 1 month (contract until {2})";
	
	public SupportExportLineBuilder(
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
				SUPPORT_PLAN_DESCRIPTION_TEMPLATE,
				StringUtils.capitalise(getBillingPlan()),
				college.getName(),
				MONTH_FORMATTER.format(getRenewalDate()));
	}

	@Override
	protected String getPlanType() {
		return SUPPORT_PLAN_TYPE;
	}

	@Override
	protected String getBillingPlan() {
		return String.valueOf(licenseData.get(college.getId()).get(getWebSiteId()).get(SUPPORT_PLAN_KEY));
	}

	@Override
	protected boolean skipLine() {
		return !isPlanBillingMonth(SUPPORT_PLAN_KEY, SUPPORT_PAID_UNTIL_KEY);
	}
	
	@Override
	protected Date getRenewalDate() {
		return (Date) licenseData.get(college.getId()).get(getWebSiteId()).get(SUPPORT_RENEWAL_DATE_KEY);
	}
}
