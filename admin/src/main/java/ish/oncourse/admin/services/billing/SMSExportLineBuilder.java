package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public class SMSExportLineBuilder extends AbstractExportLineBuilder {
	
	public static final String SMS_DESCRIPTION_TEMPLATE = "SMS usage for {0}";
	public static final String SMS_KEY = "sms";
	
	public SMSExportLineBuilder(College college, Date from, Map<Long, Map<String, Object>> billingData, Map<Long, Map<String, Object>> licenseData) {
		super(college, from, billingData, licenseData);
	}

	@Override
	protected String buildDetailedDescription() {
		return MessageFormat.format(SMS_DESCRIPTION_TEMPLATE, MONTH_FORMATTER.format(from));
	}

	@Override
	protected String getStockCode() {
		return StockCodes.SMS.getProductionCode();
	}
	
	@Override
	protected Long getQuantity() {
		Long smsQty = (Long) billingData.get(college.getId()).get(SMS_KEY);
		smsQty = smsQty == null ? 0 : smsQty;
		
		return smsQty;
	}

	@Override
	protected BigDecimal getUnitPrice() {
		return (BigDecimal) licenseData.get(college.getId()).get(SMS_KEY);
	}

	@Override
	protected boolean skipLine() {
		return false;
	}

}
