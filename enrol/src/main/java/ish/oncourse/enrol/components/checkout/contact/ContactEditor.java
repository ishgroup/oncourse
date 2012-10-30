package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.checkout.contact.ContactEditorValidator;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static ish.oncourse.enrol.pages.Checkout.DATE_FIELD_FORMAT;

public class ContactEditor {

	private static final String LABEL_TEMPLATE = "label-%s";

	@Parameter(required = true)
	private ContactEditorDelegate delegate;

	@Parameter
	private Block blockToRefresh;

	@Inject
	private PreferenceController preferenceController;

	@Property
	private ContactFieldHelper contactFieldHelper = new ContactFieldHelper(preferenceController);

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	@Property
	private String fieldName;


	@SetupRender
	void beforeRender() {
	}

	public boolean isFillRequiredProperties() {
		return delegate.isFillRequiredProperties();
	}

	public Contact getContact() {
		return delegate.getContact();
	}

	public List<String> getVisibleFields()
	{
		return delegate.getVisibleFields();
	}


	public boolean required(String fieldName)
	{
		return contactFieldHelper.isRequiredField(ContactFieldHelper.FieldDescriptor.valueOf(fieldName));
	}

	public String label(String fieldName)
	{
		return messages.get(String.format(LABEL_TEMPLATE, fieldName));
	}

	public String value(String fieldName)
	{
		ContactFieldHelper.FieldDescriptor fieldDescriptor = ContactFieldHelper.FieldDescriptor.valueOf(fieldName);

		Object value = getContact().readProperty(fieldName);
		if (fieldDescriptor.propertyClass == Date.class && value != null)
			value = getDateFormat().format((Date) value);
		return value == null ? FormatUtils.EMPTY_STRING: value.toString();
	}

	@OnEvent(value = "saveContactEvent")
	public  Object save() {

		if (!request.isXHR())
			return null;
		if (delegate != null)
		{
			ContactEditorValidator contactEditorValidator = new ContactEditorValidator();
			contactEditorValidator.setRequest(request);
			contactEditorValidator.setContact(delegate.getContact());
			contactEditorValidator.setContactFieldHelper(contactFieldHelper);
			contactEditorValidator.setMessages(messages);
			contactEditorValidator.setVisibleFields(delegate.getVisibleFields());
			contactEditorValidator.setDateFormat(getDateFormat());
			contactEditorValidator.validate();
			delegate.saveContact(contactEditorValidator.getErrors());
			return blockToRefresh;
		}
		return null;
	}

	public DateFormat getDateFormat()
	{
		return  FormatUtils.getDateFormat(DATE_FIELD_FORMAT, getContact().getCollege().getTimeZone());
	}

	@OnEvent(value = "cancelContactEvent")
	public Object cancel() {
		if (!request.isXHR())
			return null;
		if (delegate != null)
		{
			delegate.cancelContact();
			return blockToRefresh;
		}
		return null;
	}


}
