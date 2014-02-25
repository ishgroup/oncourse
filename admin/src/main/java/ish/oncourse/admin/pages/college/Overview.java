package ish.oncourse.admin.pages.college;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.amazonaws.services.identitymanagement.model.AccessKey;
import ish.oncourse.admin.utils.PreferenceUtil;
import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Overview {
	
	private static final String BUCKET_NAME_TEMPLATE = "%s-ish-oncourse";
	
	private static final String[] LICENSE_KEYS = new String[] {
		PreferenceController.LICENSE_ACCESS_CONTROL,
		PreferenceController.LICENSE_LDAP,
		PreferenceController.LICENSE_BUDGET,
		PreferenceController.LICENSE_EXTENRNAL_DB,
		PreferenceController.LICENSE_SSL,
		PreferenceController.LICENSE_SMS,
		PreferenceController.LICENSE_CC_PROCESSING,
		PreferenceController.LICENSE_PAYROLL,
		PreferenceController.LICENSE_WEBSITE,
		PreferenceController.LICENSE_VOUCHER
	};
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IS3Service s3Service;
	
	@Property
	private College college;
	
	@Property
	private String lastIP;
	
	@Property
	private String onCourseVersion;
	
	@Property
	private String replicationState;
	
	@Property
	private String lastReplication;
	
	@SetupRender
	void setupRender() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		lastIP = college.getIpAddress();
		onCourseVersion = college.getAngelVersion();
		replicationState = college.getCommunicationKeyStatus().toString();
		lastReplication = dateFormat.format(college.getLastRemoteAuthentication());
	}
	
	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
	}
	
	Object onActionFromDisableCollege() {
		
		ObjectContext context = cayenneService.newContext();
		College college = (College) context.localObject(this.college.getObjectId(), null);
		
		disableCollege(context, college);
		
		return "Index";
	}
	
	Object onActionFromEnableExternalStorage() {
		ObjectContext context = cayenneService.newContext();
		College college = context.localObject(this.college);

		enableExternalStorage(context, college);

		return null;
	}

	private void disableCollege(ObjectContext context, College college) {
		college.setBillingCode(null);
		
		Expression licensePrefsExp = ExpressionFactory.matchExp(Preference.COLLEGE_PROPERTY, college)
				.andExp(ExpressionFactory.inExp(Preference.NAME_PROPERTY, (Object[]) LICENSE_KEYS));
		Expression replicationPrefExp = ExpressionFactory.matchExp(Preference.COLLEGE_PROPERTY, college)
				.andExp(ExpressionFactory.matchExp(Preference.NAME_PROPERTY, PreferenceController.REPLICATION_ENABLED));
		
		SelectQuery licenseQuery = new SelectQuery(Preference.class, licensePrefsExp);
		List<Preference> licensePrefs = context.performQuery(licenseQuery);
		
		for (Preference pref : licensePrefs) {
			pref.setValueString(Boolean.toString(false));
		}
		
		SelectQuery relicationPrefQuery = new SelectQuery(Preference.class, replicationPrefExp);
		Preference replicationPref = (Preference) Cayenne.objectForQuery(context, relicationPrefQuery);
		
		if (replicationPref != null) {
			replicationPref.setValueString(Boolean.toString(false));
		}
		
		context.commitChanges();
	}

	private void enableExternalStorage(ObjectContext context, College college) {
		String bucketName = String.format(BUCKET_NAME_TEMPLATE, StringUtils.lowerCase(college.getName()));
		s3Service.createBucket(bucketName);
		AccessKey key = s3Service.createS3User(college.getName(), bucketName);

		PreferenceUtil.createPreference(context, college, PreferenceController.STORAGE_BUCKET_NAME, bucketName);
		PreferenceUtil.createPreference(context, college, PreferenceController.STORAGE_ACCESS_ID, key.getAccessKeyId());
		PreferenceUtil.createPreference(context, college, PreferenceController.STORAGE_ACCESS_KEY, key.getSecretAccessKey());
		
		context.commitChanges();
	}
	
	public String getBucketName() {
		Preference bucketPref = PreferenceUtil.getPreference(
				college.getObjectContext(), college, PreferenceController.STORAGE_BUCKET_NAME);
		if (bucketPref == null || StringUtils.trimToNull(bucketPref.getValueString()) == null) {
			return null;
		}
		return bucketPref.getValueString();
	}
}
