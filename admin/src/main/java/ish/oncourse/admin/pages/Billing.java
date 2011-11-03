package ish.oncourse.admin.pages;

import ish.math.Country;
import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.model.College;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.system.ICollegeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

public class Billing {

	private final String DATE_MONTH_FORMAT = "MMMMM, yyyy";

	private final int NUMBER_OF_MONTHS = 24;

	@Property
	@Persist
	private String month;

	@Property
	@Persist
	private Date fromMonth;

	@Property
	private Calendar calendar;

	@Property
	@Persist
	private List<College> colleges;

	@Property
	private College college;

	@Property
	@Persist
	private Map<Long, Map<String, Object>> licenseData;

	@Property
	@Persist
	private Map<Long, Map<String, Object>> billingData;

	@Property
	@Persist
	private StringSelectModel monthModel;
	
	@Property
	private Object currentKey;

	@Inject
	private ICollegeService collegeService;

	@Inject
	private IBillingDataService billingService;

	private boolean isExport;

	private NumberFormat moneyFormat;
	
	private Map<Double, Double> tasmaniaEcommerceFees;

	@SetupRender
	void setupRender() throws Exception {

		this.monthModel = initMonthModel();

		colleges = collegeService.allColleges();

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_MONTH_FORMAT);
		fromMonth = formatter.parse(month);

		calendar = Calendar.getInstance();
		calendar.setTime(fromMonth);
		calendar.add(Calendar.MONTH, 1);

		licenseData = billingService.getLicenseFeeData();
		billingData = billingService.getBillingData(fromMonth, calendar.getTime());

