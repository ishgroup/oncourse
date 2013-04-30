package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.pages.Index;
import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.model.Preference;
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
import org.apache.log4j.Logger;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Billing {

	private static final Logger logger = Logger.getLogger(Billing.class);

	private static final String SUPPORT_FEE_CODE = "support";
	private static final String HOSTING_FEE_CODE = "hosting";
	
	@InjectComponent
	@Property
	private Form billingForm;

	@Property
	private College college;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Map<String, Map<String, Object>> licenseInfo;
	
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
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private PreferenceControllerFactory prefsFactory;
	
	@Persist
	private PreferenceController preferenceController;

    @InjectPage
    private Index indexPage;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@SetupRender
	void setupRender() {
		ObjectContext context = cayenneService.sharedContext();

		this.college = context.localObject(college);

		this.preferenceController = prefsFactory.getPreferenceController(college);

		if (!PaymentGatewayType.PAYMENT_EXPRESS.equals(preferenceController.getPaymentGatewayType())) {
			this.webPaymentEnabled = false;
		}
		else {
			this.webPaymentEnabled = true;
		}
		this.qePaymentEnabled = preferenceController.getLicenseCCProcessing();
		
		this.licenseInfo = new LinkedHashMap<>();
		for (LicenseFee fee : college.getLicenseFees()) {
			Map<String, Object> info = new LinkedHashMap<>();
			
			info.put(LicenseFee.PLAN_NAME_PROPERTY, fee.getPlanName());
			
			if (fee.getPaidUntil() != null) {
				info.put(LicenseFee.PAID_UNTIL_PROPERTY, fee.getPaidUntil());
			}
			
			if (fee.getRenewalDate() != null) {
				info.put(LicenseFee.RENEWAL_DATE_PROPERTY, fee.getRenewalDate());
			}
			
			info.put(LicenseFee.FREE_TRANSACTIONS_PROPERTY, String.valueOf(fee.getFreeTransactions()));
			info.put(LicenseFee.FEE_PROPERTY, String.valueOf(fee.getFee()));
			
			this.licenseInfo.put(fee.getKeyCode(), info);
		}

		Expression exp = ExpressionFactory.matchExp(Preference.COLLEGE_PROPERTY, college);
		List<Preference> prefs = context.performQuery(new SelectQuery(Preference.class, exp));
		for (Preference p : prefs) {
			if (CommonPreferenceController.SERVICES_CC_AMEX_ENABLED.equals(p.getName())) {
				this.amexEnabled = Boolean.parseBoolean(p.getValueString());
				break;
			}
		}
	}
	
	public boolean isSupportOrHosting() {
		if ("support".equals(currentLicenseInfoKey) || "hosting".equals(currentLicenseInfoKey)) {
			return true;
		} else {
			return false;
		}
	}

	@OnEvent(component = "billingForm", value = "validate")
	void validate() {
		if (licenseInfo.get(SUPPORT_FEE_CODE) != null) {
			validateFeeInfo(licenseInfo.get(SUPPORT_FEE_CODE));
		}

		if (licenseInfo.get(HOSTING_FEE_CODE) != null) {
			validateFeeInfo(licenseInfo.get(HOSTING_FEE_CODE));
		}
	}

	private void validateFeeInfo(Map<String, Object> feeInfo) {
		String planName = StringUtils.trimToNull((String) feeInfo.get(LicenseFee.PLAN_NAME_PROPERTY));
		Date renewalDate = (Date) feeInfo.get(LicenseFee.RENEWAL_DATE_PROPERTY);

		if (planName != null && renewalDate == null) {
			billingForm.recordError("Renewal date must be specified for all active plans.");
		}
	}
	
	@OnEvent(component="billingForm", value="success")
	void submitted() {
		ObjectContext context = college.getObjectContext();

		if (college != null) {
			college.setPaymentGatewayAccount(this.college.getPaymentGatewayAccount());
			college.setPaymentGatewayPass(this.college.getPaymentGatewayPass());
			college.setName(this.college.getName());
			
			if (this.webPaymentEnabled) {
				preferenceController.setPaymentGatewayType(PaymentGatewayType.PAYMENT_EXPRESS);
			}
			else {
				preferenceController.setPaymentGatewayType(PaymentGatewayType.DISABLED);
			}
			preferenceController.setLicenseCCProcessing(this.qePaymentEnabled);
			
			boolean found = false;
			for (Preference p : college.getPreferences()) {
				if (CommonPreferenceController.SERVICES_CC_AMEX_ENABLED.equals(p.getName())) {
					p.setValueString(Boolean.toString(this.amexEnabled));
					found = true;
					break;
				}
			}
			if (!found) {
				Date now = new Date();
				Preference p = context.newObject(Preference.class);
				p.setCollege(college);
				p.setName(CommonPreferenceController.SERVICES_CC_AMEX_ENABLED);
				p.setValueString(Boolean.toString(this.amexEnabled));
				p.setCreated(now);
				p.setModified(now);
			}
		}
		
		for (LicenseFee fee : this.college.getLicenseFees()) {
			Map<String, Object> info = licenseInfo.get(fee.getKeyCode());
			LicenseFee lf = context.localObject(fee);
			
			if (info != null && lf != null) {
                String planName = StringUtils.trimToNull(StringUtils.trimToNull((String) info.get(LicenseFee.PLAN_NAME_PROPERTY)));
                lf.setPlanName(planName);
                if (planName == null) {
                    lf.setPaidUntil(null);
                    lf.setRenewalDate(null);
                }
                else {
                    if (isSupportFee(fee) || isHostingFee(fee)) {
                        lf.setPaidUntil((Date) info.get(LicenseFee.PAID_UNTIL_PROPERTY));
                        lf.setRenewalDate((Date) info.get(LicenseFee.RENEWAL_DATE_PROPERTY));
                    }
                }


				if (StringUtils.trimToNull((String) info.get(LicenseFee.FREE_TRANSACTIONS_PROPERTY)) != null) {
					lf.setFreeTransactions(Integer.parseInt((String) info.get(LicenseFee.FREE_TRANSACTIONS_PROPERTY)));
				}
				if (StringUtils.trimToNull((String) info.get(LicenseFee.FEE_PROPERTY)) != null) {
					lf.setFee(new BigDecimal((String) info.get(LicenseFee.FEE_PROPERTY)));
				}
			}
		}
		
		context.commitChanges();
	}

    void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return this.college.getId();
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
	
	public Map<String, Object> getCurrentLicenseInfo() {
		return licenseInfo.get(currentLicenseInfoKey);
	}
	
	public String getPlanName() {
		return (String) getCurrentLicenseInfo().get(LicenseFee.PLAN_NAME_PROPERTY);
	}
	
	public void setPlanName(String planName) {
		getCurrentLicenseInfo().put(LicenseFee.PLAN_NAME_PROPERTY, planName);
	}
	
	public String getPaidUntil() {
		Date paidUntil = (Date) getCurrentLicenseInfo().get(LicenseFee.PAID_UNTIL_PROPERTY);
		return paidUntil == null ? "" : dateFormat.format(paidUntil);
	}
	
	public void setPaidUntil(String dateString) {
		if (dateString != null) {
			try {
				Date date = dateFormat.parse(dateString);
				if (date != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					getCurrentLicenseInfo().put(LicenseFee.PAID_UNTIL_PROPERTY, cal.getTime());
				}
			} catch (ParseException e) {
				billingForm.recordError("Paid until date not valid.");
			}
		} else {
			getCurrentLicenseInfo().put(LicenseFee.PAID_UNTIL_PROPERTY, null);
		}
	}
	
	public String getRenewalDate() {
		Date renewalDate = (Date) getCurrentLicenseInfo().get(LicenseFee.RENEWAL_DATE_PROPERTY);
		return renewalDate == null ? "" : dateFormat.format(renewalDate);
	}
	
	public void setRenewalDate(String dateString){
		if (dateString != null) {
			try {
				Date date = dateFormat.parse(dateString);
				if (date != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					getCurrentLicenseInfo().put(LicenseFee.RENEWAL_DATE_PROPERTY, cal.getTime());
				}
			} catch (ParseException e) {
				billingForm.recordError("Renewal date not valid.");
			}
		} else {
			getCurrentLicenseInfo().put(LicenseFee.RENEWAL_DATE_PROPERTY, null);
		}
	}
	
	public String getFreeTransactions() {
		return (String) getCurrentLicenseInfo().get(LicenseFee.FREE_TRANSACTIONS_PROPERTY);
	}
	
	public void setFreeTransactions(String freeTransactions) {
		getCurrentLicenseInfo().put(LicenseFee.FREE_TRANSACTIONS_PROPERTY, freeTransactions);
	}
	
	public String getFee() {
		return (String) getCurrentLicenseInfo().get(LicenseFee.FEE_PROPERTY);
	}
	
	public void setFee(String fee) {
		getCurrentLicenseInfo().put(LicenseFee.FEE_PROPERTY, fee);
	}
	
	public String getCollegeName() {
		return college.getName();
	}
	
	public void setCollegeName(String collegeName) {
		if (StringUtils.trimToNull(collegeName) != null) {
			college.setName(collegeName);
		}
	}

    public Object onException(Throwable cause){
        //redirect to index page when session was expired and persist properties got null value
        if (college == null || licenseInfo == null) {
            return indexPage;
		} else {
			throw new IllegalStateException(cause);
		}
    }

	private boolean isSupportFee(LicenseFee fee) {
		return SUPPORT_FEE_CODE.equals(fee.getKeyCode());
	}

	private boolean isHostingFee(LicenseFee fee) {
		return HOSTING_FEE_CODE.equals(fee.getKeyCode());
	}

}
