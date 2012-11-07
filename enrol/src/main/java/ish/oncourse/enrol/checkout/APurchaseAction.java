package ish.oncourse.enrol.checkout;

public abstract class APurchaseAction {
	private PurchaseController controller;
	private PurchaseController.ActionParameter parameter;


	public boolean action() {
		if (parameter != null && parameter.getErrors() != null && parameter.getErrors().size() > 0) {
			controller.getErrors().putAll(parameter.getErrors());
			return false;
		}

		parse();

		if (validate()) {
			makeAction();
			return true;
		} else
			return false;
	}


	protected abstract void makeAction();

	protected abstract void parse();

	protected abstract boolean validate();


	public PurchaseModel getModel() {
		return controller.getModel();
	}

	public PurchaseController getController() {
		return controller;
	}

	public void setController(PurchaseController controller) {
		this.controller = controller;
	}

	public PurchaseController.ActionParameter getParameter() {
		return parameter;
	}

	public void setParameter(PurchaseController.ActionParameter parameter) {
		this.parameter = parameter;
	}
}
