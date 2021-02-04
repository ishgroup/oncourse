package ish.oncourse.admin.pages;

import com.amazonaws.services.identitymanagement.model.AccessKey;
import ish.oncourse.admin.pages.college.Billing;
import ish.oncourse.util.LicenseFeeUtil;
import ish.oncourse.util.PreferenceUtil;
import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.system.ICollegeService;
import ish.persistence.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

public class AddCollege {

	private static final String BUCKET_NAME_FORMAT = "ish-oncourse-%s";
	private static final String AWS_USER_NAME_FORMAT = "college.%s";
	private static final String KEY_PARAMETER_NAME = "key";
	private static final String COLLEGE_KEY_PARAMETER_NAME  = "collegeKey";
	private static final String BILLING_CODE_PARAMETER_NAME  = "billingCode";
	private static final String COLLEGE_ID_PARAMETER_NAME = "collegeId";
	private static final String MESSAGE_PARAMETER_NAME = "message";
	private static final String STATUS_PARAMETER_NAME = "needCollegeKey";
	private static final String CONTENT_TYPE = "text/json";

	@Inject
	private IS3Service s3Service;

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

	@OnEvent(value = "findCollegeEvent")
	public Object findCollege() {
		if (!request.isXHR()) {
			return null;
		}
		String serviceKey = request.getParameter(KEY_PARAMETER_NAME);
		JSONObject response = new JSONObject();
		response.put(STATUS_PARAMETER_NAME,false);

		College college = collegeService.findBySecurityCodeLastChars(serviceKey);
		if (college != null) {
			if (college.getBillingCode() != null) {
				response.put(MESSAGE_PARAMETER_NAME, "College already active.");
			}
			else {
				response.put(STATUS_PARAMETER_NAME, true);
				response.put(MESSAGE_PARAMETER_NAME, "Found. Please fill following fields, then press Next to proceed to billing setup.");
				response.put(COLLEGE_ID_PARAMETER_NAME, college.getId());
			}
		}
		else {
			response.put(MESSAGE_PARAMETER_NAME, "Could not find key.");
		}

		return new TextStreamResponse(CONTENT_TYPE, response.toString());
	}

	@OnEvent(value = "addBillingCodeAndCollegeKeyEvent")
	public Object addBillingCodeAndCollegeKey() {
		if (!request.isXHR()) {
			return null;
		}
		JSONObject response = new JSONObject();
		String collegeKey = request.getParameter(COLLEGE_KEY_PARAMETER_NAME);
		String billingCode = request.getParameter(BILLING_CODE_PARAMETER_NAME);

		if ( billingCode.isEmpty() || collegeKey.isEmpty() ) {
			response.put(MESSAGE_PARAMETER_NAME, "Billing Code and College Key must not be empty");
			return new TextStreamResponse(CONTENT_TYPE, response.toString());
		}

		if (collegeKey.endsWith(".") ||	collegeKey.contains(" ")) {
			response.put(MESSAGE_PARAMETER_NAME, "College key must not contain full stop and spaces");
			return new TextStreamResponse(CONTENT_TYPE, response.toString());
		}

		Long collegeId = Long.valueOf(request.getParameter(COLLEGE_ID_PARAMETER_NAME));

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
				response.put(MESSAGE_PARAMETER_NAME, e.getMessage());
				return new TextStreamResponse(CONTENT_TYPE, response.toString());
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
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_ACCESS_CONTROL, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_LDAP, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_BUDGET, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_EXTENRNAL_DB, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_SSL, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_SMS, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_CC_PROCESSING, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_PAYROLL, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_VOUCHER, String.valueOf(false));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_MEMBERSHIP, String.valueOf(true));
		PreferenceUtil.createPreference(context, college, Preferences.LICENSE_ATTENDANCE, String.valueOf(true));
	}

	private void enableExternalStorage(ObjectContext context, College college, String bucketName, String newUserName) {
		s3Service.createBucket(bucketName);
		AccessKey key = s3Service.createS3User(newUserName, bucketName);

		PreferenceUtil.createPreference(context, college, Preference.STORAGE_BUCKET_NAME, bucketName);
		PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_ID, key.getAccessKeyId());
		PreferenceUtil.createPreference(context, college, Preference.STORAGE_ACCESS_KEY, key.getSecretAccessKey());

		context.commitChanges();
	}
}
