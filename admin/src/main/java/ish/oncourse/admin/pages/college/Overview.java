package ish.oncourse.admin.pages.college;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.persistence.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

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
	private String collegeKey;

	@SetupRender
	void setupRender() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		lastIP = college.getIpAddress();
		onCourseVersion = college.getAngelVersion();
		replicationState = college.getCommunicationKeyStatus().toString();
		lastReplication = college.getLastRemoteAuthentication() != null ? dateFormat.format(college.getLastRemoteAuthentication()): "never";
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

	private void disableCollege(ObjectContext context, College college) {
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
}
