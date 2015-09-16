/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import org.apache.commons.lang.StringUtils;

public class CustomFieldsBuilder {
	private CustomFieldHolder fieldHolder;
	private Contact contact;
	
	private CustomFieldsBuilder() {
	}
	
	public static CustomFieldsBuilder valueOf(CustomFieldHolder fieldHolder, Contact contact) {
		CustomFieldsBuilder builder = new CustomFieldsBuilder();
		builder.fieldHolder = fieldHolder;
		builder.contact = contact;
		return builder;
	}
	
	public void build() {
		for (String name : fieldHolder.getCustomFieldNames()) {
			String value = StringUtils.trimToNull(fieldHolder.getCustomFieldValue(name));
			if (value != null) {

				CustomField customField = contact.getObjectContext().newObject(CustomField.class);

				customField.setCollege(contact.getCollege());
				customField.setCustomFieldType(contact.getObjectContext().localObject(fieldHolder.getCustomFieldType(name)));

				customField.setRelatedObject(contact);
				customField.setValue(value);
			}
		}
	}
}
