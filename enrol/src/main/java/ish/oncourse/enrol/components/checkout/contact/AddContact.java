package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.AddContactParser;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class AddContact {

	@Parameter (required = true)
	private AddContactDelegate delegate;

	@Parameter (required =  false)
	private Block blockToRefresh;

	@Inject
	private Request request;

	@OnEvent(value = "cancelContactEvent")
	public Object cancelContact() {
		if (!request.isXHR())
			return null;

		if (delegate != null)
		{
			delegate.cancelEditing();
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}

	@OnEvent(value = "saveContactEvent")
	public Object saveContact() {
		if (!request.isXHR())
			return null;

		if (delegate != null)
		{
			AddContactParser addContactValidator = new AddContactParser();
			addContactValidator.setContactCredentials(delegate.getContactCredentials());
			addContactValidator.setRequest(request);
			addContactValidator.parse();
			delegate.saveEditing(addContactValidator.getErrors());
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}

}
