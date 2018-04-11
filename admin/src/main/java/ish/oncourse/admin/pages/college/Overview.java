package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.utils.AUSKeyUtil;
import ish.oncourse.admin.utils.PreferenceUtil;
import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.usi.crypto.CryptoUtils;
import ish.persistence.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.bouncycastle.util.encoders.Base64;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Overview {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static final List<String> LICENSE_KEYS = Arrays.asList(
			Preferences.LICENSE_ACCESS_CONTROL,
			Preferences.LICENSE_LDAP,
			Preferences.LICENSE_BUDGET,
			Preferences.LICENSE_EXTENRNAL_DB,
			Preferences.LICENSE_SSL,
			Preferences.LICENSE_SMS,
			Preferences.LICENSE_CC_PROCESSING,
			Preferences.LICENSE_PAYROLL,
			Preferences.LICENSE_VOUCHER,
			Preferences.LICENSE_FUNDING_CONTRACT);
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;

	@InjectComponent
	@Property
	private Form ausKeyForm;
	
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
	
	@Property
	private UploadedFile auskeyFile;
	
	@Property
	private String auskeyPassword;

	@Property
	private String collegeKey;
	
	@SetupRender
	void setupRender() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		lastIP = college.getIpAddress();
		onCourseVersion = college.getAngelVersion();
		replicationState = college.getCommunicationKeyStatus().toString();
		lastReplication = dateFormat.format(college.getLastRemoteAuthentication());
		collegeKey = college.getCollegeKey();
	}
	
	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
	}
	
	Object onActionFromDisableCollege() {
		
		ObjectContext context = cayenneService.newContext();
		College college = context.localObject(this.college);
		
		disableCollege(context, college);
		
		return "Index";
	}

	@OnEvent(component = "ausKeyForm", value="success")
	public void uploadAusKey() {
		ausKeyForm.clearErrors();
		
		if (StringUtils.trimToNull(auskeyPassword) == null) {
			ausKeyForm.recordError("Password cannot be empty.");
			return;
		}

		AUSKeyUtil.AUSKey ausKey = null;
		
		try {
			ausKey = AUSKeyUtil.parseKeystoreXml(auskeyFile.getStream());
		} catch (Exception e) {
			logger.info(e);
		}
		
		if (ausKey == null || !ausKey.isFilled()) {
			ausKeyForm.recordError("Keystore file cannot be parsed.");
			return;
		}

		try {
			CryptoUtils.decryptPrivateKey(Base64.decode(ausKey.getPrivateKey()), auskeyPassword.toCharArray(), Base64.decode(ausKey.getSalt()));
		} catch (Exception e) {
			logger.info(e);
			ausKeyForm.recordError("Unable to decrypt private key using specified password.");
			return;
		}
		
		ObjectContext context = cayenneService.newContext();
		College localCollege = context.localObject(college);
		
		Preference password = PreferenceUtil.getPreference(context, localCollege, Preferences.AUSKEY_PASSWORD);
		Preference certificate = PreferenceUtil.getPreference(context, localCollege, Preferences.AUSKEY_CERTIFICATE);
		Preference privateKey = PreferenceUtil.getPreference(context, localCollege, Preferences.AUSKEY_PRIVATE_KEY);
		Preference salt = PreferenceUtil.getPreference(context, localCollege, Preferences.AUSKEY_SALT);
		
		if (password != null) {
			password.setValueString(auskeyPassword);
		} else {
			PreferenceUtil.createPreference(context, localCollege, Preferences.AUSKEY_PASSWORD, auskeyPassword);
		}
		
		if (certificate != null) {
			certificate.setValueString(ausKey.getCertificate());
		} else {
			PreferenceUtil.createPreference(context, localCollege, Preferences.AUSKEY_CERTIFICATE, ausKey.getCertificate());
		}
		
		if (privateKey != null) {
			privateKey.setValueString(ausKey.getPrivateKey());
		} else {
			PreferenceUtil.createPreference(context, localCollege, Preferences.AUSKEY_PRIVATE_KEY, ausKey.getPrivateKey());
		}
		
		if (salt != null) {
			salt.setValueString(ausKey.getSalt());
		} else {
			PreferenceUtil.createPreference(context, localCollege, Preferences.AUSKEY_SALT, ausKey.getSalt());
		}
		
		context.commitChanges();
	}
	
	private void disableCollege(ObjectContext context, College college) {
		college.setBillingCode(null);

		List<Preference> licensePrefs = ObjectSelect.query(Preference.class).
				where(Preference.COLLEGE.eq(college).
						andExp(Preference.NAME.in(LICENSE_KEYS))).
				select(context);
		for (Preference pref : licensePrefs) {
			pref.setValueString(Boolean.toString(false));
		}
		
		Preference replicationPref = ObjectSelect.query(Preference.class).
				where(Preference.COLLEGE.eq(college).
						andExp(Preference.NAME.eq(Preferences.REPLICATION_ENABLED))).
				selectFirst(context);
		if (replicationPref != null) {
			replicationPref.setValueString(Boolean.toString(false));
		}
		
		context.commitChanges();
	}

	public String getBucketName() {
		Preference bucketPref = PreferenceUtil.getPreference(
				college.getObjectContext(), college, Preferences.STORAGE_BUCKET_NAME);
		if (bucketPref == null || StringUtils.trimToNull(bucketPref.getValueString()) == null) {
			return null;
		}
		return bucketPref.getValueString();
	}
	
	public boolean isAusKeySet() {
		return PreferenceUtil.getPreference(
				college.getObjectContext(), college, Preferences.AUSKEY_CERTIFICATE) != null;
	}
}
