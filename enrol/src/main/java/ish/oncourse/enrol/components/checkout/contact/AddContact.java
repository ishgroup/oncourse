package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.AddContactValidator;
import org.apache.tapestry5.Block;
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

	public Object onActionFromCancelContactLink() {
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

	public Object onActionFromSaveContactLink() {
		if (!request.isXHR())
			return null;

		if (delegate != null)
		{
			AddContactValidator addContactValidator = new AddContactValidator();
			addContactValidator.setContactCredentials(delegate.getContactCredentials());
			addContactValidator.setRequest(request);
			addContactValidator.validate();
			delegate.saveEditing(addContactValidator.getErrors());
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}

}
