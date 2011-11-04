package ish.oncourse.services.preference;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.persistence.CommonPreferenceController;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PreferenceController extends CommonPreferenceController {

	private static final Logger LOGGER = Logger.getLogger(PreferenceController.class);
	
	private static final String NTIS_LAST_UPDATE = "ntis.lastupdate";
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteService webSiteService;

	/**
	 * Default constructor used for auto injection
	 */
	public PreferenceController() {
	
	}
	
	public PreferenceController(ICayenneService cayenneService, IWebSiteService webSiteService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
	}

	@Override
	protected String getValue(String key, boolean isUserPref) {

		if (isUserPref) {
			throw new IllegalArgumentException("Cannot fetch a user preference in willow.");
		}

		Preference pref = getPreferenceByKey(key);
		return (pref != null) ? pref.getValueString() : null;
	}

	@Override
	protected Object getBinaryValue(String key, boolean isUserPref) {

		if (isUserPref) {
			throw new IllegalArgumentException("Cannot fetch a user preference in willow.");
		}

		Preference pref = getPreferenceByKey(key);

		return (pref != null) ? deserializeObject(pref.getValue()) : null;
	}

	@Override
	protected void setValue(String key, boolean isUserPref, String value) {

		if (isUserPref) {
			throw new IllegalArgumentException("Cannot fetch a user preference in willow.");
		}

		Preference pref = getPreferenceByKey(key);

		ObjectContext context = cayenneService.newContext();

		if (pref == null) {
			pref = context.newObject(Preference.class);
			College college = (College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null);
			pref.setCollege(college);
			pref.setName(key);
		} else {
			pref = (Preference) context.localObject(pref.getObjectId(), null);
		}

		pref.setValueString(value);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("committing changes to prefs:" + context.uncommittedObjects());
		}

		context.commitChanges();

	}

	@Override
	protected void setBinaryValue(String key, boolean isUserPref, Object value) {

		if (isUserPref) {
			throw new IllegalArgumentException("Cannot fetch a user preference in willow.");
		}

		Preference pref = getPreferenceByKey(key);

		ObjectContext context = cayenneService.newContext();

		if (pref == null) {
			pref = context.newObject(Preference.class);
			pref.setName(key);
		} else {
			pref = (Preference) context.localObject(pref.getObjectId(), null);
		}

		pref.setValue(serializeObject(value));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("committing changes to prefs:" + context.uncommittedObjects());
		}

		context.commitChanges();
	}

	private Preference getPreferenceByKey(String key) {

		SelectQuery query = new SelectQuery(Preference.class, ExpressionFactory.matchExp(Preference.COLLEGE_PROPERTY, webSiteService.getCurrentCollege().getObjectId()).andExp(
				ExpressionFactory.matchExp(Preference.NAME_PROPERTY, key)));

		List<Preference> results = cayenneService.newContext().performQuery(query);

		return results.isEmpty() ? null : results.get(0);
	}

	public synchronized void setLicenseAvetmissUpdates(boolean value) {
		setValue(LICENSE_AVETMISS_UPDATES, false, Boolean.toString(value));
	}

	public synchronized void setLicenseAccessControl(boolean value) {
		setValue(LICENSE_ACCESS_CONTROL, false, Boolean.toString(value));
	}

	public synchronized void setLicenseLdap(boolean value) {
		setValue(LICENSE_LDAP, false, Boolean.toString(value));
	}

	public synchronized void setLicenseBudget(boolean value) {
		setValue(LICENSE_BUDGET, false, Boolean.toString(value));
	}

	public synchronized void setLicenseExternalDB(boolean value) {
		setValue(LICENSE_EXTENRNAL_DB, false, Boolean.toString(value));
	}

	public synchronized void setLicenseSSL(boolean value) {
		setValue(LICENSE_SSL, false, Boolean.toString(value));
	}

	public synchronized void setLicenseEmail(boolean value) {
		setValue(LICENSE_EMAIL, false, Boolean.toString(value));
	}

	public synchronized void setLicenseSms(boolean value) {
		setValue(LICENSE_SMS, false, Boolean.toString(value));
	}

	public synchronized void setLicenseCCProcessing(boolean value) {
		setValue(LICENSE_CC_PROCESSING, false, Boolean.toString(value));
	}

	public synchronized void setLicensePayroll(boolean value) {
		setValue(LICENSE_PAYROLL, false, Boolean.toString(value));
	}

	public synchronized void setLicenseWebsite(boolean value) {
		setValue(LICENSE_WEBSITE, false, Boolean.toString(value));
	}

	public synchronized void setLicenseWebsiteOnlineEnrolments(boolean value) {
		setValue(LICENSE_WEBSITE_ONLINE_ENROLMENTS, false, Boolean.toString(value));
	}

	public synchronized void setLicenseWebsiteTutorPortal(boolean value) {
		setValue(LICENSE_WEBSITE_TUTOR_PORTAL, false, Boolean.toString(value));
	}

	public synchronized void setWebsitePlanName(String value) {
		setValue(LICENSE_WEBSITE_PLAN_NAME, false, value);
	}

	public synchronized void setSupportPlanName(String value) {
		setValue(LICENSE_SUPPORT_PLAN_NAME, false, value);
	}

	public synchronized void setSupportPlanExpiry(String value) {
		setValue(LICENSE_SUPPORT_PLAN_EXPIRY, false, value);
	}
	
	public synchronized String getNTISLastUpdate() {
		return getValue(NTIS_LAST_UPDATE, false);
	}
	
	public synchronized void setNTISLastUpdate(String value) {
		setValue(NTIS_LAST_UPDATE, false, value);
	}
}
