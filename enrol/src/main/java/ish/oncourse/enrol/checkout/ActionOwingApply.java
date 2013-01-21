package ish.oncourse.enrol.checkout;

public class ActionOwingApply extends APurchaseAction{
	@Override
	protected void makeAction() {
        getController().getModel().setApplingOwing(false);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
