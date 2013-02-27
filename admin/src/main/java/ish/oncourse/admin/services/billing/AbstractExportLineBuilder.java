package ish.oncourse.admin.services.billing;

import static ish.oncourse.admin.services.billing.Constants.DATE_MONTH_FORMAT;
import ish.oncourse.model.College;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractExportLineBuilder implements MWExportLineBuilder {

	protected static final DateFormat MONTH_FORMATTER = new SimpleDateFormat(DATE_MONTH_FORMAT);
	private static final DateFormat TRANSACTION_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	protected College college;
	protected Date from;
	protected Map<Long, Map<String, Object>> billingData;
	protected Map<Long, Map<String, Object>> licenseData;

	public AbstractExportLineBuilder(College college, Date from, Map<Long, Map<String, Object>> billingData, Map<Long, Map<String, Object>> licenseData) {
		this.college = college;
		this.from = from;
		this.billingData = billingData;
		this.licenseData = licenseData;
	}

	public String buildLine() {
		
		if (skipLine()) {
			return StringUtils.EMPTY;
		}
		
		return MessageFormat.format(
				Constants.TEMPLATE, 
				college.getBillingCode(), 
				getStockCode(),
				buildDetailedDescription(),
				getQuantity(),
				getUnitPrice(), 
				getDescription(),
				getTransactionDateString());
	}

	protected abstract String buildDetailedDescription();
	
	protected abstract String getStockCode();
	
	protected abstract Long getQuantity();

	protected abstract BigDecimal getUnitPrice();
	
	protected abstract boolean skipLine();

	protected String getDescription() {
		return String.format("onCourse %s", MONTH_FORMATTER.format(from));
	}

	protected String getTransactionDateString() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		return TRANSACTION_DATE_FORMAT.format(cal.getTime());
	}
}
