package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.pages.Index;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

public class Billing {

	private static final Logger logger = LogManager.getLogger();

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
	private boolean creditCardPaymentEnabled;

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
		
		this.creditCardPaymentEnabled = preferenceController.isCreditCardPaymentEnabled();

		this.webSites = college.getWebSites();

		List<Preference> prefs = ObjectSelect.query(Preference.class).where(Preference.COLLEGE.eq(college)).select(context);
	}

	@OnEvent(component="billingForm", value="success")
	void submitted() {
		ObjectContext context = college.getObjectContext();

		if (college != null) {
			college.setName(this.college.getName());

			if (this.webPaymentEnabled) {
				preferenceController.setPaymentGatewayType(PaymentGatewayType.PAYMENT_EXPRESS);
			}
			else {
				preferenceController.setPaymentGatewayType(PaymentGatewayType.DISABLED);
			}
			preferenceController.setLicenseCCProcessing(this.qePaymentEnabled);

			preferenceController.setCreditCardPaymentEnabled(creditCardPaymentEnabled);
		}

		context.commitChanges();
	}
	

    void onActivate(Long id) {
		if (college == null || !college.getId().equals(id)) {
			this.college = collegeService.findById(id);
		}
	}

	Object onPassivate() {
		return this.college.getId();
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

	public WebSite getCurrentWebsite() {
		return webSites.get(webSiteIndex);
	}

}
