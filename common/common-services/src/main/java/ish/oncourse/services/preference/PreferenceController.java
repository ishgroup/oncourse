package ish.oncourse.services.preference;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.persistence.CommonPreferenceController;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;

import static ish.oncourse.services.preference.Preferences.*;
import static ish.oncourse.services.preference.Preferences.ConfigProperty.allowCreateContact;


public class PreferenceController extends CommonPreferenceController {

	private static final Logger logger = LogManager.getLogger();
	
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
		return new GetPreference(webSiteService.getCurrentCollege(), key, cayenneService.sharedContext()).getValue();
	}

	@Override
	protected void setValue(String key, boolean isUserPref, String value) {

		if (isUserPref) {
			throw new IllegalArgumentException("Cannot fetch a user preference in willow.");
		}
		ObjectContext context = cayenneService.sharedContext();
		new GetPreference(webSiteService.getCurrentCollege(), key, context).setValue(value);
		logger.debug("committing changes to prefs: {}", context.uncommittedObjects());

		context.commitChanges();
	}


	public String getNTISLastUpdate() {
		return getValue(NTIS_LAST_UPDATE, false);
	}

	public void setNTISLastUpdate(String value) {
		setValue(NTIS_LAST_UPDATE, false, value);
	}

	public String getPostcodesLastUpdate() {
		return getValue(POSTCODES_LAST_UPDATE, false);
	}

	public void setPostcodesLastUpdate(String value) {
		setValue(POSTCODES_LAST_UPDATE, false, value);
	}

	public boolean getHideStudentDetailsFromTutor() {
		try {
			return Boolean.parseBoolean(getValue(HIDE_STUDENT_DETAILS_FROM_TUTOR, false));
		} catch (Exception e) {
			logger.error("Cannot get property {}", HIDE_STUDENT_DETAILS_FROM_TUTOR, e);
			return false;
		}
	}

	public String getTutorFeedbackEmail() {
		return getValue(TUTOR_FEEDBACK_EMAIL, false);
	}

	public boolean getOutcomeMarkingViaPortal() {
		try {
			return Boolean.parseBoolean(getValue(OUTCOME_MARKING_VIA_PORTAL, false));
		} catch (Exception e) {
			logger.error("Cannot get property {}", OUTCOME_MARKING_VIA_PORTAL, e);
			return false;
		}
	}

	public boolean getEnableSocialMediaLinks() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS, false));
		} catch (Exception e) {
			logger.error("Cannot get property {}", ENABLE_SOCIAL_MEDIA_LINKS, e);
			return false;
		}
	}

	public boolean getEnableSocialMediaLinksCourse() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS_COURSE, false));
		} catch (Exception e) {
			logger.error("Cannot get property {}", ENABLE_SOCIAL_MEDIA_LINKS_COURSE, e);
			return false;
		}
	}

	public boolean getEnableSocialMediaLinksWebPage() {
		try {
			return Boolean.parseBoolean(getValue(ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, false));
		} catch (Exception e) {
			logger.error("Cannot get property {}", ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, e);
			return false;
		}
	}
	
	public String getAddThisProfileId() {
		return getValue(ADDTHIS_PROFILE_ID, false);
	}

	public boolean isPaymentGatewayEnabled() {
		return new IsPaymentGatewayEnabled(webSiteService.getCurrentCollege(), cayenneService.sharedContext()).get();
	}
	
    public boolean isCollectParentDetails()
    {
        return new IsCollectParentDetails(webSiteService.getCurrentCollege(), cayenneService.sharedContext()).get();
    }

    public void setCollectParentDetails(boolean value)
    {
        setValue(ENROLMENT_collectParentDetails, false, Boolean.toString(value));
    }

	public void setHideClassOnWebsiteAge(ClassAge classAge) {
		setValue(HIDE_CLASS_ON_WEB_AGE, false, String.valueOf(classAge.getDays()));
		setValue(HIDE_CLASS_ON_WEB_AGE_TYPE, false, String.valueOf(classAge.getType().name()));
	}

	public ClassAge getHideClassOnWebsiteAge() {
		return ClassAge.valueOf(getValue(HIDE_CLASS_ON_WEB_AGE, false),
				getValue(HIDE_CLASS_ON_WEB_AGE_TYPE, false));
	}

	public void setStopWebEnrolmentsAge(ClassAge classAge) {
		setValue(STOP_WEB_ENROLMENTS_AGE, false, String.valueOf(classAge.getDays()));
		setValue(STOP_WEB_ENROLMENTS_AGE_TYPE, false, String.valueOf(classAge.getType().name()));
	}

	public ClassAge getStopWebEnrolmentsAge() {
		return ClassAge.valueOf(getValue(STOP_WEB_ENROLMENTS_AGE, false),
				getValue(STOP_WEB_ENROLMENTS_AGE_TYPE, false));
	}

	public void setAllowCreateContact(ContactFieldSet contactFieldSet, boolean value) {
		setValue(allowCreateContact.getPreferenceNameBy(contactFieldSet), false, String.valueOf(value));
	}

    public boolean getAllowCreateContact(ContactFieldSet contactFieldSet) {
        String value = getValue(allowCreateContact.getPreferenceNameBy(contactFieldSet), false);
        if (StringUtils.isBlank(value)) {
            return true;
        } else {
            return Boolean.valueOf(value);
        }
    }
    
	public String getStorageAccessId() {
		return getSetting(Settings.STORAGE_ACCESS_ID);
	}
	
	public String getStorageAccessKey() {
		return getSetting(Settings.STORAGE_ACCESS_KEY);
	}
	
	public String getStorageBucketName() {
		return getSetting(Settings.STORAGE_BUCKET_NAME);
	}
	
	protected String getSetting(String name) {
		Settings setting = ObjectSelect.query(Settings.class)
				.where(Settings.COLLEGE.eq(webSiteService.getCurrentCollege()))
				.and(Settings.NAME.eq(name)).selectOne(cayenneService.newContext());
		if (setting != null) {
			return setting.getValue();
		} else {
			return null;
		}
	}
	
	public enum FieldDescriptor {
		street("address", Contact.STREET.getName(), String.class, true, true),
		suburb("suburb", Contact.SUBURB.getName(), String.class, true, true),
		postcode("postcode", Contact.POSTCODE.getName(), String.class, true, true),
		state("state", Contact.STATE.getName(), String.class, true, true),
		country("country", Contact.COUNTRY.getName(), Country.class, true, true),
		homePhoneNumber("homephone", Contact.HOME_PHONE_NUMBER.getName(), String.class, false, true),
		businessPhoneNumber("businessphone", Contact.BUSINESS_PHONE_NUMBER.getName(), String.class, true, true),
		faxNumber("fax", Contact.FAX_NUMBER.getName(), String.class, true, true),
		mobilePhoneNumber("mobile", Contact.MOBILE_PHONE_NUMBER.getName(), String.class, false, true),
		dateOfBirth("birth", Contact.DATE_OF_BIRTH.getName(), Date.class, false, true),
		specialNeeds("specialneeds", Contact.STUDENT.getName() + "." + Student.SPECIAL_NEEDS.getName(), String.class, false, true),
		abn("abn", Contact.ABN.getName(), String.class, true, false),
		isMale("isMale", Contact.GENDER.getName(), Boolean.class, false, true),
		townCityOfBirth("townCityOfBirth", Student.TOWN_OF_BIRTH.getName(), String.class, false, true);

		private final String preferenceName;
		public final String propertyName;
		public final Class propertyClass;
        private boolean forCompany;
        private boolean forPerson;

        FieldDescriptor(String preferenceName, String propertyName, Class propertyClass, boolean forCompany, boolean forPerson) {
			this.preferenceName = preferenceName;
			this.propertyName = propertyName;
			this.propertyClass = propertyClass;
            this.forCompany = forCompany;
            this.forPerson = forPerson;
        }

		public String getPreferenceNameBy(ContactFieldSet contactFieldSet) {
			return String.format("%s.contact.%s.required", contactFieldSet.name(), this.preferenceName);
		}

        public boolean isForCompany() {
            return forCompany;
        }

        public boolean isForPerson() {
            return forPerson;
        }
    }

}
