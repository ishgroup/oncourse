package ish.oncourse.enrol.services.payment;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseModel;

public interface IPurchaseControllerBuilder {

	public PurchaseController build(PurchaseModel model);
	public PurchaseModel build();

    /**
     * method returns true if new items were added to this <code>purchaseController</code>
     */
    public void updatePurchaseItems(PurchaseController purchaseController);

}
