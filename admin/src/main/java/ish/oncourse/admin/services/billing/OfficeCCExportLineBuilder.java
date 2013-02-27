package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public class OfficeCCExportLineBuilder extends AbstractExportLineBuilder {
	
	public static final String OFFICE_CC_KEY = "ccOffice";
	public static final String OFFICE_CC_LICENSE_KEY = "cc-office";
	public static final String OFFICE_CC_FREE_TRANSACTIONS = "cc-office-free";
	public static final String OFFICE_CC_SIMPLE_DESCRIPTION_TEMPLATE = "onCourse office credit card transaction fee for {0}";
    public static final String OFFICE_CC_DESCRIPTION_TEMPLATE = "onCourse office credit card transaction fee for {0} ({1} less {2} free transactions)";
	
	public OfficeCCExportLineBuilder(College college, Date from, Map<Long, Map<String, Object>> billingData, Map<Long, Map<String, Object>> licenseData) {
		super(college, from, billingData, licenseData);
	}
	
	private Integer getFreeTransactionsCount() {
		Integer ccOfficeFree = (Integer) licenseData.get(college.getId()).get(OFFICE_CC_FREE_TRANSACTIONS);
		ccOfficeFree = ccOfficeFree == null ? 0 : ccOfficeFree;
		
		return ccOfficeFree;
	}
	
	private Long getTransactionsCount() {
		Long ccOffice = (Long) billingData.get(college.getId()).get(OFFICE_CC_KEY);
		ccOffice = ccOffice == null ? 0 : ccOffice;
		
		return ccOffice;
	}

	@Override
	protected String buildDetailedDescription() {
		
		String template = getFreeTransactionsCount() > 0 ? OFFICE_CC_DESCRIPTION_TEMPLATE : OFFICE_CC_SIMPLE_DESCRIPTION_TEMPLATE;
		
		return MessageFormat.format(
				template, 
				MONTH_FORMATTER.format(from), 
				getTransactionsCount(), 
				getFreeTransactionsCount());
	}

	@Override
	protected String getStockCode() {
		return StockCodes.OFFICE_CC.getProductionCode();
	}

	@Override
	protected Long getQuantity() {
		return Math.max(0, getTransactionsCount() - getFreeTransactionsCount());
	}

	@Override
	protected BigDecimal getUnitPrice() {
		return (BigDecimal) licenseData.get(college.getId()).get(OFFICE_CC_LICENSE_KEY);
	}

	@Override
	protected boolean skipLine() {
		return false;
	}

}
