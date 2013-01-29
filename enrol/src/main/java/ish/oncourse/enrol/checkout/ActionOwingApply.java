package ish.oncourse.enrol.checkout;

public class ActionOwingApply extends APurchaseAction{
	@Override
	protected void makeAction() {
        if (getController().hasPreviousOwing())
            getController().getModel().setApplyPrevOwing(false);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
