package ish.oncourse.services.preference;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomFieldType;

import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

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

	private boolean isShow(String require) {
		return VALUE_Show.equals(require) || VALUE_Required.equals(require) || require == null;
	}
	
	private boolean isShowField(FieldDescriptor fieldDescriptor, Contact contact) {
		if (!isValid(fieldDescriptor, contact))
			return false;
		String preferenceValue = preferenceController.getValue(fieldDescriptor.getPreferenceNameBy(), false);
		return isShow(preferenceValue);
	}

	public boolean isRequiredField(FieldDescriptor fieldDescriptor, Contact contact) {
		if (!isValid(fieldDescriptor, contact))
			return false;
		String preferenceValue = preferenceController.getValue(fieldDescriptor.getPreferenceNameBy(), false);
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

	public boolean isCustomFieldTypeRequired(CustomFieldType customFieldType) {
		return VALUE_Required.equals(customFieldType.getRequireForEnrolment());
	}
}
