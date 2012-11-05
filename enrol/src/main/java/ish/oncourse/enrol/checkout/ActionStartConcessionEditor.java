package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editConcession;

public class ActionStartConcessionEditor extends APurchaseAction {
	private Contact contact;

	@Override
	protected void makeAction() {
		ConcessionEditorController concessionEditorController = new ConcessionEditorController();
		concessionEditorController.setObjectContext(this.getModel().getObjectContext().createChildContext());
		concessionEditorController.setContact((Contact) concessionEditorController.getObjectContext().localObject(contact.getObjectId(), null));
		concessionEditorController.setPurchaseController(getController());
		getController().setConcessionEditorController(concessionEditorController);
		getController().setState(editConcession);
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
		{
			contact = getParameter().getValue(Contact.class);
		}
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
