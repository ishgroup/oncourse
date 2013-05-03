package ish.oncourse.admin.services.billing;

import ish.math.Country;
import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

public class EcommerceExportLineBuilder extends AbstractExportLineBuilder {
	
	public static final String ECOMMERCE_KEY = "ecommerce";
	public static final String WEB_VALUE_KEY = "ccWebValue";
	public static final String ECOMMERCE_DESCRIPTION_TEMPLATE = "{0} eCommerce fee at {1}% of {2}";
	
	private static final String TASMANIA_ECOMMERCE_MIN_FEE_DESC = "Adjustment for onCourse eCommerce minimum monthly fee of $375.";
	private static final Long ECOMMERCE_QUANTITY = 1L;
	private static final Long TASMANIA_COLLEGE_ID = 15L;
	private static final double TASMANIA_MIN_FEE = 375;
	
	private DecimalFormat decimalFormatter;
	private NumberFormat moneyFormat;
	
	public EcommerceExportLineBuilder(
			College college,
			WebSite webSite,
			Date from,
			Map<Long, Map<Long, Map<String, Object>>> billingData,
			Map<Long, Map<Long, Map<String, Object>>> licenseData) {
		super(college, webSite, from, billingData, licenseData);
		
		decimalFormatter = new DecimalFormat();
		decimalFormatter.setRoundingMode(RoundingMode.valueOf(2));
		
		moneyFormat = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
		moneyFormat.setMinimumFractionDigits(2);
	}
	
	private boolean isTasmaniaEcommerce() {
		return TASMANIA_COLLEGE_ID.equals(college.getId());
	}
	
	@Override
	public String buildLine() {

		if (isTasmaniaEcommerce()) {

			String tasmaniaLines = "";

			Map<Double, Double> tasmaniaEcommerceFees = getTasmaniaFees();

			double totalFee = 0.0;

			for (Double key : tasmaniaEcommerceFees.keySet()) {

				double fee = tasmaniaEcommerceFees.get(key) * key / 100;
				totalFee += fee;

				tasmaniaLines += MessageFormat.format(
						Constants.TEMPLATE,
						college.getBillingCode(),
						getStockCode(),
						getTasmaniaEcommerceKeyDescription(key, tasmaniaEcommerceFees),
						ECOMMERCE_QUANTITY,
						decimalFormatter.format(fee),
						getDescription(),
						getTransactionDateString());
			}

			if (totalFee < TASMANIA_MIN_FEE) {
				tasmaniaLines += MessageFormat.format(
						Constants.TEMPLATE,
						college.getBillingCode(),
						getStockCode(),
						TASMANIA_ECOMMERCE_MIN_FEE_DESC,
						ECOMMERCE_QUANTITY,
						decimalFormatter.format(TASMANIA_MIN_FEE - totalFee),
						getDescription(),
						getTransactionDateString());
			}

			return tasmaniaLines;
		}
		
		return super.buildLine();
	}
	
	private String getTasmaniaEcommerceKeyDescription(Double key, Map<Double, Double> tasmaniaEcommerceFees) {
		return MessageFormat.format(
				ECOMMERCE_DESCRIPTION_TEMPLATE,
				webSite.getName(),
				decimalFormatter.format(key),
				moneyFormat.format(tasmaniaEcommerceFees.get(key)));
	}
	
	private BigDecimal getEcommerce() {
		BigDecimal ecommerce = (BigDecimal) licenseData.get(college.getId()).get(getWebSiteId()).get(ECOMMERCE_KEY);
		ecommerce = ecommerce == null ? new BigDecimal(0.0) : ecommerce;
		
		return ecommerce;
	}
	
	private BigDecimal getWebValue() {
		BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get(getWebSiteId()).get(WEB_VALUE_KEY);
		ccWebValue = ccWebValue ==null ? new BigDecimal(0.0) : ccWebValue;
		
		return ccWebValue;
	}

	@Override
	protected String buildDetailedDescription() {
		BigDecimal ecommerce = (BigDecimal) licenseData.get(college.getId()).get(getWebSiteId()).get(ECOMMERCE_KEY);
		BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get(getWebSiteId()).get(WEB_VALUE_KEY);
		ecommerce = ecommerce == null ? new BigDecimal(0.0) : ecommerce;
		ccWebValue = ccWebValue ==null ? new BigDecimal(0.0) : ccWebValue;
		
		return MessageFormat.format(
				ECOMMERCE_DESCRIPTION_TEMPLATE,
				webSite.getName(),
				decimalFormatter.format(ecommerce.doubleValue() * 100),
				moneyFormat.format(ccWebValue));
	}

	@Override
	protected String getStockCode() {
		return StockCodes.ECOMMERCE.getProductionCode();
	}

	@Override
	protected Long getQuantity() {
		return ECOMMERCE_QUANTITY;
	}

	@Override
	protected BigDecimal getUnitPrice() {
		return getWebValue().multiply(getEcommerce());
	}

	@Override
	protected boolean skipLine() {
		return false;
	}

	private Map<Double, Double> getTasmaniaFees() {
		if (isTasmaniaEcommerce()) {
			BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get(getWebSiteId()).get("ccWebValue");
			ccWebValue = ccWebValue == null ? new BigDecimal(0.0) : ccWebValue;
			return BillingDataServiceImpl.getTasmaniaEcommerceMap(ccWebValue.doubleValue(), billingData);
		} else {
			return null;
		}
	}
}
