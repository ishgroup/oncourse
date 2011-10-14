package ish.oncourse.admin.pages;

import ish.math.Country;
import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.model.College;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.system.ICollegeService;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

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

	@Inject
	private ICollegeService collegeService;

	@Inject
	private IBillingDataService billingService;

	private boolean isExport;

	private NumberFormat moneyFormat;

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
	Object submitted() {

		if (isExport) {

		} else {

		}

		return this;
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
		calendar.setTime(fromMonth);
		Integer renewMonth = (Integer) licenseData.get(college.getId()).get("support-renewMonth");
		if (renewMonth != null) {
			int month = calendar.get(Calendar.MONTH);
			return renewMonth.equals(month);
		}
		return false;

	}

	public String getSupportRenew() {
		return String.valueOf(licenseData.get(college.getId()).get("support-renewMonth"));
	}

	public String getSupportFree() {
		return String.valueOf(licenseData.get(college.getId()).get("support-free"));
	}

	public String getSupport() {
		return String.valueOf(licenseData.get(college.getId()).get("support"));
	}

	public String getWebHostingFree() {
		return String.valueOf(licenseData.get(college.getId()).get("hosting-free"));
	}

	public String getHosting() {
		return String.valueOf(licenseData.get(college.getId()).get("hosting"));
	}

	public boolean isWebHostingRenewMonth() {
		calendar.setTime(fromMonth);
		Integer renewMonth = (Integer) licenseData.get(college.getId()).get("hosting-renewMonth");
		if (renewMonth != null) {
			return renewMonth.equals(calendar.get(Calendar.MONTH));
		}
		return false;
	}

	public String getWebHostingRenew() {
		return String.valueOf(licenseData.get(college.getId()).get("hosting-renewMonth"));
	}

	public String getSms() {
		return String.valueOf(billingData.get(college.getId()).get("sms"));
	}

	public String getSmsFree() {
		return String.valueOf(licenseData.get(college.getId()).get("sms-free"));
	}

	public String getSmsCollege() {
		return String.valueOf(licenseData.get(college.getId()).get("sms"));
	}

	public String getCcOffice() {
		return String.valueOf(billingData.get(college.getId()).get("ccOffice"));
	}

	public String getCcOfficeFree() {
		return String.valueOf(licenseData.get(college.getId()).get("cc-office-free"));
	}

	public String getCcOfficeCollege() {
		return String.valueOf(licenseData.get(college.getId()).get("ccOffice"));
	}

	public String getCcWeb() {
		return String.valueOf(billingData.get(college.getId()).get("ccWeb"));
	}

	public String getCcWebFree() {
		return String.valueOf(licenseData.get(college.getId()).get("cc-web-free"));
	}

	public String getCcWebCollege() {
		return String.valueOf(licenseData.get(college.getId()).get("cc-web"));
	}

	public String getCcWebValue() {
		return String.valueOf(billingData.get(college.getId()).get("ccWebValue"));
	}

	public String getEcommerceFree() {
		return String.valueOf(licenseData.get(college.getId()).get("ecommerce-free"));
	}

	public String getEcommerce() {
		return String.valueOf(licenseData.get(college.getId()).get("ecommerce"));
	}

	public String getSmsTotal() {
		return "";
	}

	public String getCcOfficeTotal() {
		return "";
	}

	public String getEcommerceTotal() {
		return "";
	}

	public String getCcWebTotal() {
		return "";
	}
}
