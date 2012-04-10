package ish.oncourse.admin.pages.college;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.model.Preference;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.system.ICollegeService;
import ish.persistence.CommonPreferenceController;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Billing {
	
	@Property
	@Persist
	private College college;
	
	@Property
	@Persist
	private Map<String, Map<String, String>> licenseInfo;
	
	@Property
	private String currentLicenseInfoKey;
	
	@Property
	private String infoKey;
	
	@Property
	private boolean webPaymentEnabled;
	
	@Property
	private boolean qePaymentEnabled;
	
	@Property
	private boolean amexEnabled;
	
	@Property
	@Persist
	private StringSelectModel monthModel;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private PreferenceControllerFactory prefsFactory;
	
	@Persist
	private PreferenceController preferenceController;
	
	private SimpleDateFormat dateFormat;
	
	@Persist
	private String[] months;
	
	@SetupRender
	void setupRender() {
		this.preferenceController = prefsFactory.getPreferenceController(college);
		this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		this.months = new DateFormatSymbols().getMonths();
		this.monthModel = new StringSelectModel(months);
		
		if (!PaymentGatewayType.PAYMENT_EXPRESS.equals(preferenceController.getPaymentGatewayType())) {
			this.webPaymentEnabled = false;
		}
		else {
			this.webPaymentEnabled = true;
		}
		this.qePaymentEnabled = preferenceController.getLicenseCCProcessing();
		
		this.licenseInfo = new LinkedHashMap<String, Map<String,String>>();
		for (LicenseFee fee : college.getLicenseFees()) {
			Map<String, String> info = new LinkedHashMap<String, String>();
			
			info.put(LicenseFee.PLAN_NAME_PROPERTY, fee.getPlanName());
			
			if (fee.getBillingMonth() != null) {
				info.put(LicenseFee.BILLING_MONTH_PROPERTY, String.valueOf(fee.getBillingMonth()));
			}
			else {
				info.put(LicenseFee.BILLING_MONTH_PROPERTY, "");
			}
			
			info.put(LicenseFee.FREE_TRANSACTIONS_PROPERTY, String.valueOf(fee.getFreeTransactions()));
			info.put(LicenseFee.FEE_PROPERTY, String.valueOf(fee.getFee()));
			
			this.licenseInfo.put(fee.getKeyCode(), info);
		}
		
		ObjectContext context = cayenneService.sharedContext();
		Expression exp = ExpressionFactory.matchExp(Preference.COLLEGE_PROPERTY, college);
		List<Preference> prefs = context.performQuery(new SelectQuery(Preference.class, exp));
		for (Preference p : prefs) {
			if (CommonPreferenceController.SERVICES_CC_AMEX_ENABLED.equals(p.getName())) {
				this.amexEnabled = Boolean.parseBoolean(p.getValueString());
				break;
			}
		}
	}
	
	public boolean getShowMonthsCombo() {
		if ("support".equals(currentLicenseInfoKey) || "hosting".equals(currentLicenseInfoKey)) {
			return true;
		} else {
			return false;
		}
	}
	
	@OnEvent(component="billingForm", value="success")
	void submitted() throws Exception {
		this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		ObjectContext context = cayenneService.newContext();
		
		College col = (College) context.localObject(college.getObjectId(), null);
		if (col != null) {
			col.setPaymentGatewayAccount(college.getPaymentGatewayAccount());
			col.setPaymentGatewayPass(college.getPaymentGatewayPass());
			
			if (this.webPaymentEnabled) {
				preferenceController.setPaymentGatewayType(PaymentGatewayType.PAYMENT_EXPRESS);
			}
			else {
				preferenceController.setPaymentGatewayType(PaymentGatewayType.DISABLED);
			}
			preferenceController.setLicenseCCProcessing(this.qePaymentEnabled);
			
			boolean found = false;
			for (Preference p : col.getPreferences()) {
				if (CommonPreferenceController.SERVICES_CC_AMEX_ENABLED.equals(p.getName())) {
					p.setValueString(Boolean.toString(this.amexEnabled));
					found = true;
					break;
				}
			}
			if (!found) {
				Date now = new Date();
				Preference p = context.newObject(Preference.class);
				p.setCollege(col);
				p.setName(CommonPreferenceController.SERVICES_CC_AMEX_ENABLED);
				p.setValueString(Boolean.toString(this.amexEnabled));
				p.setCreated(now);
				p.setModified(now);
			}
		}
		
		for (LicenseFee fee : college.getLicenseFees()) {
			Map<String, String> info = licenseInfo.get(fee.getKeyCode());
			LicenseFee lf = (LicenseFee) context.localObject(fee.getObjectId(), null);
			
			if (info != null && lf != null) {
				if (StringUtils.trimToNull(info.get(LicenseFee.PLAN_NAME_PROPERTY)) != null) {
					lf.setPlanName(info.get(LicenseFee.PLAN_NAME_PROPERTY));
				}
				if ("support".equals(fee.getKeyCode()) || "hosting".equals(fee.getKeyCode())) {
					if (StringUtils.trimToNull(info.get(LicenseFee.BILLING_MONTH_PROPERTY)) != null) {
						lf.setBillingMonth(Integer.parseInt(info.get(LicenseFee.BILLING_MONTH_PROPERTY)));
					}
				}
				if (StringUtils.trimToNull(info.get(LicenseFee.FREE_TRANSACTIONS_PROPERTY)) != null) {
					lf.setFreeTransactions(Integer.parseInt(info.get(LicenseFee.FREE_TRANSACTIONS_PROPERTY)));
				}
				if (StringUtils.trimToNull(info.get(LicenseFee.FEE_PROPERTY)) != null) {
					lf.setFee(new BigDecimal(info.get(LicenseFee.FEE_PROPERTY)));
				}
			}
		}
		
		context.commitChanges();
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

	public String getPaymentExpUser() {
		return college.getPaymentGatewayAccount();
	}
	
	public void setPaymentExpUser(String value) {
		college.setPaymentGatewayAccount(value);
	}
	
	public String getPaymentExpPass() {
		return college.getPaymentGatewayPass();
	}
	
	public void setPaymentExpPass(String value) {
		college.setPaymentGatewayPass(value);
	}
	
	public Map<String, String> getCurrentLicenseInfo() {
		return licenseInfo.get(currentLicenseInfoKey);
	}
	
	public String getPlanName() {
		return getCurrentLicenseInfo().get(LicenseFee.PLAN_NAME_PROPERTY);
	}
	
	public void setPlanName(String planName) {
		getCurrentLicenseInfo().put(LicenseFee.PLAN_NAME_PROPERTY, planName);
	}
	
	public String getBillingMonth() {
		try {
			int monthNumber = Integer.parseInt(getCurrentLicenseInfo().get(LicenseFee.BILLING_MONTH_PROPERTY));
			return this.months[monthNumber];
		} catch (Exception e) {
			return null;
		}
	}
	
	public void setBillingMonth(String billingMonth) {
		String result = null;
		for (int i = 0; i < months.length; i++) {
			if (months[i].equals(billingMonth)) {
				result = String.valueOf(i);
				break;
			}
		}
		if (result != null && !"".equals(result)) {
			getCurrentLicenseInfo().put(LicenseFee.BILLING_MONTH_PROPERTY, result);
		}
	}
	
	public String getFreeTransactions() {
		return getCurrentLicenseInfo().get(LicenseFee.FREE_TRANSACTIONS_PROPERTY);
	}
	
	public void setFreeTransactions(String freeTransactions) {
		getCurrentLicenseInfo().put(LicenseFee.FREE_TRANSACTIONS_PROPERTY, freeTransactions);
	}
	
	public String getFee() {
		return getCurrentLicenseInfo().get(LicenseFee.FEE_PROPERTY);
	}
	
	public void setFee(String fee) {
		getCurrentLicenseInfo().put(LicenseFee.FEE_PROPERTY, fee);
	}
}