		moneyFormat = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
		moneyFormat.setMinimumFractionDigits(2);
	}

	@OnEvent(component = "export", value = "selected")
	void onSelectedFromExport() {
		this.isExport = true;
	}

	@OnEvent(component = "billingForm", value = "success")
	Object submitted() throws Exception {

		if (isExport) {
			String exportData = "Type\tNameCode\tDetail.StockCode\tDetail.Description\tDetail.Qty\tDetail.UnitPrice\tDescription\n";
			for (College college : colleges) {
				exportData += buildMWExport(college);
			}
			
			return new TextStreamResponse("text/plain", exportData) {
				
				@Override
				public void prepareResponse(Response response) {
					response.setHeader("Content-Disposition", "attachment; filename=onCourseBilling.txt");
				}
			};
		} 
		
		return this;
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

	private StringSelectModel initMonthModel() {

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_MONTH_FORMAT);

		Calendar cal = Calendar.getInstance();

		String[] months = new String[NUMBER_OF_MONTHS];

		for (int i = 0; i < NUMBER_OF_MONTHS; i++) {
			cal.add(Calendar.MONTH, -1);
			months[i] = formatter.format(cal.getTime());
		}
		
		if (this.month == null) {
			this.month = months[0];
		}
		
		return new StringSelectModel(months);
	}

	public boolean isTasmaniaEcommerce() {
		return college.getId().longValue() == 15;
	}

	public boolean isSupportRenewMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromMonth);
		Integer renewMonth = (Integer) licenseData.get(college.getId()).get("support-renewMonth");
		if (renewMonth != null) {
			int month = calendar.get(Calendar.MONTH);
			return renewMonth.equals(month);
		}
		return false;

	}

	public String getSupportRenew() {
		Object supportRenew = licenseData.get(college.getId()).get("support-renewMonth");
		return supportRenew == null ? "" : DateFormatSymbols.getInstance().getMonths()[(Integer) supportRenew];
	}

	public String getSupportFree() {
		Object supportFree = licenseData.get(college.getId()).get("support-free");
		return supportFree == null ? "" : String.valueOf(supportFree);
	}

	public String getSupport() {
		Object support = licenseData.get(college.getId()).get("support");
		return support == null ? moneyFormat.format(0.0) : moneyFormat.format(support);
	}

	public String getWebHostingFree() {
		Object webHostingFree = licenseData.get(college.getId()).get("support-free");
		return webHostingFree == null ? "" : String.valueOf(webHostingFree);
	}

	public String getHosting() {
		Object hosting = licenseData.get(college.getId()).get("hosting");
		return hosting == null ? moneyFormat.format(0.0) : moneyFormat.format(hosting);
	}

	public boolean isWebHostingRenewMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromMonth);
		Integer renewMonth = (Integer) licenseData.get(college.getId()).get("hosting-renewMonth");
		if (renewMonth != null) {
			return renewMonth.equals(calendar.get(Calendar.MONTH));
		}
		return false;
	}

	public String getWebHostingRenew() {
		Object webHostingRenew = licenseData.get(college.getId()).get("hosting-renewMonth");
		return webHostingRenew == null ? "" : 
				DateFormatSymbols.getInstance().getMonths()[(Integer) webHostingRenew];
	}

	public String getSms() {
		Object sms = billingData.get(college.getId()).get("sms");
		return sms == null ? "" : String.valueOf(sms);
	}

	public String getSmsFree() {
		Object smsFree = licenseData.get(college.getId()).get("sms-free");
		return smsFree == null ? "" : String.valueOf(smsFree);
	}

	public String getSmsCollege() {
		Object smsCollege = licenseData.get(college.getId()).get("sms");
		return smsCollege == null ? moneyFormat.format(0.0) : moneyFormat.format(smsCollege);
	}

	public String getCcOffice() {
		Object ccOffice = billingData.get(college.getId()).get("ccOffice");
		return ccOffice == null ? "" : String.valueOf(ccOffice);
	}

	public String getCcOfficeFree() {
		Object ccOfficeFree = licenseData.get(college.getId()).get("cc-office-free");
		return ccOfficeFree == null ? "" : String.valueOf(ccOfficeFree);
	}

	public String getCcOfficeCollege() {
		Object ccOfficeCollege = licenseData.get(college.getId()).get("cc-office");
		return ccOfficeCollege == null ? moneyFormat.format(0.0) : moneyFormat.format(ccOfficeCollege);
	}

	public String getCcWeb() {
		Object ccWeb = billingData.get(college.getId()).get("ccWeb");
		return ccWeb == null ? "" : String.valueOf(ccWeb);
	}

	public String getCcWebFree() {
		Object ccWebFree = licenseData.get(college.getId()).get("cc-web-free");
		return ccWebFree == null ? "" : String.valueOf(ccWebFree);
	}

	public String getCcWebCollege() {
		Object ccWebCollege = licenseData.get(college.getId()).get("cc-web");
		return ccWebCollege == null ? moneyFormat.format(0.0) : moneyFormat.format(ccWebCollege);
	}

	public String getCcWebValue() {
		Object ccWebValue = billingData.get(college.getId()).get("ccWebValue");
		return ccWebValue == null ? moneyFormat.format(0.0) : moneyFormat.format(ccWebValue);
	}

	public String getEcommerceFree() {
		Object ecommerceFree = licenseData.get(college.getId()).get("ecommerce-free");
		return ecommerceFree == null ? "" : String.valueOf(ecommerceFree);
	}

	public String getEcommerce() {
		Object ecommerce = licenseData.get(college.getId()).get("ecommerce");
		return ecommerce == null ? "0.0" : 
				String.valueOf(((BigDecimal) ecommerce).doubleValue() * 100);
	}

	public String getSmsTotal() {
		Object sms = billingData.get(college.getId()).get("sms");
		Object smsCost = licenseData.get(college.getId()).get("sms");
		if (sms != null && smsCost != null) {
			return moneyFormat.format((Long) sms * ((BigDecimal) smsCost).doubleValue());
		}
		else {
			return moneyFormat.format(0.0);
		}
	}

	public String getCcOfficeTotal() {
		Object ccOffice = billingData.get(college.getId()).get("ccOffice");
		Object ccOfficeCost = licenseData.get(college.getId()).get("cc-office");
		if (ccOffice != null && ccOfficeCost != null) {
			return moneyFormat.format((Long) ccOffice * ((BigDecimal) ccOfficeCost).doubleValue());
		}
		else {
			return moneyFormat.format(0.0);
		}
	}
	
	public String getCcWebTotal() {
		Object ccWeb = billingData.get(college.getId()).get("ccWeb");
		Object ccWebCost = licenseData.get(college.getId()).get("cc-web");
		if (ccWeb != null && ccWebCost != null) {
			return moneyFormat.format((Long) ccWeb * ((BigDecimal) ccWebCost).doubleValue());
		}
		else {
			return moneyFormat.format(0.0);
		}
	}

	public String getEcommerceTotal() {
		Object ecommerce = licenseData.get(college.getId()).get("ecommerce");
		Object ccWebValue = billingData.get(college.getId()).get("ccWebValue");
		if (ecommerce != null && ccWebValue != null) {
			return moneyFormat.format(
					((BigDecimal) ecommerce).multiply((BigDecimal) ccWebValue));
		}

		return moneyFormat.format(0.0);
	}
	
	public Map<Double, Double> getTasmaniaFees() {
		if (isTasmaniaEcommerce()) {
			BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get("ccWebValue");
			ccWebValue = ccWebValue == null ? new BigDecimal(0.0) : ccWebValue;
			tasmaniaEcommerceFees = getTasmaniaEcommerce(ccWebValue.doubleValue());
		}
		return tasmaniaEcommerceFees;
	}
	
	public String getTasmaniaFee() {
		return moneyFormat.format(tasmaniaEcommerceFees.get(currentKey));
	}
	
	public String getTasmaniaTotal() {
		return moneyFormat.format((Double)currentKey / 100 * tasmaniaEcommerceFees.get(currentKey));
	}
	
	private Map<Double, Double> getTasmaniaEcommerce(double thisMonth) {
		
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
	
	private String buildMWExport(College college) throws Exception {
		String monthAndYear = this.month.replace(",", "");
		String description = "onCourse " + monthAndYear;
		String text = "";
		
		NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
		moneyFormat.setMinimumFractionDigits(2);
		
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_MONTH_FORMAT);
		Date renewalDate = formatter.parse(month);
		Calendar cal = Calendar.getInstance();
		cal.setTime(renewalDate);
		cal.add(Calendar.YEAR, 1);
		renewalDate = cal.getTime();
		
		if (isSupportRenewMonth()) {	
			text += "DI\t" +
					college.getBillingCode() + "\t" +
					getProductCode(String.valueOf(billingData.get(college.getId()).get("support-plan"))) + "\t" +
					"onCourse " + billingData.get(college.getId()).get("support-plan") + 
					"support plan, 1 year valid to " + formatter.format(renewalDate) + "\t" +
					"1\t" +
					billingData.get(college.getId()).get("support") + "\t" +
					description + "\n";
		}
		
		if (isWebHostingRenewMonth()) {
			text += "DI\t" +
					college.getBillingCode() + "\t" +
					getProductCode(String.valueOf(billingData.get(college.getId()).get("hosting-plan"))) + "\t" +
					"onCourse " + billingData.get(college.getId()).get("hosting-plan") + 
					"hosting plan, 1 year valid to " + formatter.format(renewalDate) + "\t" +
					"1\t" +
					billingData.get(college.getId()).get("support") + "\t" +
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
				"onCourse office credit card transaction fee for " + monthAndYear + "\t";
		
		Integer ccOfficeFree = (Integer) licenseData.get(college.getId()).get("cc-office-free");
		Long ccOffice = (Long) billingData.get(college.getId()).get("ccOffice");
		ccOfficeFree = ccOfficeFree == null ? 0 : ccOfficeFree;
		ccOffice = ccOffice == null ? 0 : ccOffice;
		
		if (ccOfficeFree > 0) {
			text += "(" + ccOffice + " less " + ccOfficeFree + " free transactions)";
		}
		text += "\t" + Math.max(0, ccOffice - ccOfficeFree) + "\t" + 
				licenseData.get(college.getId()).get("cc-office") + 
				"\t" + description +"\n";
		
		if (isTasmaniaEcommerce()) {
			getTasmaniaFees();
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
					moneyFormat.format(ccWebValue) + " for " + monthAndYear + "1\t" +
					decimalFormatter.format(ccWebValue.multiply(ecommerce)) + "\t" +
					description + "\n";
		}
		
		return text;
	}
}
