package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactEditorValidator {
	private Request request;
	private Contact contact;
	private ContactFieldHelper contactFieldHelper;
	private Messages messages;
	private DateFormat dateFormat;

	private List<String> visibleFields;

	private Map<String, String> errors = new HashMap<String, String>();



	public void validate()
	{
		for (String visibleField : visibleFields) {
			ContactFieldHelper.FieldDescriptor fieldDescriptor = ContactFieldHelper.FieldDescriptor.valueOf(visibleField);
			String value = StringUtils.trimToNull(request.getParameter(fieldDescriptor.propertyName));
			if (value == null)
			{
				errors.put(fieldDescriptor.propertyName, messages.get("required"));
				return;
			}
			if (fieldDescriptor.propertyClass == Date.class)
			{
				try {
					Date date = dateFormat.parse(value);
					contact.writeProperty(fieldDescriptor.propertyName,date);
				} catch (ParseException e) {
					errors.put(fieldDescriptor.propertyName, messages.get("birthdate-hint"));
					return;
				}
			}
			else
				contact.writeProperty(fieldDescriptor.propertyName,value);
		}
	}



	public void setRequest(Request request) {
		this.request = request;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Map<String, String> getErrors() {
		return errors;
	}


	public void setContactFieldHelper(ContactFieldHelper contactFieldHelper) {
		this.contactFieldHelper = contactFieldHelper;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}


	public void setVisibleFields(List<String> visibleFields) {
		this.visibleFields = visibleFields;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
}
