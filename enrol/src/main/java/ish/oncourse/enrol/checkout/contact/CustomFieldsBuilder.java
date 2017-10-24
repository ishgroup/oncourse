/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactCustomField;
import ish.oncourse.model.CustomField;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CustomFieldsBuilder {
	private ContactCustomFieldHolder fieldHolder;
	private Contact contact;
	
	private CustomFieldsBuilder() {
	}
	
	public static CustomFieldsBuilder valueOf(ContactCustomFieldHolder fieldHolder, Contact contact) {
		CustomFieldsBuilder builder = new CustomFieldsBuilder();
		builder.fieldHolder = fieldHolder;
		builder.contact = contact;
		return builder;
	}
	
	public void build() {

		Map<String, CustomField> contactFields = new HashMap<>();

		//collect all custom field which was already defined for contact
		for (CustomField field : contact.getCustomFields()) {
			contactFields.put(field.getCustomFieldType().getName(), field);
		}
		
		for (String name : fieldHolder.getCustomFieldNames()) {
			if (contactFields.containsKey(name)) {
				//reset value if field for such custom field type already exist for contact
				contactFields.get(name).setValue(fieldHolder.getCustomFieldValue(name));
			} else {
				//create new custom field if value for such custom field type populated on form
				String value = StringUtils.trimToNull(fieldHolder.getCustomFieldValue(name));
				if (value != null) {

					CustomField customField = contact.getObjectContext().newObject(ContactCustomField.class);

					customField.setCollege(contact.getCollege());
					customField.setCustomFieldType(contact.getObjectContext().localObject(fieldHolder.getCustomFieldType(name)));

					customField.setRelatedObject(contact);
					customField.setValue(value);
				}
			}
		}
	}
}
