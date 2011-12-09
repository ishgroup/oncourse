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
	
	private static final String REQUIRE_CONTACT_ADDRESS_ENROLMENT = "enrolment.contact.address.required";
	private static final String REQUIRE_CONTACT_SUBURB_ENROLMENT = "enrolment.contact.suburb.required";
	private static final String REQUIRE_CONTACT_STATE_ENROLMENT = "enrolment.contact.state.required";
	private static final String REQUIRE_CONTACT_POSTCODE_ENROLMENT = "enrolment.contact.postcode.required";
	private static final String REQUIRE_CONTACT_PHONE_ENROLMENT = "enrolment.contact.phone.required";
	private static final String REQUIRE_CONTACT_MOBILE_ENROLMENT = "enrolment.contact.mobile.required";
	private static final String REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT = "enrolment.contact.birth.required";
	
	private static final String REQUIRE_CONTACT_ADDRESS_WAITING_LIST = "waitinglist.contact.address.required";
	private static final String REQUIRE_CONTACT_SUBURB_WAITING_LIST = "waitinglist.contact.suburb.required";
	private static final String REQUIRE_CONTACT_STATE_WAITING_LIST = "waitinglist.contact.state.required";
	private static final String REQUIRE_CONTACT_POSTCODE_WAITING_LIST = "waitinglist.contact.postcode.required";
	private static final String REQUIRE_CONTACT_PHONE_WAITING_LIST = "waitinglist.contact.phone.required";
	private static final String REQUIRE_CONTACT_MOBILE_WAITING_LIST = "waitinglist.contact.mobile.required";
	private static final String REQUIRE_CONTACT_DATE_OF_BIRTH_WAITING_LIST = "waitinglist.contact.birthdate.required";
	
	private static final String REQUIRE_CONTACT_ADDRESS_MAILING_LIST = "mailinglist.contact.address.required";
	private static final String REQUIRE_CONTACT_SUBURB_MAILING_LIST = "mailinglist.contact.suburb.required";
	private static final String REQUIRE_CONTACT_STATE_MAILING_LIST = "mailinglist.contact.state.required";
	private static final String REQUIRE_CONTACT_POSTCODE_MAILING_LIST = "mailinglist.contact.postcode.required";
	private static final String REQUIRE_CONTACT_PHONE_MAILING_LIST = "mailinglist.contact.phone.required";
	private static final String REQUIRE_CONTACT_MOBILE_MAILING_LIST = "mailinglist.contact.mobile.required";
	private static final String REQUIRE_CONTACT_DATE_OF_BIRTH_MAILING_LIST = "mailinglist.contact.birth.required";
	
	private static final String HIDE_STUDENT_DETAILS_FROM_TUTOR = "student.details.hidden";
	private static final String TUTOR_FEEDBACK_EMAIL = "tutor.feedbackemail";
	private static final String REDIRECT_ON_PAYMENT_SUCCESSFUL = "payment.successful.redirect";
	private static final String PAYMENT_SUCCESSFUL_REDIRECT_URL = "payment.successful.redirecturl";
	private static final String ENABLE_SOCIAL_MEDIA_LINKS = "website.medialinks.enabled";
	private static final String ENABLE_SOCIAL_MEDIA_LINKS_COURSE = "website.course.medialinks.enabled";
	private static final String ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE = "website.webpage.medialinks.enabled";
	private static final String ADDTHIS_PROFILE_ID = "website.medialinks.addthis";
	
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
		
		ObjectContext context = null;
		College college = null;
		
		if (webSiteService.getCurrentCollege() != null) {
			context = cayenneService.newContext();

			college = webSiteService.getCurrentCollege();
			college = (College) context.localObject(college.getObjectId(), null);
		}
		else {
			context = cayenneService.newNonReplicatingContext();
		}

		if (pref == null) {
			pref = context.newObject(Preference.class);
			pref.setName(key);
		} else {
			pref = (Preference) context.localObject(pref.getObjectId(), null);
		}

		pref.setCollege(college);
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

		SelectQuery query = new SelectQuery(Preference.class, ExpressionFactory.matchExp(Preference.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()).andExp(
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
	
	public synchronized String getRequireContactAddressWaitingList() {
		return getValue(REQUIRE_CONTACT_ADDRESS_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactAddressWaitingList(String value) {
		setValue(REQUIRE_CONTACT_ADDRESS_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactSuburbWaitingList() {
		return getValue(REQUIRE_CONTACT_SUBURB_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactSuburbWaitingList(String value) {
		setValue(REQUIRE_CONTACT_SUBURB_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactStateWaitingList() {
		return getValue(REQUIRE_CONTACT_STATE_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactStateWaitingList(String value) {
		setValue(REQUIRE_CONTACT_STATE_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactPostcodeWaitingList() {
		return getValue(REQUIRE_CONTACT_POSTCODE_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactPostcodeWaitingList(String value) {
		setValue(REQUIRE_CONTACT_POSTCODE_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactPhoneWaitingList() {
		return getValue(REQUIRE_CONTACT_PHONE_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactPhoneWaitingList(String value) {
		setValue(REQUIRE_CONTACT_PHONE_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactMobileWaitingList() {
		return getValue(REQUIRE_CONTACT_MOBILE_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactMobileWaitingList(String value) {
		setValue(REQUIRE_CONTACT_MOBILE_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactDateOfBirthWaitingList() {
		return getValue(REQUIRE_CONTACT_DATE_OF_BIRTH_WAITING_LIST, false);
	}
	
	public synchronized void setRequireContactDateOfBirthWaitingList(String value) {
		setValue(REQUIRE_CONTACT_DATE_OF_BIRTH_WAITING_LIST, false, value);
	}
	
	public synchronized String getRequireContactAddressMailingList() {
		return getValue(REQUIRE_CONTACT_ADDRESS_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactAddressMailingList(String value) {
		setValue(REQUIRE_CONTACT_ADDRESS_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactSuburbMailingList() {
		return getValue(REQUIRE_CONTACT_SUBURB_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactSuburbMailingList(String value) {
		setValue(REQUIRE_CONTACT_SUBURB_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactStateMailingList() {
		return getValue(REQUIRE_CONTACT_STATE_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactStateMailingList(String value) {
		setValue(REQUIRE_CONTACT_STATE_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactPostcodeMailingList() {
		return getValue(REQUIRE_CONTACT_POSTCODE_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactPostcodeMailingList(String value) {
		setValue(REQUIRE_CONTACT_POSTCODE_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactPhoneMailingList() {
		return getValue(REQUIRE_CONTACT_PHONE_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactPhoneMailingList(String value) {
		setValue(REQUIRE_CONTACT_PHONE_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactMobileMailingList() {
		return getValue(REQUIRE_CONTACT_MOBILE_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactMobileMailingList(String value) {
		setValue(REQUIRE_CONTACT_MOBILE_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactDateOfBirthMailingList() {
		return getValue(REQUIRE_CONTACT_DATE_OF_BIRTH_MAILING_LIST, false);
	}
	
	public synchronized void setRequireContactDateOfBirthMailingList(String value) {
		setValue(REQUIRE_CONTACT_DATE_OF_BIRTH_MAILING_LIST, false, value);
	}
	
	public synchronized String getRequireContactAddressEnrolment() {
		return getValue(REQUIRE_CONTACT_ADDRESS_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactAddressEnrolment(String value) {
		setValue(REQUIRE_CONTACT_ADDRESS_ENROLMENT, false, value);
	}
	
	public synchronized String getRequireContactSuburbEnrolment() {
		return getValue(REQUIRE_CONTACT_SUBURB_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactSuburbEnrolment(String value) {
		setValue(REQUIRE_CONTACT_SUBURB_ENROLMENT, false, value);
	}
	
	public synchronized String getRequireContactStateEnrolment() {
		return getValue(REQUIRE_CONTACT_STATE_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactStateEnrolment(String value) {
		setValue(REQUIRE_CONTACT_STATE_ENROLMENT, false, value);
	}
	
	public synchronized String getRequireContactPostcodeEnrolment() {
		return getValue(REQUIRE_CONTACT_POSTCODE_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactPostcodeEnrolment(String value) {
		setValue(REQUIRE_CONTACT_POSTCODE_ENROLMENT, false, value);
	}
	
	public synchronized String getRequireContactPhoneEnrolment() {
		return getValue(REQUIRE_CONTACT_PHONE_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactPhoneEnrolment(String value) {
		setValue(REQUIRE_CONTACT_PHONE_ENROLMENT, false, value);
	}
	
	public synchronized String getRequireContactMobileEnrolment() {
		return getValue(REQUIRE_CONTACT_MOBILE_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactMobileEnrolment(String value) {
		setValue(REQUIRE_CONTACT_MOBILE_ENROLMENT, false, value);
	}
	
	public synchronized String getRequireContactDateOfBirthEnrolment() {
		return getValue(REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT, false);
	}
	
	public synchronized void setRequireContactDateOfBirthEnrolment(String value) {
		setValue(REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT, false, value);
	}

	public synchronized boolean getHideStudentDetailsFromTutor() {
		try {
			return Boolean.parseBoolean(getValue(HIDE_STUDENT_DETAILS_FROM_TUTOR, false));
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized void setHideStudentDetailsFromTutor(boolean value) {
		setValue(HIDE_STUDENT_DETAILS_FROM_TUTOR, false, Boolean.toString(value));
	}
	
	public synchronized String getTutorFeedbackEmail() {
		return getValue(TUTOR_FEEDBACK_EMAIL, false);
	}
	
	public synchronized void setTutorFeedbackEmail(String value) {
		setValue(TUTOR_FEEDBACK_EMAIL, false, value);
	}
	
	public synchronized boolean getRedirectOnPaymentSuccessful() {
		try {
			return Boolean.parseBoolean(getValue(REDIRECT_ON_PAYMENT_SUCCESSFUL, false));
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized void setRedirectOnPaymentSuccessful(boolean value) {
		setValue(REDIRECT_ON_PAYMENT_SUCCESSFUL, false, Boolean.toString(value));
	}
	
	public synchronized String getPaymentSuccessfulRedirectUrl() {
		return getValue(PAYMENT_SUCCESSFUL_REDIRECT_URL, false);
	}
	
	public synchronized void setPaymentSuccessfulRedirectUrl(String value) {
		setValue(PAYMENT_SUCCESSFUL_REDIRECT_URL, false, value);
	}
	
	public synchronized boolean getEnableSocialMediaLinks() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS, false));
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized void setEnableSocialMediaLinks(boolean value) {
		setValue(ENABLE_SOCIAL_MEDIA_LINKS, false, Boolean.toString(value));
	}
	
	public synchronized boolean getEnableSocialMediaLinksCourse() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS_COURSE, false));
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized void setEnableSocialMediaLinksCourse(boolean value) {
		setValue(ENABLE_SOCIAL_MEDIA_LINKS_COURSE, false, Boolean.toString(value));
	}
	
	public synchronized boolean getEnableSocialMediaLinksWebPage() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, false));
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized void setEnableSocialMediaLinksWebPage(boolean value) {
		setValue(ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, false, Boolean.toString(value));
	}
	
	public synchronized String getAddThisProfileId() {
		return getValue(ADDTHIS_PROFILE_ID, false);
	}
	
	public synchronized void setAddThisProfileId(String value) {
		setValue(ADDTHIS_PROFILE_ID, false, value);
	}
}
