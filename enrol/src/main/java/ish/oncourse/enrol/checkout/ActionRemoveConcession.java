package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.StudentConcession;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editCheckout;

public class ActionRemoveConcession extends APurchaseAction{
	private Contact contact;
	private ConcessionType concessionType;

	@Override
	protected void makeAction() {
			for (StudentConcession sc : contact.getStudent().getStudentConcessions()) {
				if (sc.getConcessionType().equals(concessionType)) {
					getModel().getObjectContext().deleteObject(sc);
					break;
				}
			}
			getModel().removeConcession(contact, concessionType);
			getController().recalculateEnrolmentInvoiceLines();
            getController().setState(editCheckout);
    }

	@Override
	protected void parse() {
		if (getParameter() != null)
		{
			contact = getParameter().getValue(Contact.class);
			concessionType = getParameter().getValue(ConcessionType.class);
		}
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
