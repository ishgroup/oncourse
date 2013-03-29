package ish.oncourse.services.preference;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.persistence.CommonPreferenceController;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

public class PreferenceController extends CommonPreferenceController {

	private static final Logger LOGGER = Logger.getLogger(PreferenceController.class);

	private static final String NTIS_LAST_UPDATE = "ntis.lastupdate";
	private static final String POSTCODES_LAST_UPDATE = "postcodes.lastupdate";

	static final String ENROLMENT_MIN_AGE = "enrolment.min.age";
	private static final String REFUND_POLICY_URL = "enrolment.refund.policy.url";

	private static final String HIDE_STUDENT_DETAILS_FROM_TUTOR = "student.details.hidden";
	private static final String TUTOR_FEEDBACK_EMAIL = "tutor.feedbackemail";
	private static final String ENABLE_SOCIAL_MEDIA_LINKS = "website.medialinks.enabled";
	private static final String ENABLE_SOCIAL_MEDIA_LINKS_COURSE = "website.course.medialinks.enabled";
	private static final String ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE = "website.webpage.medialinks.enabled";
	private static final String ADDTHIS_PROFILE_ID = "website.medialinks.addthis";

	protected static final String PAYMENT_GATEWAY_TYPE = "payment.gateway.type";

	//deprecated part
	@Deprecated
	public static final String LICENSE_AVETMISS_UPDATES = "license.avetmiss.updates";
	@Deprecated
	public static final String LICENSE_EMAIL = "license.email";
	@Deprecated
	public static final String LICENSE_WEBSITE_TUTOR_PORTAL = "license.website.tutor.portal";
	@Deprecated
	public static final String LICENSE_WEBSITE_ONLINE_ENROLMENTS = "license.website.online.enrolments";
	@Deprecated
	public static final String LICENSE_WEBSITE_PLAN_NAME = "license.website.plan.name";
	@Deprecated
	public static final String LICENSE_SUPPORT_PLAN_NAME = "license.support.plan.name";
	@Deprecated
	public static final String LICENSE_SUPPORT_PLAN_EXPIRY = "license.support.plan.expiry";

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

