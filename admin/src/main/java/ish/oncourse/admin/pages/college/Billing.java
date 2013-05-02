package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.pages.Index;
import ish.oncourse.admin.utils.LicenseFeeUtil;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.system.ICollegeService;
import ish.persistence.CommonPreferenceController;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Billing {

	private static final Logger logger = Logger.getLogger(Billing.class);

	@InjectComponent
	@Property
	private Form billingForm;

	@Property
	@Persist
	private College college;

	@Property
	private int webSiteIndex;

	@Property
	private int feeIndex;

	@Property
	@Persist
	private List<WebSite> webSites;

	@Property
	@Persist
	private List<LicenseFee> collegeLicenseFees;

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
		ObjectContext context = cayenneService.newContext();

		this.college = context.localObject(college);

		this.preferenceController = prefsFactory.getPreferenceController(college);

		if (!PaymentGatewayType.PAYMENT_EXPRESS.equals(preferenceController.getPaymentGatewayType())) {
			this.webPaymentEnabled = false;
		}
		else {
			this.webPaymentEnabled = true;
		}
		this.qePaymentEnabled = preferenceController.getLicenseCCProcessing();

		this.webSites = college.getWebSites();

		Expression collegeFeesExp = ExpressionFactory.matchExp(LicenseFee.WEB_SITE_PROPERTY, null);
		this.collegeLicenseFees = collegeFeesExp.filterObjects(college.getLicenseFees());
		Ordering.orderList(collegeLicenseFees, Arrays.asList(new Ordering(LicenseFee.KEY_CODE_PROPERTY, SortOrder.ASCENDING)));

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
		return LicenseFeeUtil.SUPPORT_FEE_CODE.equals(getCurrentLicenseFee().getKeyCode())
				|| LicenseFeeUtil.HOSTING_FEE_CODE.equals(getCurrentLicenseFee().getKeyCode());
	}

	@OnEvent(component = "billingForm", value = "validate")
	void validate() {

		for (LicenseFee fee : collegeLicenseFees) {
			if (isSupportFee(fee) || isHostingFee(fee)) {
				validateFee(fee);
			}
		}

		for (WebSite webSite : webSites) {
			for (LicenseFee fee : webSite.getLicenseFees()) {
				if (isSupportFee(fee) || isHostingFee(fee)) {
					validateFee(fee);
				}
			}
		}
	}

	private void validateFee(LicenseFee fee) {
		String planName = StringUtils.trimToNull(fee.getPlanName());
		Date renewalDate = fee.getRenewalDate();

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

		context.commitChanges();
	}

    void onActivate(Long id) {
		if (college == null) {
			this.college = collegeService.findById(id);
		}
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

	public void setPaidUntil(LicenseFee fee, String dateString) {
		if (dateString != null) {
			try {
				Date date = dateFormat.parse(dateString);
				if (date != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					fee.setPaidUntil(cal.getTime());
				}
			} catch (ParseException e) {
				billingForm.recordError("Paid until date not valid.");
			}
		} else {
			fee.setPaidUntil(null);
		}
	}
	
	public void setRenewalDate(LicenseFee fee, String dateString){
		if (dateString != null) {
			try {
				Date date = dateFormat.parse(dateString);
				if (date != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					fee.setRenewalDate(cal.getTime());
				}
			} catch (ParseException e) {
				billingForm.recordError("Renewal date not valid.");
			}
		} else {
			fee.setRenewalDate(null);
		}
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
        if (college == null) {
            return indexPage;
		} else {
			throw new IllegalStateException(cause);
		}
    }

	private boolean isSupportFee(LicenseFee fee) {
		return LicenseFeeUtil.SUPPORT_FEE_CODE.equals(fee.getKeyCode());
	}

	private boolean isHostingFee(LicenseFee fee) {
		return LicenseFeeUtil.HOSTING_FEE_CODE.equals(fee.getKeyCode());
	}

	public WebSite getCurrentWebsite() {
		return webSites.get(webSiteIndex);
	}

	public LicenseFee getCurrentLicenseFee() {
		return getCurrentWebsite().getLicenseFees().get(feeIndex);
	}

	public LicenseFee getCurrentCollegeLicenseFee() {
		return collegeLicenseFees.get(feeIndex);
	}

	public String getCurrentPaidUntil() {
		return getFeePaidUntil(getCurrentLicenseFee());
	}

	public void setCurrentPaidUntil(String paidUntilString) {
		setPaidUntil(getCurrentLicenseFee(), paidUntilString);
	}

	public String getCurrentRenewalDate() {
		return getFeeRenewalDate(getCurrentLicenseFee());
	}

	public void setCurrentRenewalDate(String renewalDateString) {
		setRenewalDate(getCurrentLicenseFee(), renewalDateString);
	}

	public String getCurrentCollegeFeePaidUntil() {
		return getFeePaidUntil(getCurrentCollegeLicenseFee());
	}

	public void setCurrentCollegeFeePaidUntil(String paidUntilString) {
		setPaidUntil(getCurrentCollegeLicenseFee(), paidUntilString);
	}

	public String getCurrentCollegeFeeRenewalDate() {
		return getFeeRenewalDate(getCurrentCollegeLicenseFee());
	}

	public void setCurrentCollegeFeeRenewalDate(String renewalDateString) {
		setRenewalDate(getCurrentCollegeLicenseFee(), renewalDateString);
	}

	private String getFeePaidUntil(LicenseFee fee) {
		Date paidUntil = getCurrentLicenseFee().getPaidUntil();
		if (paidUntil != null) {
			return dateFormat.format(paidUntil);
		}

		return StringUtils.EMPTY;
	}

	private String getFeeRenewalDate(LicenseFee fee) {
		Date renewalDate = getCurrentLicenseFee().getRenewalDate();
		if (renewalDate != null) {
			return dateFormat.format(renewalDate);
		}

		return StringUtils.EMPTY;
	}
}
