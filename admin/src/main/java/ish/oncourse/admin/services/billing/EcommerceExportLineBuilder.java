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
	public static final String ECOMMERCE_FREE_KEY = "ecommerce-free";
	public static final String WEB_VALUE_KEY = "ccWebValue";
	public static final String FULL_ECOMMERCE_DESCRIPTION_TEMPLATE = "{0} eCommerce fee at {1}% of {2} ({3} less pre-paid bundle) ";

	private static final String SIMPLE_ECOMMERCE_DESCRIPTION_TEMPLATE = "{0} eCommerce fee at {1}% of {2}";
	private static final Long ECOMMERCE_QUANTITY = 1L;

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
	
	private BigDecimal getEcommerce() {
		BigDecimal ecommerce = (BigDecimal) licenseData.get(college.getId()).get(getWebSiteId()).get(ECOMMERCE_KEY);
		ecommerce = ecommerce == null ? new BigDecimal(0.0) : ecommerce;
		
		return ecommerce;
	}

	private BigDecimal getEcommerceFree() {
		Integer ecommerceFree = (Integer) licenseData.get(college.getId()).get(getWebSiteId()).get(ECOMMERCE_FREE_KEY);
		return ecommerceFree == null ? new BigDecimal(0.0) : new BigDecimal(ecommerceFree);
	}
	
	private BigDecimal getWebValue() {
		BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get(getWebSiteId()).get(WEB_VALUE_KEY);
		ccWebValue = ccWebValue == null ? new BigDecimal(0.0) : ccWebValue;
		
		return ccWebValue;
	}

	@Override
	protected String buildDetailedDescription() {
		return MessageFormat.format(
				getEcommerceFree().compareTo(new BigDecimal(0)) > 0 ? FULL_ECOMMERCE_DESCRIPTION_TEMPLATE : SIMPLE_ECOMMERCE_DESCRIPTION_TEMPLATE,
				webSite.getName(),
				decimalFormatter.format(getEcommerce().doubleValue() * 100),
				moneyFormat.format(getWebValue()),
				moneyFormat.format(getEcommerceFree()));
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
		return getWebValue().compareTo(getEcommerceFree()) == -1 ? new BigDecimal(0.0) : getWebValue().subtract(getEcommerceFree()).multiply(getEcommerce());
	}

	@Override
	protected boolean skipLine() {
		return false;
	}
}
