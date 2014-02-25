package ish.oncourse.admin.pages;

import ish.oncourse.admin.pages.college.Billing;
import ish.oncourse.admin.utils.LicenseFeeUtil;
import ish.oncourse.admin.utils.PreferenceUtil;
import ish.oncourse.model.College;
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
	
	private void initializeLicenseFees(ObjectContext context, College college) {
		LicenseFeeUtil.createFee(context, college, null, LicenseFeeUtil.SMS_FEE_CODE);
		LicenseFeeUtil.createFee(context, college, null, LicenseFeeUtil.CC_OFFICE_FEE_CODE);
		LicenseFeeUtil.createFee(context, college, null, LicenseFeeUtil.SUPPORT_FEE_CODE);
	}
	
	private void initializeLicensePreferences(ObjectContext context, College college) {
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_ACCESS_CONTROL, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_LDAP, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_BUDGET, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_EXTENRNAL_DB, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_SSL, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_SMS, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_CC_PROCESSING, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_PAYROLL, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_WEBSITE, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_VOUCHER, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_MEMBERSHIP, String.valueOf(true));
		PreferenceUtil.createPreference(context, college, PreferenceController.LICENSE_ATTENDANCE, String.valueOf(true));
	}
}
