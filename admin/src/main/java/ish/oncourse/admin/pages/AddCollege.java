package ish.oncourse.admin.pages;

import java.math.BigDecimal;

import ish.oncourse.admin.pages.college.Billing;
import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class AddCollege {
	
	@Property
	private String findCollegeUrl;
	
	@Property
	private Long collegeId;
	
	@Property
	private String billingCode;
	
	@Property
	private String servicesPassword;
	
	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private PageRenderLinkSource prls;
	
	@SetupRender
	void setupRender() {
		this.findCollegeUrl = response.encodeURL(request.getContextPath() + "/findCollege");
	}
	
	@OnEvent(component="newCollegeForm", value="success")
	Object newCollege() {
		ObjectContext context = cayenneService.newContext();
		
		College college = (College) context.localObject(collegeService.findById(collegeId).getObjectId(), null);
		if (college != null) {
			college.setBillingCode(billingCode);
			college.setCommunicationKey(null);
			college.setRequiresAvetmiss(true);
		}
		
		initializeLicenseFees(context, college);
		initializeLicensePreferences(context, college);
		
		context.commitChanges();
		
		return prls.createPageRenderLinkWithContext(Billing.class, collegeId);
	}
	
	private LicenseFee createFee(ObjectContext context, College college, String keyCode) {
		LicenseFee fee = context.newObject(LicenseFee.class);
		fee.setCollege(college);
		fee.setKeyCode(keyCode);
		fee.setFreeTransactions(0);
		fee.setFee(new BigDecimal(0));
		return fee;
	}
	
	private Preference createPreference(ObjectContext context, College college, String name, String value) {
		Preference pref = context.newObject(Preference.class);
		
		pref.setCollege(college);
		pref.setName(name);
		pref.setValueString(value);
		
		return pref;
	}
	
	private void initializeLicenseFees(ObjectContext context, College college) {
		createFee(context, college, "sms");
		createFee(context, college, "cc-office");
		createFee(context, college, "cc-web");
		createFee(context, college, "ecommerce");
		createFee(context, college, "support");
		createFee(context, college, "hosting");
	}
	
	private void initializeLicensePreferences(ObjectContext context, College college) {
		createPreference(context, college, PreferenceController.LICENSE_ACCESS_CONTROL, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_LDAP, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_BUDGET, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_EXTENRNAL_DB, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_SSL, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_SMS, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_CC_PROCESSING, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_PAYROLL, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_WEBSITE, String.valueOf(false));
		createPreference(context, college, PreferenceController.LICENSE_VOUCHER, String.valueOf(false));
	}
}
