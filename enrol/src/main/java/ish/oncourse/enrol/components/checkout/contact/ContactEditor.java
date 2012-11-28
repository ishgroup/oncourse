package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.checkout.contact.ContactEditorParser;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.enrol.pages.Checkout.DATE_FIELD_FORMAT;

public class ContactEditor {

	@Parameter(required = true)
	@Property
	private ContactEditorDelegate delegate;

	@Parameter
	private Object returnPage;

	@Inject
	private PreferenceController preferenceController;

	@Property
	private ContactFieldHelper contactFieldHelper = new ContactFieldHelper(preferenceController);

	@Inject
	private Request request;

	@Property
	private ValidateHandler validateHandler;

	@InjectComponent
	private AvetmissEditor avetmissEditor;

	@InjectComponent
	private ContactEditorFieldSet contactEditorFieldSet;


	@SetupRender
	void beforeRender() {
		validateHandler = new ValidateHandler();
		validateHandler.setErrors(delegate.getErrors());
	}

	public boolean isFillRequiredProperties() {
		return delegate.isFillRequiredProperties();
	}

	public Contact getContact() {
		return delegate.getContact();
	}

	@OnEvent(component = "saveContact", value = "selected")
	public  Object save() {

		if (delegate != null)
		{
			ContactEditorParser contactEditorValidator = new ContactEditorParser();
			contactEditorValidator.setRequest(request);
			contactEditorValidator.setContact(delegate.getContact());
			contactEditorValidator.setContactFieldHelper(contactFieldHelper);
			contactEditorValidator.setMessages(contactEditorFieldSet.getMessages());
			contactEditorValidator.setVisibleFields(delegate.getVisibleFields());
			contactEditorValidator.setDateFormat(getDateFormat());
			contactEditorValidator.parse();
			Map<String,String> errors = new HashMap<String, String>(contactEditorValidator.getErrors());

			avetmissEditor.save();
			errors.putAll(avetmissEditor.getErrors());

			delegate.setErrors(errors);
			delegate.saveContact();
			return returnPage;
		}
		return null;
	}

	public DateFormat getDateFormat()
	{
		return  FormatUtils.getDateFormat(DATE_FIELD_FORMAT, getContact().getCollege().getTimeZone());
	}

	@OnEvent(component = "saveContact", value = "selected")
	public Object cancel() {
		if (!request.isXHR())
			return null;
		if (delegate != null)
		{
			delegate.setErrors(Collections.EMPTY_MAP);
			delegate.cancelContact();
		}
		return returnPage;
	}
}
