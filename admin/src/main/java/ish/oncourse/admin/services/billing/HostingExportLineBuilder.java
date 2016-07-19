package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;


public class HostingExportLineBuilder extends PlanExportLineBuilder {
	
	public static final String HOSTING_PLAN_TYPE = "hosting";
	public static final String HOSTING_PLAN_KEY = "hosting-plan";
	public static final String HOSTING_PAID_UNTIL_KEY = "hosting-paidUntil";

	public static final String HOSTING_PLAN_DESCRIPTION_TEMPLATE = "{0} web plan for {1}: 1 month";
	
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
				StringUtils.capitalize(getBillingPlan()),
				webSite.getName());
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
}
