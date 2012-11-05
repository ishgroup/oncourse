package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;

public class ActionChangePayer extends  APurchaseAction{

	private  Contact contact;

	@Override
	protected void parse()
	{
		if (getParameter() != null)
			contact = getParameter().getValue(Contact.class);
	}

	@Override
	protected boolean validate()
	{
		return getController().getModel().getContacts().contains(contact);
	}

	@Override
	protected void makeAction() {
		Contact oldPayer = getController().getModel().getPayer();

		if (oldPayer != null) {
			getController().getModel().removeAllProductItems(contact);
		}

		getController().getModel().setPayer(contact);

		for (Product product : getController().getModel().getProducts()) {
			ProductItem productItem = getController().createProductItem(contact, product);
			getController().getModel().addProductItem(productItem);
			ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(getController());
			actionEnableProductItem.setProductItem(productItem);
			actionEnableProductItem.action();
		}

	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
