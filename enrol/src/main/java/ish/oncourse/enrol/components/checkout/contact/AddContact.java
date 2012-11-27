package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.AddContactParser;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Collections;

public class AddContact {

	@Parameter (required = true)
	@Property
	private AddContactDelegate delegate;

	@Property
	private ValidateHandler validateHandler;

	@Parameter (required =  false)
	private Object resultPage;

	@Inject
	private Request request;

	private boolean reset;

	@SetupRender
	void beforeRender() {
		validateHandler = new ValidateHandler();
		validateHandler.setErrors(delegate.getErrors());
	}


	@OnEvent(component = "cancelContact", value = "selected")
	public Object cancelContact() {
		if (delegate != null)
		{
			delegate.setErrors(Collections.EMPTY_MAP);
			delegate.cancelEditing();
		}
		return resultPage;
	}

	@OnEvent(component = "saveContact", value = "selected")
	public Object saveContact() {
		if (delegate != null)
		{
			AddContactParser addContactValidator = new AddContactParser();
			addContactValidator.setContactCredentials(delegate.getContactCredentials());
			addContactValidator.setRequest(request);
			addContactValidator.parse();
			delegate.setErrors(addContactValidator.getErrors());
			delegate.saveEditing();
		}
		return resultPage;
	}
}
