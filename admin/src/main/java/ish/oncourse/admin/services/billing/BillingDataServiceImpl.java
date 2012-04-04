package ish.oncourse.admin.services.billing;

import ish.math.Country;
import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BillingDataServiceImpl implements IBillingDataService {
	
	private final String DATE_MONTH_FORMAT = "MMMMM, yyyy";

    private static final String SQL_LICENSE_FEE = "SELECT l.college_id as collegeId, l.key_code as keyCode, l.fee as fee, l.billingMonth as billingMonth, l.plan_name as plan, l.free_transactions as freeTransactions FROM LicenseFee as l JOIN College as c on c.id = l.college_id WHERE c.billingCode IS NOT NULL";
    private static final String SQL_SMS = "SELECT count(*) as count, c.id as collegeId FROM MessagePerson AS m JOIN College AS c on c.Id = m.collegeId WHERE c.billingCode IS NOT NULL and type = 2 AND timeOfDelivery >= #bind($from)  AND timeOfDelivery <= #bind($to) GROUP BY collegeid";
    private static final String SQL_OFFICE_TRANSACTION_COUNT = "SELECT count(*) as count, c.id as collegeId FROM PaymentIn As p JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND source = 'O' AND p.type = 2 NOT NULL AND (status = 3 OR status = 6) GROUP BY collegeid";
    private static final String SQL_WEB_TRANSACTION_COUNT = "SELECT count(*) as count, c.id as collegeId FROM PaymentIn As p JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND source = 'W' AND p.type = 2 IS NOT NULL AND (status = 3 OR status = 6) GROUP BY collegeid";
    private static final String SQL_WEB_TRANSACTION_VALUE = "SELECT c.id as collegeId, SUM(i.totalGst) as value FROM Invoice i JOIN College AS c on c.Id = i.collegeId WHERE c.billingCode IS NOT NULL and i.created >= #bind($from) AND i.created <= #bind($to) AND i.source = 'W' GROUP BY collegeid";
    private static final String SQL_TASMANIA_ECOMMERCE = "SELECT SUM(i.totalGst) as value FROM Invoice i WHERE i.created >= #bind($from) AND i.created <= #bind($to) AND i.collegeId = 15 AND i.source = 'W'";

    @Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeService collegeService;

	@Override
	public Map<Long, Map<String, Object>> getLicenseFeeData() {

		Map<Long, Map<String, Object>> data = new HashMap<Long, Map<String, Object>>();

		for (College college : collegeService.allColleges()) {
			if (!data.containsKey(college.getId())) {
				data.put(college.getId(), new HashMap<String, Object>());
			}
		}

		SQLTemplate query = new SQLTemplate(LicenseFee.class, SQL_LICENSE_FEE);
		query.setFetchingDataRows(true);

		List<DataRow> result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");

			Map<String, Object> billingRow = data.get(collegeId);

			String key = (String) r.get("keyCode");
			billingRow.put(key, r.get("fee"));
			billingRow.put(key + "-renewMonth", r.get("billingMonth"));
			billingRow.put(key + "-plan", r.get("plan"));

			Integer freeTransactions = (Integer) r.get("freeTransactions");

			if (freeTransactions != null && freeTransactions.intValue() != 0) {
				billingRow.put(key + "-free", freeTransactions);
			}
		}

		return data;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Map<String, Object>> getBillingData(Date from, Date to) {

		Map<Long, Map<String, Object>> data = new HashMap<Long, Map<String, Object>>();
		
		for (College college : collegeService.allColleges()) {
			if (!data.containsKey(college.getId())) {
				data.put(college.getId(), new HashMap<String, Object>());
			}
		}
		
		// SMS
		SQLTemplate query = new SQLTemplate(MessagePerson.class, SQL_SMS);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("from", from);
		params.put("to", to);

		query.setParameters(params);
		query.setFetchingDataRows(true);

		List<DataRow> result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("sms", r.get("count"));
		}

		// office transaction count
		query = new SQLTemplate(PaymentIn.class, SQL_OFFICE_TRANSACTION_COUNT);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("ccOffice", r.get("count"));
		}

		// web transaction count
		query = new SQLTemplate(PaymentIn.class, SQL_WEB_TRANSACTION_COUNT);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("ccWeb", r.get("count"));
		}

		// web transaction value
		query = new SQLTemplate(PaymentIn.class, SQL_WEB_TRANSACTION_VALUE);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("ccWebValue", r.get("value"));
		}

		// tasmania ecommerce
		Long tasmaniaId = new Long(15);
		
		if (data.get(tasmaniaId) != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(to);
			
			if (cal.get(Calendar.MONTH) == 1) {
				data.get(tasmaniaId).put("tasmaniaYearToDate", new BigDecimal(0.0));
			}
			else {
				Map<String, Object> tasmaniaParams = new HashMap<String, Object>();
				
				cal.setTime(from);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				tasmaniaParams.put("from", cal.getTime());
				cal.setTime(to);
				cal.add(Calendar.MONTH, -1);
				tasmaniaParams.put("to", cal.getTime());
				
				query = new SQLTemplate(PaymentIn.class, SQL_TASMANIA_ECOMMERCE);
				query.setParameters(tasmaniaParams);
				query.setFetchingDataRows(true);
				
				result = cayenneService.sharedContext().performQuery(query);
				
				BigDecimal value = (BigDecimal) result.get(0).get("value");
				
				if (value != null) {
					data.get(tasmaniaId).put("tasmaniaYearToDate", value);
				}
			}
		}
		
		return data;
	}
	
	public String getBillingDataExport(List<College> colleges, Date month) {
		String exportData = "Type\tNameCode\tDetail.StockCode\tDetail.Description\tDetail.StockQty\tDetail.UnitPrice\tDescription\n";
		for (College college : colleges) {
			exportData += buildMWExport(college, month);
		}
		
		return exportData;
	}
	
	private String buildMWExport(College college, Date month) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		Date from = cal.getTime();
		
		cal.add(Calendar.MONTH, 1);
		
		Date to = cal.getTime();
		
		Map<Long, Map<String, Object>> billingData = getBillingData(from, to);
		Map<Long, Map<String, Object>> licenseData = getLicenseFeeData();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
		String monthAndYear = dateFormat.format(from);
		String description = "onCourse " + monthAndYear;
		String text = "";
		
		NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
		moneyFormat.setMinimumFractionDigits(2);
		
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_MONTH_FORMAT);
		Date renewalDate = from;
		cal.setTime(renewalDate);
		cal.add(Calendar.YEAR, 1);
		renewalDate = cal.getTime();
		
		if (isSupportRenewMonth(college, from, licenseData)) {	
			text += "DI\t" +
					college.getBillingCode() + "\t" +
					getProductCode(String.valueOf(licenseData.get(college.getId()).get("support-plan"))) + "\t" +
					"onCourse " + licenseData.get(college.getId()).get("support-plan") + 
					" support plan, 1 year valid to " + formatter.format(renewalDate) + "\t" +
					"1\t" +
					licenseData.get(college.getId()).get("support") + "\t" +
					description + "\n";
		}
		
		if (isWebHostingRenewMonth(college, from, licenseData)) {
			text += "DI\t" +
					college.getBillingCode() + "\t" +
					getProductCode(String.valueOf(licenseData.get(college.getId()).get("hosting-plan"))) + "\t" +
					"onCourse " + licenseData.get(college.getId()).get("hosting-plan") + 
					" hosting plan, 1 year valid to " + formatter.format(renewalDate) + "\t" +
					"1\t" +
					licenseData.get(college.getId()).get("support") + "\t" +
					description + "\n";
		}
		
		DecimalFormat decimalFormatter = new DecimalFormat();
		decimalFormatter.setRoundingMode(RoundingMode.valueOf(2));
		
		Long sms = (Long) billingData.get(college.getId()).get("sms");
		sms = sms == null ? 0 : sms;
		
		text += "DI\t" +
				college.getBillingCode() + "\t" +
				"ON-SMS" + "\t" +
				"SMS usage for " + monthAndYear + "\t" +
				sms + "\t" +
				licenseData.get(college.getId()).get("sms") + "\t" +
				description + "\n";
		
		Long ccWeb = (Long) billingData.get(college.getId()).get("ccWeb");
		BigDecimal ccWebFree = (BigDecimal) licenseData.get(college.getId()).get("cc-web");
		ccWeb = ccWeb == null ? 0 : ccWeb;
		ccWebFree = ccWebFree == null ? new BigDecimal(0.0) : ccWebFree;
		
		text += "DI\t" +
				college.getBillingCode() + "\t" +
				"ON-CC-TRANS" + "\t" +
				"onCourse online credit card transaction fee for " + monthAndYear + "\t" +
				ccWeb + "\t" + ccWebFree + "\t" +
				description + "\n";
		
		text += "DI\t" +
				college.getBillingCode() + "\t" +
				"ON-NWEB-CC" + "\t" +
				"onCourse office credit card transaction fee for " + monthAndYear;
		
		Integer ccOfficeFree = (Integer) licenseData.get(college.getId()).get("cc-office-free");
		Long ccOffice = (Long) billingData.get(college.getId()).get("ccOffice");
		ccOfficeFree = ccOfficeFree == null ? 0 : ccOfficeFree;
		ccOffice = ccOffice == null ? 0 : ccOffice;
		
		if (ccOfficeFree > 0) {
			text += " (" + ccOffice + " less " + ccOfficeFree + " free transactions)";
		}
		text += "\t" + Math.max(0, ccOffice - ccOfficeFree) + "\t" + 
				licenseData.get(college.getId()).get("cc-office") + 
				"\t" + description +"\n";
		
		if (college.getId().longValue() == 15) {
			
			// Tasmania ecommerce
			
			Map<Double, Double> tasmaniaEcommerceFees = getTasmaniaFees(college, billingData);
			
			double totalFee = 0.0;
			
			for (Double key : tasmaniaEcommerceFees.keySet()) {
				text += "DI\t" +
						college.getBillingCode() + "\t" +
						"ON-ECOM-PERC" + "\t" +
						"onCourse eCommerce fee at " + decimalFormatter.format(key) + "% of " +
						moneyFormat.format(tasmaniaEcommerceFees.get(key)) + " for " + monthAndYear + "\t" +
						"1\t";
				double fee = tasmaniaEcommerceFees.get(key) * key / 100;
				totalFee += fee;
				
				text += decimalFormatter.format(fee) + "\t" +
						description + "\n";
			}
			
			if (totalFee < 375) {
				text += "DI\t" + 
						college.getBillingCode() + "\t" +
						"ON-ECOM-PERC" + "\t" +
						"Adjustment for onCourse eCommerce minimum monthly fee of $375.\t" +
						"1\t" +
						decimalFormatter.format(375 - totalFee) + "\t" +
						description + "\n";
			}
		}
		else {
			BigDecimal ecommerce = (BigDecimal) licenseData.get(college.getId()).get("ecommerce");
			BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get("ccWebValue");
			ecommerce = ecommerce == null ? new BigDecimal(0.0) : ecommerce;
			ccWebValue = ccWebValue ==null ? new BigDecimal(0.0) : ccWebValue;
			text += "DI\t" +
					college.getBillingCode() + "\t" +
					"ON-ECOM-PERC" + "\t" +
					"onCourse eCommerce fee at " + (ecommerce.doubleValue() * 100) + "% of " +
					moneyFormat.format(ccWebValue) + " for " + monthAndYear + "\t" + "1\t" +
					decimalFormatter.format(ccWebValue.multiply(ecommerce)) + "\t" +
					description + "\n";
		}
		
		return text;
	}
	
	private boolean isSupportRenewMonth(College college, Date fromMonth, Map<Long, Map<String, Object>> licenseData) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromMonth);
		Integer renewMonth = (Integer) licenseData.get(college.getId()).get("support-renewMonth");
		if (renewMonth != null) {
			int month = calendar.get(Calendar.MONTH);
			return renewMonth.equals(month);
		}
		return false;

	}
	
	private boolean isWebHostingRenewMonth(College college, Date fromMonth, Map<Long, Map<String, Object>> licenseData) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromMonth);
		Integer renewMonth = (Integer) licenseData.get(college.getId()).get("hosting-renewMonth");
		if (renewMonth != null) {
			return renewMonth.equals(calendar.get(Calendar.MONTH));
		}
		return false;
	}
	
	private Map<Double, Double> getTasmaniaFees(College college, Map<Long, Map<String, Object>> billingData) {
		if (college.getId() == 15) {
			BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get("ccWebValue");
			ccWebValue = ccWebValue == null ? new BigDecimal(0.0) : ccWebValue;
			return getTasmaniaEcommerce(ccWebValue.doubleValue(), billingData);
		} else {
			return null;
		}
	}
	
	public Map<Double, Double> getTasmaniaEcommerce(double thisMonth, Map<Long, Map<String, Object>> billingData) {
		
		Map<Double, Double> fees = new HashMap<Double, Double>();
		fees.put(3.50, 100000.0);
		fees.put(3.00, 100000.0);
		fees.put(1.80, 200000.0);
		fees.put(1.25, 400000.0);
		fees.put(1.00, 800000.0);
		fees.put(0.95, 1600000.0);
		fees.put(0.90, 9999999999.0);
		
		Map<Double, Double> result = new HashMap<Double, Double>();
		
		BigDecimal tasmaniaYearToDate = (BigDecimal) billingData.get(new Long(15)).get("tasmaniaYearToDate");
		double yearToDate = tasmaniaYearToDate == null ? 0 : tasmaniaYearToDate.doubleValue();
		
		for (Double key : fees.keySet()) {
			if (yearToDate > 0) {
				double deductThisIteration = Math.min(yearToDate, fees.get(key));
				fees.put(key, fees.get(key) - deductThisIteration);
				yearToDate -= deductThisIteration;
			}
		}
		
		for (Double key : fees.keySet()) {
			if (fees.get(key) > 0 && thisMonth > 0) {
				result.put(key, Math.min(thisMonth, fees.get(key)));
				thisMonth -= fees.get(key);
			}
		}
		
		return result;
	}
	
	private String getProductCode(String name) {
		if ("light".equals(name)) {
			return "OC-LIGHT";
		}
		else if ("professional".equals(name)) {
			return "OC-11";
		}
		else if ("enterprise".equals(name)) {
			return "OC-10";
		}
		else if ("starter".equals(name)) {
			return "OCW-20";
		}
		else if ("standard".equals(name)) {
			return "OCW-21";
		}
		else if ("premium".equals(name)) {
			return "OCW-22";
		}
		else if ("platinum".equals(name)) {
			return "OCW-23";
		}
		else {
			return null;
		}
	}
}
