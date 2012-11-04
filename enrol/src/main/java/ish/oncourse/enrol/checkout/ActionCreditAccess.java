package ish.oncourse.enrol.checkout;

public class ActionCreditAccess extends APurchaseAction{

	public static final String ERROR_KEY_creditAccessPasswordIsWrong = "error-creditAccessPasswordIsWrong";
	@Override
	protected void makeAction() {
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		getController().getErrors().add(getController().getMessages().get(ERROR_KEY_creditAccessPasswordIsWrong));
		return false;
	}
}
