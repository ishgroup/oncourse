package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.ADelegate;
import ish.oncourse.enrol.checkout.PurchaseController;

public class AddContactController extends ADelegate implements AddContactDelegate{


	private ContactCredentials contactCredentials = new ContactCredentials();
    private PurchaseController.Action cancelAction;
    private PurchaseController.Action addAction;

    private String headerTitle;
    private String headerMessage;
	private boolean companyPayer = false;



	@Override
	public void resetContact() {
		getPurchaseController().performAction(new PurchaseController.ActionParameter(cancelAction));
	}

	@Override
	public void addContact() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(addAction);
		actionParameter.setErrors(getErrors());
		actionParameter.setValue(contactCredentials);
		getPurchaseController().performAction(actionParameter);
	}

	public ContactCredentials getContactCredentials() {
		return contactCredentials;
	}

    public void setCancelAction(PurchaseController.Action cancelAction) {
        this.cancelAction = cancelAction;
    }

    public void setAddAction(PurchaseController.Action addAction) {
        this.addAction = addAction;
    }

    @Override
    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    @Override
    public String getHeaderMessage() {
        return headerMessage;
    }

	@Override
	public boolean isCompanyPayer() {
		return companyPayer;
	}

	public void setHeaderMessage(String headerMessage) {
        this.headerMessage = headerMessage;
    }

	public void setCompanyPayer(boolean companyPayer) {
		this.companyPayer = companyPayer;
	}
}
