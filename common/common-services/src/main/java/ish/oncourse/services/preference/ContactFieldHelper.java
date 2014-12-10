package ish.oncourse.services.preference;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

/**
 * The class has been introduced to exclude duplicate code in tapestry page templates for portal and enrol applications.
 */
public class ContactFieldHelper {

	public static final String VALUE_Show = "Show";
	public static final String VALUE_Required = "Required";
	public static final String VALUE_Hide = "Hide";

	private PreferenceController preferenceController;

	private ContactFiledsSet contactFiledsSet;

	public ContactFieldHelper(PreferenceController preferenceController, ContactFiledsSet contactFiledsSet) {
		this.preferenceController = preferenceController;
		this.contactFiledsSet = contactFiledsSet;
	}

	@Deprecated
	public boolean getShowAddress() {
		String require = preferenceController.getRequireContactAddressEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireAddress() {
		String require = preferenceController.getRequireContactAddressEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowSuburb() {
		String require = preferenceController.getRequireContactSuburbEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireSuburb() {
		String require = preferenceController.getRequireContactSuburbEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowState() {
		String require = preferenceController.getRequireContactStateEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireState() {
		String require = preferenceController.getRequireContactStateEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowPostcode() {
		String require = preferenceController.getRequireContactPostcodeEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequirePostcode() {
		String require = preferenceController.getRequireContactPostcodeEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowHomePhone() {
		String require = preferenceController.getRequireContactHomePhoneEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireHomePhone() {
		String require = preferenceController.getRequireContactHomePhoneEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowBusinessPhone() {
		String require = preferenceController.getRequireContactBusinessPhoneEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireBusinessPhone() {
		String require = preferenceController.getRequireContactBusinessPhoneEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowFax() {
		String require = preferenceController.getRequireContactFaxEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireFax() {
		String require = preferenceController.getRequireContactFaxEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowMobile() {
		String require = preferenceController.getRequireContactMobileEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireMobile() {
		String require = preferenceController.getRequireContactMobileEnrolment();
		return VALUE_Required.equals(require);
	}

	@Deprecated
	public boolean getShowDateOfBirth() {
		String require = preferenceController.getRequireContactDateOfBirthEnrolment();
		return isShow(require);
	}

	@Deprecated
	public boolean getRequireDateOfBirth() {
		String require = preferenceController.getRequireContactDateOfBirthEnrolment();
		return VALUE_Required.equals(require);
	}


	private boolean isShow(String require) {
		return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
	}


	public boolean isAllRequiredFieldFilled(Contact contact) {

		FieldDescriptor[] fields = FieldDescriptor.values();

		for (FieldDescriptor field : fields) {
			String preferenceValue = preferenceController.getValue(field.getPreferenceNameBy(contactFiledsSet), false);
			Object propertyValue = contact.readProperty(field.propertyName);
			if (VALUE_Required.equals(preferenceValue) && propertyValue == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isShowField(FieldDescriptor fieldDescriptor) {
		String preferenceValue = preferenceController.getValue(fieldDescriptor.getPreferenceNameBy(contactFiledsSet), false);
		return isShow(preferenceValue);
	}

	public boolean isRequiredField(FieldDescriptor fieldDescriptor) {
		String preferenceValue = preferenceController.getValue(fieldDescriptor.getPreferenceNameBy(contactFiledsSet), false);
		return VALUE_Required.equals(preferenceValue);
	}

	public List<String> getVisibleFields(Contact contact, boolean isFillRequiredProperties) {
		ArrayList<String> visibleFields = new ArrayList<>();

		if (contact.getIsCompany()) {
			for (FieldDescriptor fieldDescriptor : FieldDescriptor.COMPANY_FIELDS) {
				visibleFields.add(fieldDescriptor.name());
			}
		} else {
			FieldDescriptor[] fieldDescriptors = FieldDescriptor.values();
			for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
				if (isVisible(fieldDescriptor, contact, isFillRequiredProperties))
					visibleFields.add(fieldDescriptor.name());
			}
		}
		if (visibleFields.size() < 1)
			throw new IllegalArgumentException();
		return visibleFields;
	}

	private boolean isVisible(FieldDescriptor descriptor, Contact contact, boolean isFillRequiredProperties) {
		if (isFillRequiredProperties)
			return isRequiredField(descriptor) && contact.readProperty(descriptor.propertyName) == null;
		else
			return isShowField(descriptor);
	}

	public PreferenceController getPreferenceController() {
		return preferenceController;
	}


	public boolean isCustomFieldVisible(CustomField customField) {
		switch (contactFiledsSet) {
			case enrolment:
				return isShow(customField.getCustomFieldType().getRequireForEnrolment());
			case waitinglist:
				return isShow(customField.getCustomFieldType().getRequireForWaitingList());
			case mailinglist:
				return isShow(customField.getCustomFieldType().getRequireForMailingList());
			default:
				throw new IllegalArgumentException("Unknown field set type.");
		}
	}
	
	public boolean isCustomFieldRequired(CustomField customField) {
		switch (contactFiledsSet) {
			case enrolment:
				return VALUE_Required.equals(customField.getCustomFieldType().getRequireForEnrolment());
			case waitinglist:
				return VALUE_Required.equals(customField.getCustomFieldType().getRequireForWaitingList());
			case mailinglist:
				return VALUE_Required.equals(customField.getCustomFieldType().getRequireForMailingList());
			default:
				throw new IllegalArgumentException("Unknown field set type.");
		}
	}
}
