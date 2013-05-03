package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class SMSExportLineBuilder extends AbstractExportLineBuilder {
	
	public static final String SMS_DESCRIPTION_TEMPLATE = "SMS usage";
	public static final String SMS_KEY = "sms";
	
	public SMSExportLineBuilder(
			College college,
			WebSite webSite,
			Date from,
			Map<Long, Map<Long, Map<String, Object>>> billingData,
			Map<Long, Map<Long, Map<String, Object>>> licenseData) {
		super(college, webSite, from, billingData, licenseData);
	}

	@Override
	protected String buildDetailedDescription() {
		return SMS_DESCRIPTION_TEMPLATE;
	}

	@Override
	protected String getStockCode() {
		return StockCodes.SMS.getProductionCode();
	}
	
	@Override
	protected Long getQuantity() {
		Long smsQty = (Long) billingData.get(college.getId()).get(getWebSiteId()).get(SMS_KEY);
		smsQty = smsQty == null ? 0 : smsQty;
		
		return smsQty;
	}

	@Override
	protected BigDecimal getUnitPrice() {
		return (BigDecimal) licenseData.get(college.getId()).get(getWebSiteId()).get(SMS_KEY);
	}

	@Override
	protected boolean skipLine() {
		return false;
	}

}
