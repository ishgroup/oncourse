package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.PurchaseController;
import org.apache.tapestry5.annotations.Persist;

public class Payment {

	@Persist
	private PurchaseController purchaseController;

	String onActivate(){
		if (purchaseController == null)
			return Checkout.class.getSimpleName();
		else
			return null;

	}


	public synchronized PurchaseController getPurchaseController() {
		return purchaseController;
	}

	public synchronized void setPurchaseController(PurchaseController purchaseController) {
		this.purchaseController = purchaseController;
	}
}
