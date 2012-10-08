package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ContactItem {
	@Parameter(required = true)
	@Property
	private PurchaseController purchaseController;

	@Parameter(required = true)
	@Property
	private Contact contact;


	public boolean isShowConcessionsArea()
	{
		return true;
	}
}
