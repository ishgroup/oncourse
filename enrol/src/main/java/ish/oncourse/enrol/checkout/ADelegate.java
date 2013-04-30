package ish.oncourse.enrol.checkout;

import java.util.HashMap;
import java.util.Map;

public abstract class ADelegate implements IDelegate{
	private Map<String, String> errors = new HashMap<>();
	private PurchaseController purchaseController;

	@Override
	public void setErrors(Map<String, String> errors) {
		this.errors.clear();
		this.errors.putAll(errors);
	}

	@Override
	public Map<String, String> getErrors() {
		return this.errors;
	}



	public PurchaseController getPurchaseController() {
		return purchaseController;
	}

	public void setPurchaseController(PurchaseController purchaseController) {
		this.purchaseController = purchaseController;
	}

}
