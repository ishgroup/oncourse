package ish.oncourse.enrol.checkout;

import java.util.Collections;

public class ActionCreditAccess extends APurchaseAction{
    private String password;

	@Override
	protected void makeAction() {
        getController().setShowCreditAmount(true);
	}

	@Override
	protected void parse() {
        if (getParameter() != null)
            password = getParameter().getValue(String.class);
	}

	@Override
	protected boolean validate() {

        if (password == null || password.length() == 0)
        {
            getController().addError(PurchaseController.Message.passwordShouldBeSpecified);
            return false;
        }
        getModel().getObjectContext().invalidateObjects(Collections.singleton(getController().getModel().getPayer()));
        if (!password.equals(getController().getModel().getPayer().getPassword()))
        {
		    getController().addError(PurchaseController.Message.creditAccessPasswordIsWrong);
            return false;
        }
		return true;
	}
}
