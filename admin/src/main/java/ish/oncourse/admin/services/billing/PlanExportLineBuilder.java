package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public abstract class PlanExportLineBuilder extends AbstractExportLineBuilder {

	private static final Long PLAN_QUANTITY = 1L;
	
	public PlanExportLineBuilder(
			College college,
			WebSite webSite,
			Date from,
			Map<Long, Map<Long, Map<String, Object>>> billingData,
			Map<Long, Map<Long, Map<String, Object>>> licenseData) {
		super(college, webSite, from, billingData, licenseData);
	}
	
	@Override
	protected String getStockCode() {
		return StockCodes.valueOf(getBillingPlan()).getProductionCode();
	}
	
	@Override
	protected Long getQuantity() {
		return PLAN_QUANTITY;
	}
	
	protected abstract String getPlanType();
	
	protected abstract String getBillingPlan();

	@Override
	protected BigDecimal getUnitPrice() {
		return (BigDecimal) licenseData.get(college.getId()).get(getWebSiteId()).get(getPlanType());
	}
	
	protected boolean isPlanBillingMonth(String planKey, String paidUntilKey) {

		String billingPlan = (String) licenseData.get(college.getId()).get(getWebSiteId()).get(planKey);

		Date paidUntil = (Date) licenseData.get(college.getId()).get(getWebSiteId()).get(paidUntilKey);

		return IsPlanBillingMonth.valueOf(billingPlan, paidUntil, from).is();
	}
	
}
