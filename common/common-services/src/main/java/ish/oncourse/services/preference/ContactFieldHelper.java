package ish.oncourse.services.preference;

/**
 * The class has been introduced to exclude duplicate code in tapestry page templates for portal and enrol applications.
 */
public class ContactFieldHelper {

    public static final String VALUE_Show = "Show";
    public static final String VALUE_Required = "Required";

    private PreferenceController preferenceController;

    public ContactFieldHelper(PreferenceController preferenceController) {
        this.preferenceController = preferenceController;
    }

    public boolean getShowAddress() {
        String require = preferenceController.getRequireContactAddressEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireAddress() {
        String require = preferenceController.getRequireContactAddressEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowSuburb() {
        String require = preferenceController.getRequireContactSuburbEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireSuburb() {
        String require = preferenceController.getRequireContactSuburbEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowState() {
        String require = preferenceController.getRequireContactStateEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireState() {
        String require = preferenceController.getRequireContactStateEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowPostcode() {
        String require = preferenceController.getRequireContactPostcodeEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequirePostcode() {
        String require = preferenceController.getRequireContactPostcodeEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowHomePhone() {
        String require = preferenceController.getRequireContactHomePhoneEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireHomePhone() {
        String require = preferenceController.getRequireContactHomePhoneEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowBusinessPhone() {
        String require = preferenceController.getRequireContactBusinessPhoneEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireBusinessPhone() {
        String require = preferenceController.getRequireContactBusinessPhoneEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowFax() {
        String require = preferenceController.getRequireContactFaxEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireFax() {
        String require = preferenceController.getRequireContactFaxEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowMobile() {
        String require = preferenceController.getRequireContactMobileEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireMobile() {
        String require = preferenceController.getRequireContactMobileEnrolment();
        return VALUE_Required.equals(require);
    }

    public boolean getShowDateOfBirth() {
        String require = preferenceController.getRequireContactDateOfBirthEnrolment();
        return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
    }

    public boolean getRequireDateOfBirth() {
        String require = preferenceController.getRequireContactDateOfBirthEnrolment();
        return VALUE_Required.equals(require);
    }

}
