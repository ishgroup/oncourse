package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public class WebCCExportLineBuilder extends AbstractExportLineBuilder {
	
	public static final String WEB_CC_KEY = "ccWeb";
	public static final String WEB_CC_LICENSE_KEY = "cc-web";
	public static final String WEB_CC_DESCRIPTION_TEMPLATE = "onCourse online credit card transaction fee for {0}";
	
	public WebCCExportLineBuilder(College college, Date from, Map<Long, Map<String, Object>> billingData, Map<Long, Map<String, Object>> licenseData) {
		super(college, from, billingData, licenseData);
	}

	@Override
	protected String buildDetailedDescription() {
		return MessageFormat.format(WEB_CC_DESCRIPTION_TEMPLATE, MONTH_FORMATTER.format(from));
	}

	@Override
	protected String getStockCode() {
		return StockCodes.WEB_CC.getProductionCode();
	}

	@Override
	protected Long getQuantity() {
		Long ccWeb = (Long) billingData.get(college.getId()).get(WEB_CC_KEY);
		ccWeb = ccWeb == null ? 0 : ccWeb;
		
		return ccWeb;
	}

	@Override
	protected BigDecimal getUnitPrice() {
		BigDecimal ccWebFee = (BigDecimal) licenseData.get(college.getId()).get(WEB_CC_LICENSE_KEY);
		ccWebFee = ccWebFee == null ? new BigDecimal(0.0) : ccWebFee;
		
		return ccWebFee;
	}

	@Override
	protected boolean skipLine() {
		return false;
	}

}