	/**
	 * Deprecated now should use {@link PreferenceController#getValue(String, boolean)} instead
	 */
	@Deprecated
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
		} else {
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

	/**
	 * Deprecated now should use {@link PreferenceController#setValue(String, boolean, String)} instead
	 *
	 * @param key
	 * @param isUserPref
	 * @param value
	 */
	@Deprecated
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

		@SuppressWarnings("unchecked")
		List<Preference> results = cayenneService.newContext().performQuery(query);

		return results.isEmpty() ? null : results.get(0);
	}

	public synchronized void setLicenseAvetmissUpdates(boolean value) {
		setValue(LICENSE_AVETMISS_UPDATES, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseAvetmissUpdates() {
		return Boolean.valueOf(getValue(LICENSE_AVETMISS_UPDATES, false));
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

	public synchronized boolean getLicenseEmail() {
		return Boolean.valueOf(getValue(LICENSE_EMAIL, false));
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

	public synchronized boolean getLicenseWebsiteOnlineEnrolments() {
		return Boolean.valueOf(getValue(LICENSE_WEBSITE_ONLINE_ENROLMENTS, false));
	}

	public synchronized void setLicenseWebsiteTutorPortal(boolean value) {
		setValue(LICENSE_WEBSITE_TUTOR_PORTAL, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseWebsiteTutorPortal() {
		return Boolean.valueOf(getValue(LICENSE_WEBSITE_TUTOR_PORTAL, false));
	}

	public synchronized void setWebsitePlanName(String value) {
		setValue(LICENSE_WEBSITE_PLAN_NAME, false, value);
	}

	public synchronized boolean getWebsitePlanName() {
		return Boolean.valueOf(getValue(LICENSE_WEBSITE_PLAN_NAME, false));
	}

	public synchronized void setSupportPlanName(String value) {
		setValue(LICENSE_SUPPORT_PLAN_NAME, false, value);
	}

	public synchronized boolean getSupportPlanName() {
		return Boolean.valueOf(getValue(LICENSE_SUPPORT_PLAN_NAME, false));
	}

	public synchronized void setSupportPlanExpiry(String value) {
		setValue(LICENSE_SUPPORT_PLAN_EXPIRY, false, value);
	}

	public synchronized boolean getSupportPlanExpiry() {
		return Boolean.valueOf(getValue(LICENSE_SUPPORT_PLAN_EXPIRY, false));
	}

	public synchronized String getNTISLastUpdate() {
		return getValue(NTIS_LAST_UPDATE, false);
	}

	public synchronized void setNTISLastUpdate(String value) {
		setValue(NTIS_LAST_UPDATE, false, value);
	}

	public synchronized String getPostcodesLastUpdate() {
		return getValue(POSTCODES_LAST_UPDATE, false);
	}

	public synchronized void setPostcodesLastUpdate(String value) {
		setValue(POSTCODES_LAST_UPDATE, false, value);
	}


	@Deprecated
	public synchronized String getRequireContactAddressWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.street);
	}

	@Deprecated
	public synchronized void setRequireContactAddressWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.street, value);
	}

	@Deprecated
	public synchronized String getRequireContactSuburbWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.suburb);
	}

	@Deprecated
	public synchronized void setRequireContactSuburbWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.suburb, value);
	}

	@Deprecated
	public synchronized String getRequireContactStateWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.state);
	}

	@Deprecated
	public synchronized void setRequireContactStateWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.state, value);
	}

	@Deprecated
	public synchronized String getRequireContactPostcodeWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.postcode);
	}

	@Deprecated
	public synchronized void setRequireContactPostcodeWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.postcode, value);
	}

	@Deprecated
	public synchronized String getRequireContactHomePhoneWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.homePhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactHomePhoneWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.homePhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactBusinessPhoneWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.businessPhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactBusinessPhoneWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.businessPhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactFaxWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.faxNumber);
	}

	@Deprecated
	public synchronized void setRequireContactFaxWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.faxNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactMobileWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.mobilePhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactMobileWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.mobilePhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactDateOfBirthWaitingList() {
		return getRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.dateOfBirth);
	}

	@Deprecated
	public synchronized void setRequireContactDateOfBirthWaitingList(String value) {
		setRequireContactField(ContactFiledsSet.waitinglist, FieldDescriptor.dateOfBirth, value);
	}

	//mailing list
	@Deprecated
	public synchronized String getRequireContactAddressMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.street);
	}

	@Deprecated
	public synchronized void setRequireContactAddressMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.street, value);
	}

	@Deprecated
	public synchronized String getRequireContactSuburbMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.suburb);
	}

	@Deprecated
	public synchronized void setRequireContactSuburbMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.suburb, value);
	}

	@Deprecated
	public synchronized String getRequireContactStateMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.state);
	}

	@Deprecated
	public synchronized void setRequireContactStateMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.state, value);
	}

	@Deprecated
	public synchronized String getRequireContactPostcodeMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.postcode);
	}

	@Deprecated
	public synchronized void setRequireContactPostcodeMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.postcode, value);
	}

	@Deprecated
	public synchronized String getRequireContactHomePhoneMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.homePhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactHomePhoneMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.homePhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactBusinessPhoneMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.businessPhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactBusinessPhoneMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.businessPhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactFaxMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.faxNumber);
	}

	@Deprecated
	public synchronized void setRequireContactFaxMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.faxNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactMobileMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.mobilePhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactMobileMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.mobilePhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactDateOfBirthMailingList() {
		return getRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.dateOfBirth);
	}

	@Deprecated
	public synchronized void setRequireContactDateOfBirthMailingList(String value) {
		setRequireContactField(ContactFiledsSet.mailinglist, FieldDescriptor.dateOfBirth, value);
	}

	//enrolment
	@Deprecated
	public synchronized String getRequireContactAddressEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.street);
	}

	@Deprecated
	public synchronized void setRequireContactAddressEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.street, value);
	}

	@Deprecated
	public synchronized String getRequireContactSuburbEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.suburb);
	}

	@Deprecated
	public synchronized void setRequireContactSuburbEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.suburb, value);
	}

	@Deprecated
	public synchronized String getRequireContactStateEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.state);
	}

	@Deprecated
	public synchronized void setRequireContactStateEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.state, value);
	}

	@Deprecated
	public synchronized String getRequireContactPostcodeEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.postcode);
	}

	@Deprecated
	public synchronized void setRequireContactPostcodeEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.postcode, value);
	}

	@Deprecated
	public synchronized String getRequireContactHomePhoneEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.homePhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactHomePhoneEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.homePhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactBusinessPhoneEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.businessPhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactBusinessPhoneEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.businessPhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactFaxEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.faxNumber);
	}

	@Deprecated
	public synchronized void setRequireContactFaxEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.faxNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactMobileEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.mobilePhoneNumber);
	}

	@Deprecated
	public synchronized void setRequireContactMobileEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.mobilePhoneNumber, value);
	}

	@Deprecated
	public synchronized String getRequireContactDateOfBirthEnrolment() {
		return getRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.dateOfBirth);
	}

	@Deprecated
	public synchronized void setRequireContactDateOfBirthEnrolment(String value) {
		setRequireContactField(ContactFiledsSet.enrolment, FieldDescriptor.dateOfBirth, value);
	}

	public synchronized boolean getHideStudentDetailsFromTutor() {
		try {
			return Boolean.parseBoolean(getValue(HIDE_STUDENT_DETAILS_FROM_TUTOR, false));
		} catch (Exception e) {
			LOGGER.error(String.format("Cannot get property %s", PAYMENT_GATEWAY_TYPE), e);
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

	public synchronized boolean getEnableSocialMediaLinks() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS, false));
		} catch (Exception e) {
			LOGGER.error(String.format("Cannot get property %s", ENABLE_SOCIAL_MEDIA_LINKS), e);
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
			LOGGER.error(String.format("Cannot get property %s", ENABLE_SOCIAL_MEDIA_LINKS_COURSE), e);
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
			LOGGER.error(String.format("Cannot get property %s", ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE), e);
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

	public synchronized PaymentGatewayType getPaymentGatewayType() {
		try {
			return PaymentGatewayType.valueOf(getValue(PAYMENT_GATEWAY_TYPE, false));
		} catch (Exception e) {
			LOGGER.error(String.format("Cannot get property %s", PAYMENT_GATEWAY_TYPE), e);
			return PaymentGatewayType.DISABLED;
		}
	}

	public synchronized void setPaymentGatewayType(PaymentGatewayType value) {
		setValue(PAYMENT_GATEWAY_TYPE, false, value.toString());
	}

	public synchronized boolean isPaymentGatewayEnabled() {
		return !PaymentGatewayType.DISABLED.equals(this.getPaymentGatewayType());
	}

	public synchronized Integer getEnrolmentMinAge() {
		String value = getValue(ENROLMENT_MIN_AGE, false);

		if (value != null && StringUtils.isNumeric(value)) {
			return Integer.valueOf(value);
		} else {
			LOGGER.warn(String.format("Cannot get property %s", ENROLMENT_MIN_AGE));
			return 0;
		}
	}

	public synchronized void setEnrolmentMinAge(Integer age) {
		setValue(ENROLMENT_MIN_AGE, false, age.toString());
	}
	
	public synchronized String getRefundPolicyUrl() {
		return getValue(REFUND_POLICY_URL, false);
	}
	
	public synchronized void setRefundPolicyUrl(String value) {
		setValue(REFUND_POLICY_URL, false, value);
	}

	public String getRequireContactField(ContactFiledsSet contactFiledsSet, FieldDescriptor field) {
		return getValue(field.getPreferenceNameBy(contactFiledsSet), false);
	}


	public void setRequireContactField(ContactFiledsSet contactFiledsSet, FieldDescriptor field, String value) {
		setValue(field.getPreferenceNameBy(contactFiledsSet), false, value);
	}


	public static enum ContactFiledsSet {
		enrolment,
		waitinglist,
		mailinglist,
	}


	public static enum FieldDescriptor {
		street("address", Contact.STREET_PROPERTY, String.class),
		suburb("suburb", Contact.SUBURB_PROPERTY, String.class),
		postcode("postcode", Contact.POSTCODE_PROPERTY, String.class),
		state("state", Contact.STATE_PROPERTY, String.class),
		country("country", Contact.COUNTRY_PROPERTY, Country.class),
		homePhoneNumber("homephone", Contact.HOME_PHONE_NUMBER_PROPERTY, String.class),
		businessPhoneNumber("businessphone", Contact.BUSINESS_PHONE_NUMBER_PROPERTY, String.class),
		faxNumber("fax", Contact.FAX_NUMBER_PROPERTY, String.class),
		mobilePhoneNumber("mobile", Contact.MOBILE_PHONE_NUMBER_PROPERTY, String.class),
		dateOfBirth("birth", Contact.DATE_OF_BIRTH_PROPERTY, Date.class);

		private final String preferenceName;
		public final String propertyName;
		public final Class propertyClass;

		private FieldDescriptor(String preferenceName, String propertyName, Class propertyClass) {
			this.preferenceName = preferenceName;
			this.propertyName = propertyName;
			this.propertyClass = propertyClass;
		}

		public String getPreferenceNameBy(ContactFiledsSet contactFiledsSet) {
			return String.format("%s.contact.%s.required", contactFiledsSet.name(), this.preferenceName);
		}
	}

}
