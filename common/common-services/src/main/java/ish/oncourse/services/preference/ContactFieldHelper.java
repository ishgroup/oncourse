package ish.oncourse.services.preference;

import ish.oncourse.model.Contact;

import java.util.Date;

import static ish.oncourse.services.preference.PreferenceController.*;

/**
 * The class has been introduced to exclude duplicate code in tapestry page templates for portal and enrol applications.
 */
public class ContactFieldHelper {

    public static final String VALUE_Show = "Show";
    public static final String VALUE_Required = "Required";
	public static final String VALUE_Hide = "Hide";

	private PreferenceController preferenceController;

    public ContactFieldHelper(PreferenceController preferenceController) {
        this.preferenceController = preferenceController;
    }

    public boolean getShowAddress() {
        String require = preferenceController.getRequireContactAddressEnrolment();
        return isShow(require);
    }

    public boolean getRequireAddress() {
        String require = preferenceController.getRequireContactAddressEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowSuburb() {
        String require = preferenceController.getRequireContactSuburbEnrolment();
        return isShow(require);
    }

    public boolean getRequireSuburb() {
        String require = preferenceController.getRequireContactSuburbEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowState() {
        String require = preferenceController.getRequireContactStateEnrolment();
        return isShow(require);
    }

    public boolean getRequireState() {
        String require = preferenceController.getRequireContactStateEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowPostcode() {
        String require = preferenceController.getRequireContactPostcodeEnrolment();
        return isShow(require);
    }

    public boolean getRequirePostcode() {
        String require = preferenceController.getRequireContactPostcodeEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowHomePhone() {
        String require = preferenceController.getRequireContactHomePhoneEnrolment();
        return isShow(require);
    }

    public boolean getRequireHomePhone() {
        String require = preferenceController.getRequireContactHomePhoneEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowBusinessPhone() {
        String require = preferenceController.getRequireContactBusinessPhoneEnrolment();
        return isShow(require);
    }

    public boolean getRequireBusinessPhone() {
        String require = preferenceController.getRequireContactBusinessPhoneEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowFax() {
        String require = preferenceController.getRequireContactFaxEnrolment();
        return isShow(require);
    }

    public boolean getRequireFax() {
        String require = preferenceController.getRequireContactFaxEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowMobile() {
        String require = preferenceController.getRequireContactMobileEnrolment();
        return isShow(require);
    }

    public boolean getRequireMobile() {
        String require = preferenceController.getRequireContactMobileEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowDateOfBirth() {
        String require = preferenceController.getRequireContactDateOfBirthEnrolment();
        return isShow(require);
    }

	private boolean isShow(String require) {
		return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
	}

	public boolean getRequireDateOfBirth() {
        String require = preferenceController.getRequireContactDateOfBirthEnrolment();
        return VALUE_Required.equals(require);
    }

	public boolean isAllRequiredFieldFilled(Contact contact)
	{

		FieldDescriptor[] fields = FieldDescriptor.values();

		for (FieldDescriptor field : fields) {
			String preferenceValue = preferenceController.getValue(field.preferenceName, false);
			Object propertyValue = contact.readProperty(field.propertyName);
			if (VALUE_Required.equals(preferenceValue) && propertyValue == null)
			{
				return false;
			}
		}
		return true;
	}

	public boolean isShowField(FieldDescriptor fieldDescriptor)
	{
		String preferenceValue = preferenceController.getValue(fieldDescriptor.preferenceName, false);
		return isShow(preferenceValue);
	}

	public boolean isRequiredField(FieldDescriptor fieldDescriptor)
	{
		String preferenceValue = preferenceController.getValue(fieldDescriptor.preferenceName, false);
		return VALUE_Required.equals(preferenceValue);
	}

	public PreferenceController getPreferenceController() {
		return preferenceController;
	}


	public static enum FieldDescriptor
	{
		street(REQUIRE_CONTACT_ADDRESS_ENROLMENT, Contact.STREET_PROPERTY,String.class),
		suburb(REQUIRE_CONTACT_ADDRESS_ENROLMENT, Contact.SUBURB_PROPERTY, String.class),
		postcode(REQUIRE_CONTACT_POSTCODE_ENROLMENT, Contact.POSTCODE_PROPERTY, String.class),
		state(REQUIRE_CONTACT_STATE_ENROLMENT, Contact.STATE_PROPERTY, String.class),
		homePhoneNumber(REQUIRE_CONTACT_HOME_PHONE_ENROLMENT, Contact.HOME_PHONE_NUMBER_PROPERTY,String.class),
		businessPhoneNumber(REQUIRE_CONTACT_HOME_PHONE_ENROLMENT, Contact.BUSINESS_PHONE_NUMBER_PROPERTY,String.class),
		faxNumber(REQUIRE_CONTACT_FAX_ENROLMENT, Contact.FAX_NUMBER_PROPERTY,String.class),
		mobilePhoneNumber(REQUIRE_CONTACT_MOBILE_ENROLMENT, Contact.MOBILE_PHONE_NUMBER_PROPERTY,String.class),
		dateOfBirth(REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT, Contact.DATE_OF_BIRTH_PROPERTY,Date.class);

		public final String preferenceName;
		public final String propertyName;
		public final Class propertyClass;

		private FieldDescriptor(String preferenceName, String propertyName, Class propertyClass)
		{
			this.preferenceName = preferenceName;
			this.propertyName = propertyName;
			this.propertyClass = propertyClass;
		}
	}
}
