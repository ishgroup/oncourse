package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.pages.Index;
import ish.oncourse.admin.services.billing.StockCodes;
import ish.oncourse.util.LicenseFeeUtil;
import ish.oncourse.util.PreferenceUtil;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.system.ICollegeService;
import ish.persistence.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectById;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Billing {

	private static final Logger logger = LogManager.getLogger();

	@InjectComponent
	@Property
	private Form billingForm;

	@InjectComponent
	@Property
	private Form newCustomFeeForm;
	
	@Property
	@Persist
	private College college;

	@Property
	private int webSiteIndex;

	@Property
	private int feeIndex;

	@Property
	private int customFeeIndex;

	@Property
	@Persist
	private List<WebSite> webSites;

	@Property
	@Persist
	private List<LicenseFee> collegeLicenseFees;

	@Property
	private String newCode;

	@Property
	private String newName;

	@Property
	private String newPaidUntil;

	@Property
	private BigDecimal newFee;

	@Property
	@Persist
	private List<CustomFee> collegeCustomFees;

	@Property
	private boolean webPaymentEnabled;
	
	@Property
	private boolean qePaymentEnabled;
	
	@Property
	private boolean amexEnabled;

	@Property
	private boolean replicationEnabled;

	@Property
	private boolean creditCardPaymentEnabled;

	@Property
	private boolean corporatePassPaymentEnabled;


	@Property
	@Persist
	private boolean showReplicationMessage;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private PreferenceControllerFactory prefsFactory;
	
	private PreferenceController preferenceController;

    @InjectPage
    private Index indexPage;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	Object onActivate() {
		this.preferenceController = prefsFactory.getPreferenceController(college);
		return null;
	}

	@SetupRender
	void setupRender() {

		ObjectContext context = cayenneService.newContext();

		this.college = context.localObject(college);

		if (!PaymentGatewayType.PAYMENT_EXPRESS.equals(preferenceController.getPaymentGatewayType())) {
			this.webPaymentEnabled = false;
		}
		else {
			this.webPaymentEnabled = true;
		}
		this.qePaymentEnabled = preferenceController.getLicenseCCProcessing();

		this.replicationEnabled = preferenceController.getReplicationEnabled();

		this.corporatePassPaymentEnabled = preferenceController.isCorporatePassPaymentEnabled();

		this.creditCardPaymentEnabled = preferenceController.isCreditCardPaymentEnabled();

		this.webSites = college.getWebSites();

		Expression collegeFeesExp = ExpressionFactory.matchExp(LicenseFee.WEB_SITE_PROPERTY, null);
		this.collegeLicenseFees = collegeFeesExp.filterObjects(college.getLicenseFees());
		Ordering.orderList(collegeLicenseFees, Arrays.asList(new Ordering(LicenseFee.KEY_CODE_PROPERTY, SortOrder.ASCENDING)));
		
		this.collegeCustomFees = college.getCustomFees();
		
		List<Preference> prefs = ObjectSelect.query(Preference.class).where(Preference.COLLEGE.eq(college)).select(context);
		for (Preference p : prefs) {
			if (Preferences.SERVICES_CC_AMEX_ENABLED.equals(p.getName())) {
				this.amexEnabled = Boolean.parseBoolean(p.getValueString());
				break;
			}
		}
	}

	public boolean isSupport() {
		return isSupportFee(getCurrentCollegeLicenseFee());
	}

	public boolean isHosting() {
		return isHostingFee(getCurrentLicenseFee());
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

			replicationEnable();
			preferenceController.setCorporatePassPaymentEnabled(corporatePassPaymentEnabled);
			preferenceController.setCreditCardPaymentEnabled(creditCardPaymentEnabled);

			boolean found = false;
			for (Preference p : college.getPreferences()) {
				if (Preferences.SERVICES_CC_AMEX_ENABLED.equals(p.getName())) {
					p.setValueString(Boolean.toString(this.amexEnabled));
					found = true;
					break;
				}
			}
			if (!found) {
				PreferenceUtil.createPreference(context, college,
						Preferences.SERVICES_CC_AMEX_ENABLED, Boolean.toString(this.amexEnabled));
			}
		}

		context.commitChanges();
	}

	private void replicationEnable()
	{
		boolean old = preferenceController.getReplicationEnabled();
		showReplicationMessage = old != replicationEnabled && replicationEnabled;
		preferenceController.setReplicationEnabled(replicationEnabled);
	}


    void onActivate(Long id) {
		if (college == null || !college.getId().equals(id)) {
			this.college = collegeService.findById(id);
		}
	}

	Object onPassivate() {
		return this.college.getId();
	}

	@AfterRender
	void afterRender()
	{
		showReplicationMessage = false;
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

	public String getBillingCode() {
		return college.getBillingCode();
	}

	public void setPurchaseOrder(String value) {
		college.setPurchaseOrder(value);
	}

	public String getPurchaseOrder() {
		return college.getPurchaseOrder();
	}

	public void setBillingCode(String value) {
		college.setBillingCode(value);
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

	public CustomFee getCurrentCustomFee() {
		return collegeCustomFees.get(customFeeIndex);
	}
	
	public String getCurrentPaidUntil() {
		return getFeePaidUntil(getCurrentLicenseFee());
	}

	public void setCurrentPaidUntil(String paidUntilString) {
		setPaidUntil(getCurrentLicenseFee(), paidUntilString);
	}

	public String getCurrentCollegeFeePaidUntil() {
		return getFeePaidUntil(getCurrentCollegeLicenseFee());
	}

	public void setCurrentCollegeFeePaidUntil(String paidUntilString) {
		setPaidUntil(getCurrentCollegeLicenseFee(), paidUntilString);
	}

	private String getFeePaidUntil(LicenseFee fee) {
		Date paidUntil = fee.getPaidUntil();
		if (paidUntil != null) {
			return dateFormat.format(paidUntil);
		}

		return StringUtils.EMPTY;
	}

	public String getCurrentPlanName()
	{
		return getCurrentLicenseFee().getPlanName();
	}

	public void setCurrentPlanName(String planName)
	{
		setPlanName(getCurrentLicenseFee(), planName);
	}


	public String getCurrentCollegePlanName()
	{
		return getCurrentCollegeLicenseFee().getPlanName();
	}

	public void setCurrentCollegePlanName(String planName)
	{
		setPlanName(getCurrentCollegeLicenseFee(), planName);
	}

	private void setPlanName(LicenseFee licenseFee, String planName)
	{
		if (planName != null)
		{
			try {
				StockCodes code = StockCodes.valueOf(StringUtils.trimToEmpty(planName).toLowerCase());
				planName = code.name();
			} catch (IllegalArgumentException e) {
				//billingForm.recordError(String.format("Plan name \"%s\" is not supported.",planName));
				planName = StockCodes.platinum.name();
			}
		}
		licenseFee.setPlanName(planName);
	}
	
	public String getCurrentCustomFeeCode() {
		return getCurrentCustomFee().getCode();
	}

	public void setCurrentCustomFeeCode(String code) {
		getCurrentCustomFee().setCode(code);
	}

	public String getCurrentCustomFeeName() {
		return getCurrentCustomFee().getName();
	}

	public void setCurrentCustomFeeName(String name) {
		getCurrentCustomFee().setName(name);
	}

	public String getCurrentCustomFeePauidUntil() {
		Date paidUntil = getCurrentCustomFee().getPaidUntil();
		if (paidUntil != null) {
			return dateFormat.format(paidUntil);
		}

		return StringUtils.EMPTY;
	}

	public void setCurrentCustomFeePauidUntil(String paidUntil) {
		if (paidUntil != null) {
			try {
				Date date = dateFormat.parse(paidUntil);
				if (date != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					getCurrentCustomFee().setPaidUntil(cal.getTime());
				}
			} catch (ParseException e) {
				billingForm.recordError("Paid until date not valid.");
			}
		} else {
			getCurrentCustomFee().setPaidUntil(null);
		}
	}

	Object onActionFromDeleteCustomFee(Long id) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		CustomFee customFee = SelectById.query(CustomFee.class, id).selectOne(context);
		

		context.deleteObjects(customFee);
		context.commitChanges();
		return null;
	}

	@OnEvent(component = "newCustomFeeForm", value = "validate")
	void validateCustomFee() {

		if (newPaidUntil != null) {
			try {
				Date date = dateFormat.parse(newPaidUntil);
			} catch (ParseException e) {
				newCustomFeeForm.recordError("Paid until date not valid.");
			}
		}
	}
	
	@OnEvent(component="newCustomFeeForm", value="success")
	void addCustomFee() throws ParseException {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		CustomFee customFee = context.newObject(CustomFee.class);
		customFee.setCollege(context.localObject(college));

		customFee.setCode(newCode);
		customFee.setName(newName);
		if (newPaidUntil != null) {
			customFee.setPaidUntil(dateFormat.parse(newPaidUntil));
		}
		customFee.setFee(newFee);
		context.commitChanges();
	}

}
