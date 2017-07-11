package ish.oncourse.services.preference;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.CustomFieldType;

import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.services.preference.Preferences.ContactFieldSet;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

/**
 * The class has been introduced to exclude duplicate code in tapestry page templates for portal and enrol applications.
 */
public class ContactFieldHelper {

	public static final String VALUE_Show = "Show";
	public static final String VALUE_Required = "Required";
	public static final String VALUE_Hide = "Hide";

	private PreferenceController preferenceController;

	private ContactFieldSet contactFieldSet;

	public ContactFieldHelper(PreferenceController preferenceController, ContactFieldSet contactFieldSet) {
		this.preferenceController = preferenceController;
		this.contactFieldSet = contactFieldSet;
	}

	private boolean isShow(String require) {
		return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
	}

	public boolean hasVisibleFields(Contact contact) {

		FieldDescriptor[] fields = FieldDescriptor.values();

		for (FieldDescriptor field : fields) {
			if (isValid(field, contact)) {
				String preferenceValue = preferenceController.getValue(field.getPreferenceNameBy(contactFieldSet), false);
				if ((VALUE_Required.equals(preferenceValue) || VALUE_Show.equals(preferenceValue))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasVisibleCustomFields(Contact contact) {
		for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
			if (isCustomFieldTypeRequired(fieldType) || isCustomFieldTypeVisible(fieldType)) {
				return true;
			}
		}
		return false;
	}


	public boolean isAllRequiredFieldFilled(Contact contact) {

		FieldDescriptor[] fields = FieldDescriptor.values();

		for (FieldDescriptor field : fields) {
			if (isValid(field, contact)) {
				String preferenceValue = preferenceController.getValue(field.getPreferenceNameBy(contactFieldSet), false);
				Object propertyValue = contact.readProperty(field.propertyName);
				if (VALUE_Required.equals(preferenceValue) && propertyValue == null) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isAllRequiredCustomFieldFilled(Contact contact) {

		for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
			if (isCustomFieldTypeRequired(fieldType) &&
				CustomField.CUSTOM_FIELD_TYPE.eq(fieldType).andExp(CustomField.VALUE.isNotNull()).filterObjects(contact.getCustomFields()).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isShowField(FieldDescriptor fieldDescriptor, Contact contact) {
		if (!isValid(fieldDescriptor, contact))
			return false;
		String preferenceValue = preferenceController.getValue(fieldDescriptor.getPreferenceNameBy(contactFieldSet), false);
		return isShow(preferenceValue);
	}

	public boolean isRequiredField(FieldDescriptor fieldDescriptor, Contact contact) {
		if (!isValid(fieldDescriptor, contact))
			return false;
		String preferenceValue = preferenceController.getValue(fieldDescriptor.getPreferenceNameBy(contactFieldSet), false);
		return VALUE_Required.equals(preferenceValue);
	}

	public List<String> getVisibleFields(Contact contact, boolean isFillRequiredProperties) {
		ArrayList<String> visibleFields = new ArrayList<>();

		FieldDescriptor[] fieldDescriptors = FieldDescriptor.values();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			if (isVisible(fieldDescriptor, contact, isFillRequiredProperties))
				visibleFields.add(fieldDescriptor.name());
		}
		return visibleFields;
	}

	private boolean isVisible(FieldDescriptor descriptor, Contact contact, boolean isFillRequiredProperties) {
		if (!isValid(descriptor, contact)) {
			 return false;
		}

		if (isFillRequiredProperties)
			return isRequiredField(descriptor, contact) && contact.readProperty(descriptor.propertyName) == null;
		else
			return isShowField(descriptor, contact);
	}

	private boolean isValid(FieldDescriptor descriptor, Contact contact) {
		return (descriptor.isForCompany() && contact.getIsCompany()) ||
				(descriptor.isForPerson() && !contact.getIsCompany());
	}

	public PreferenceController getPreferenceController() {
		return preferenceController;
	}


	public boolean isCustomFieldVisible(CustomField customField) {
		return isCustomFieldTypeVisible(customField.getCustomFieldType());
	}
	
	public boolean isCustomFieldTypeVisible(CustomFieldType customFieldType) {
		if (customFieldType != null) {
			switch (contactFieldSet) {
				case enrolment:
					return isShow(customFieldType.getRequireForEnrolment());
				case waitinglist:
					return isShow(customFieldType.getRequireForWaitingList());
				case mailinglist:
					return isShow(customFieldType.getRequireForMailingList());
				default:
					throw new IllegalArgumentException("Unknown field set type.");
			}
		}
		return false;
	}

	public boolean isCustomFieldTypeRequired(CustomFieldType customFieldType) {
		if (customFieldType != null) {
			switch (contactFieldSet) {
				case enrolment:
					return VALUE_Required.equals(customFieldType.getRequireForEnrolment());
				case waitinglist:
					return VALUE_Required.equals(customFieldType.getRequireForWaitingList());
				case mailinglist:
					return VALUE_Required.equals(customFieldType.getRequireForMailingList());
				default:
					throw new IllegalArgumentException("Unknown field set type.");
			}
		}
		return false;
	}
}
