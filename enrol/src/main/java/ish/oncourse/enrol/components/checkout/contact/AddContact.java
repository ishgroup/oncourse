package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.AddContactParser;
import ish.oncourse.enrol.pages.Payment;
import ish.oncourse.util.ValidateHandler;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Collections;

import static ish.oncourse.enrol.services.Constants.COMPONENT_submitContact;

public class AddContact {

	@Parameter(required = true)
	@Property
	private AddContactDelegate delegate;

	@Parameter
	@Property
	private boolean showCancelLink;

	@Property
	private ValidateHandler validateHandler;

	@Parameter
	private Object returnPage;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	private boolean reset;

	@SetupRender
	void beforeRender() {
		validateHandler = new ValidateHandler();
		validateHandler.setErrors(delegate.getErrors());
	}


	@OnEvent(value = "cancelContact")
	public Object cancelContact() {
		if (delegate != null) {
			delegate.setErrors(Collections.EMPTY_MAP);
			delegate.resetContact();
		}
		return returnPage;
	}

	@OnEvent(component = COMPONENT_submitContact, value = "selected")
	public Object submitContact() {
		if (delegate != null) {
			AddContactParser addContactValidator = new AddContactParser();
			addContactValidator.setContactCredentials(delegate.getContactCredentials());
			addContactValidator.setRequest(request);
			addContactValidator.parse();
			delegate.setErrors(addContactValidator.getErrors());
			delegate.addContact();
		}
		return returnPage;
	}

	public boolean isPayer() {
		return (returnPage instanceof Payment);
	}
}
