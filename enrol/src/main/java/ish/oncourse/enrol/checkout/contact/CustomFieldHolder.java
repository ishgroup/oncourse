/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.CustomFieldType;
import ish.oncourse.model.Pair;
import ish.oncourse.services.preference.ContactFieldHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


import java.util.TreeSet;

public class CustomFieldHolder {

	private ContactFieldHelper contactFieldHelper;

	private HashMap<String, Pair<CustomFieldType, String>> fieldContainer = new HashMap<>();

	private CustomFieldHolder(){
	}

	private void addType(String fieldName, CustomFieldType type, String value) {
		fieldContainer.put(fieldName, new Pair<>(type, value));
	}

	public Set<String> getCustomFieldNames() {
		Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		names.addAll(fieldContainer.keySet());
		return names;
	}

	public String getCustomFieldValue(String name) {
		return fieldContainer.get(name).getSecond();
	}

	public CustomFieldType getCustomFieldType(String name) {
		return fieldContainer.get(name).getFirst();
	}

	public String getDefaultCustomFieldValue(String name) {
		return fieldContainer.get(name).getFirst().getDefaultValue();
	}

	public void setCustomFieldValue(String name, String value) {
		fieldContainer.get(name).setSecond(value);
	}
	
	public boolean isCustomFieldRequared(String name) {
		return contactFieldHelper.isCustomFieldTypeRequired(fieldContainer.get(name).getFirst());
	}
	
	public void setContactFieldHelper(ContactFieldHelper contactFieldHelper) {
		this.contactFieldHelper = contactFieldHelper;
	}
	
	public static CustomFieldHolder valueOf(ContactFieldHelper contactFieldHelper, Contact contact, boolean fillRequaredFieldOnly) {
		CustomFieldHolder fieldHolder = new CustomFieldHolder();
		fieldHolder.setContactFieldHelper(contactFieldHelper);

		if (fillRequaredFieldOnly) {
			for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
				if (contactFieldHelper.isCustomFieldTypeRequired(fieldType) &&
						CustomField.CUSTOM_FIELD_TYPE.eq(fieldType).andExp(CustomField.VALUE.isNotNull()).filterObjects(contact.getCustomFields()).isEmpty()) {
					fieldHolder.addType(fieldType.getName(), fieldType, null);
				}
			}
		} else {
			for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
				if (contactFieldHelper.isCustomFieldTypeVisible(fieldType)) {
					fieldHolder.addType(fieldType.getName(), fieldType, null);
				}
			}
		}

		return fieldHolder;
	}
}
