package ish.oncourse.admin.pages;

import com.amazonaws.services.identitymanagement.model.AccessKey;
import ish.oncourse.admin.pages.college.Billing;
import ish.oncourse.admin.utils.LicenseFeeUtil;
import ish.oncourse.admin.utils.PreferenceUtil;
import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

public class AddCollege {

	private static final String BUCKET_NAME_FORMAT = "ish-oncourse-%s";
	private static final String AWS_USER_NAME_FORMAT = "college.%s";

	@Inject
	private IS3Service s3Service;
	
	@Property
	private String findCollegeUrl;
	
	@Property
	private Long collegeId;
	
	@Property
	private String billingCode;

	@Property
	private String collegeKey;
	
	@Property
	private String servicesPassword;

	@Inject
	@Property
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

	@OnEvent(value = "findCollegeEvent")
	public Object findCollege() {
		if (!request.isXHR()) {
			return null;
		}
		String serviceKey = request.getParameter("key");
		JSONObject response = new JSONObject();

		College college = collegeService.findBySecurityCodeLastChars(serviceKey);
		if (college != null) {
			if (college.getBillingCode() != null) {
				response.put("status", "ACTIVE");
				response.put("message", "College already active.");

			}
			else {
				response.put("status", "SETUP");
				response.put("message", "Found. Please fill following fields, then press Next to proceed to billing setup.");
				response.put("id", college.getId());
			}
		}
		else {
			response.put("status", "NOT FOUND");
			response.put("message", "Could not find key.");
		}

		return new TextStreamResponse("text/json", response.toString());
	}

	@OnEvent(value = "addBillingCodeAndCollegeKeyEvent")
	public Object addBillingCodeAndCollegeKey() {
		if (!request.isXHR()) {
			return null;
		}
		JSONObject response = new JSONObject();
		String collegeKey = request.getParameter("collegeKey");
		String billingCode = request.getParameter("billingCode");

		if ( billingCode.isEmpty() || collegeKey.isEmpty() ) {
			response.put("message", "Billing Code and College Key must not be empty");
			return new TextStreamResponse("text/json", response.toString());
		}

		if (collegeKey.endsWith(".") ||	collegeKey.contains(" ")) {
			response.put("message", "College key must not contain full stop and spaces");
			return new TextStreamResponse("text/json", response.toString());
		}

		Long collegeId = Long.valueOf(request.getParameter("collegeId"));

		ObjectContext context = cayenneService.newContext();
		College college = context.localObject(collegeService.findById(collegeId));

		if (college != null) {
			college.setBillingCode(billingCode);
			college.setCommunicationKey(null);
			college.setRequiresAvetmiss(true);
			collegeKey = collegeKey.toLowerCase();
			college.setCollegeKey(collegeKey);
			String bucketName = String.format(BUCKET_NAME_FORMAT, collegeKey);
			String userName = String.format(AWS_USER_NAME_FORMAT, collegeKey);

			try {
				enableExternalStorage(context, college, bucketName, userName);
			} catch (Exception e) {
				response.put("message", e.getMessage());
				return new TextStreamResponse("text/json", response.toString());
			}
		}

		initializeLicenseFees(context, college);
		initializeLicensePreferences(context, college);
		context.commitChanges();
		//it is return json which contains redirect link in redirectURL parameter
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

	private void enableExternalStorage(ObjectContext context, College college, String bucketName, String newUserName) {
		s3Service.createBucket(bucketName);
		AccessKey key = s3Service.createS3User(newUserName, bucketName);

		PreferenceUtil.createPreference(context, college, PreferenceController.STORAGE_BUCKET_NAME, bucketName);
		PreferenceUtil.createPreference(context, college, PreferenceController.STORAGE_ACCESS_ID, key.getAccessKeyId());
		PreferenceUtil.createPreference(context, college, PreferenceController.STORAGE_ACCESS_KEY, key.getSecretAccessKey());

		context.commitChanges();
	}
}
