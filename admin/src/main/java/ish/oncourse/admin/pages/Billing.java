package ish.oncourse.admin.pages;

import ish.math.Country;
import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private int webSiteIndex;

	@Property
	@Persist
	private Map<Long, Map<Long, Map<String, Object>>> licenseData;

	@Property
	@Persist
	private Map<Long, Map<Long, Map<String, Object>>> billingData;

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
	
	private DateFormat dateFormat;

	@SetupRender
	void setupRender() throws Exception {

		this.monthModel = initMonthModel();
		this.dateFormat = new SimpleDateFormat(DATE_MONTH_FORMAT);

		colleges = collegeService.allColleges();
		
		Ordering.orderList(colleges, Arrays.asList(new Ordering(College.NAME_PROPERTY, SortOrder.ASCENDING_INSENSITIVE)));

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
			String exportData = billingService.getBillingDataExport(colleges, fromMonth);
			
			return new TextStreamResponse("text/plain", exportData) {
				
				@Override
				public void prepareResponse(Response response) {
					response.setHeader("Content-Disposition", "attachment; filename=onCourseBilling.txt");
				}
			};
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
		return college.getId() == 15;
	}
	
	public boolean isSupportPayMonth() {
		String plan = (String) licenseData.get(college.getId()).get(null).get("support-plan");
		Date paidUntil = (Date) licenseData.get(college.getId()).get(null).get("support-paidUntil");
		if (plan != null) {
			if (paidUntil == null) {
				return true;
			}
			if (DateUtils.truncatedCompareTo(paidUntil, fromMonth, Calendar.MONTH) <= 0) {
				return true;
			}
		}
		
		return false;
	}

	public String getSupportPaidUntil() {
		Object supportPaidUntil = licenseData.get(college.getId()).get(null).get("support-paidUntil");
		return supportPaidUntil == null ? "" : dateFormat.format(supportPaidUntil);
	}

	public String getSupportFree() {
		Object supportFree = licenseData.get(college.getId()).get(null).get("support-free");
		return supportFree == null ? "" : String.valueOf(supportFree);
	}

	public String getSupport() {
		Object support = licenseData.get(college.getId()).get(null).get("support");
		return support == null ? moneyFormat.format(0.0) : moneyFormat.format(support);
	}

	public String getSms() {
		Object sms = billingData.get(college.getId()).get(null).get("sms");
		return sms == null ? "" : String.valueOf(sms);
	}

	public String getSmsFree() {
		Object smsFree = licenseData.get(college.getId()).get(null).get("sms-free");
		return smsFree == null ? "" : String.valueOf(smsFree);
	}

	public String getSmsCollege() {
		Object smsCollege = licenseData.get(college.getId()).get(null).get("sms");
		return smsCollege == null ? moneyFormat.format(0.0) : moneyFormat.format(smsCollege);
	}

	public String getSmsTotal() {
		Object sms = billingData.get(college.getId()).get(null).get("sms");
		Object smsCost = licenseData.get(college.getId()).get(null).get("sms");
		if (sms != null && smsCost != null) {
			return moneyFormat.format((Long) sms * ((BigDecimal) smsCost).doubleValue());
		}
		else {
			return moneyFormat.format(0.0);
		}
	}

	public String getWebHostingFree() {
		Object webHostingFree = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("hosting-free");
		return webHostingFree == null ? "" : String.valueOf(webHostingFree);
	}

	public String getHosting() {
		Object hosting = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("hosting");
		return hosting == null ? moneyFormat.format(0.0) : moneyFormat.format(hosting);
	}

	public boolean isWebHostingPayMonth() {
		String plan = (String) licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("hosting-plan");
		Date paidUntil = (Date) licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("hosting-paidUntil");
		if (plan != null) {
			if (paidUntil == null) {
				return true;
			}

			if (DateUtils.truncatedCompareTo(paidUntil, fromMonth, Calendar.MONTH) <= 0) {
				return true;
			}
		}

		return false;
	}

	public String getWebHostingPaidUntil() {
		Object webHostingPaidUntil = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("hosting-paidUntil");
		return webHostingPaidUntil == null ? "" : dateFormat.format(webHostingPaidUntil);
	}

	public String getCcOffice() {
		Object ccOffice = billingData.get(college.getId()).get(null).get("ccOffice");
		return ccOffice == null ? "" : String.valueOf(ccOffice);
	}

	public String getCcOfficeFree() {
		Object ccOfficeFree = licenseData.get(college.getId()).get(null).get("cc-office-free");
		return ccOfficeFree == null ? "" : String.valueOf(ccOfficeFree);
	}

	public String getCcOfficeCollege() {
		Object ccOfficeCollege = licenseData.get(college.getId()).get(null).get("cc-office");
		return ccOfficeCollege == null ? moneyFormat.format(0.0) : moneyFormat.format(ccOfficeCollege);
	}

	public String getCcOfficeTotal() {
		Long ccOffice = (Long) billingData.get(college.getId()).get(null).get("ccOffice");
		BigDecimal ccOfficeCost = (BigDecimal) licenseData.get(college.getId()).get(null).get("cc-office");
		Integer ccOfficeFree = (Integer) licenseData.get(college.getId()).get(null).get("cc-office-free");
		if (ccOffice != null && ccOfficeCost != null) {
			if (ccOfficeFree == null) {
				ccOfficeFree = 0;
			}

			if (ccOfficeFree < ccOffice) {
				return moneyFormat.format((ccOffice - ccOfficeFree) * ccOfficeCost.doubleValue());
			}
			else {
				return moneyFormat.format(0.0);
			}
		}
		else {
			return moneyFormat.format(0.0);
		}
	}


	public String getCcWeb() {
		Object ccWeb = billingData.get(college.getId()).get(getCurrentWebSiteId()).get("ccWeb");
		return ccWeb == null ? "" : String.valueOf(ccWeb);
	}

	public String getCcWebFree() {
		Object ccWebFree = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("cc-web-free");
		return ccWebFree == null ? "" : String.valueOf(ccWebFree);
	}

	public String getCcWebCollege() {
		Object ccWebCollege = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("cc-web");
		return ccWebCollege == null ? moneyFormat.format(0.0) : moneyFormat.format(ccWebCollege);
	}

	public String getCcWebValue() {
		Object ccWebValue = billingData.get(college.getId()).get(getCurrentWebSiteId()).get("ccWebValue");
		return ccWebValue == null ? moneyFormat.format(0.0) : moneyFormat.format(ccWebValue);
	}

	public String getCcWebTotal() {
		Object ccWeb = billingData.get(college.getId()).get(getCurrentWebSiteId()).get("ccWeb");
		Object ccWebCost = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("cc-web");
		if (ccWeb != null && ccWebCost != null) {
			return moneyFormat.format((Long) ccWeb * ((BigDecimal) ccWebCost).doubleValue());
		}
		else {
			return moneyFormat.format(0.0);
		}
	}

	public String getEcommerceFree() {
		Object ecommerceFree = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("ecommerce-free");
		return ecommerceFree == null ? "" : String.valueOf(ecommerceFree);
	}

	public String getEcommerce() {
		Object ecommerce = licenseData.get(college.getId()).get(getCurrentWebSiteId()).get("ecommerce");
		return ecommerce == null ? "0.0" : 
				String.valueOf(((BigDecimal) ecommerce).doubleValue() * 100);
	}

	public String getEcommerceTotal() {
        Long id = college.getId();
		Object ecommerce = licenseData.get(id).get("ecommerce");
		Object ccWebValue = billingData.get(id).get("ccWebValue");
		if (ecommerce != null && ccWebValue != null) {
			return moneyFormat.format(
					((BigDecimal) ecommerce).multiply((BigDecimal) ccWebValue));
		}

		return moneyFormat.format(0.0);
	}
	
	public Map<Double, Double> getTasmaniaFees() {
		if (isTasmaniaEcommerce()) {
			BigDecimal ccWebValue = (BigDecimal) billingData.get(college.getId()).get(
					getCurrentWebSiteId()).get("ccWebValue");
			ccWebValue = ccWebValue == null ? new BigDecimal(0.0) : ccWebValue;
			tasmaniaEcommerceFees = billingService.getTasmaniaEcommerce(ccWebValue.doubleValue(), billingData);
		}
		return tasmaniaEcommerceFees;
	}
	
	public String getTasmaniaFee() {
		return moneyFormat.format(tasmaniaEcommerceFees.get(currentKey));
	}
	
	public String getTasmaniaTotal() {
		return moneyFormat.format((Double)currentKey / 100 * tasmaniaEcommerceFees.get(currentKey));
	}

	public WebSite getCurrentWebsite() {
		List<WebSite> webSites = college.getWebSites();
		if (webSiteIndex < webSites.size()) {
			return college.getWebSites().get(webSiteIndex);
		}
		return null;
	}

	private Long getCurrentWebSiteId() {
		WebSite currentWebsite = getCurrentWebsite();
		return currentWebsite != null ? currentWebsite.getId() : null;
	}
	
}
