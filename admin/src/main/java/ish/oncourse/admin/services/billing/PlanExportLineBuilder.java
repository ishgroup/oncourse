package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public abstract class PlanExportLineBuilder extends AbstractExportLineBuilder {
	
	public static final String PLAN_DESCRIPTION_TEMPLATE = "onCourse {0} {1} plan for {2}: {3} (contract until {4})";
	
	private static final Long PLAN_QUANTITY = 1L;
	
	public PlanExportLineBuilder(College college, Date from, Map<Long, Map<String, Object>> billingData, Map<Long, Map<String, Object>> licenseData) {
		super(college, from, billingData, licenseData);
	}

	@Override
	protected String buildDetailedDescription() {
       return MessageFormat.format(
        		PLAN_DESCRIPTION_TEMPLATE, 
        		getBillingPlan(),
                getPlanType(),
                college.getName(),
                MONTH_FORMATTER.format(from),
                MONTH_FORMATTER.format(getRenewalDate()));
	}
	
	@Override
	protected String getStockCode() {
		return StockCodes.valueOf(getBillingPlan()).getProductionCode();
	}
	
	@Override
	protected Long getQuantity() {
		return PLAN_QUANTITY;
	}
	
	protected abstract Date getRenewalDate();
	
	protected abstract String getPlanType();
	
	protected abstract String getBillingPlan();

	@Override
	protected BigDecimal getUnitPrice() {
		return (BigDecimal) licenseData.get(college.getId()).get(getPlanType());
	}
	
}
