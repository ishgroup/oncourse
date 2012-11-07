package ish.oncourse.enrol.services.payment;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseModel;

public interface IPurchaseControllerBuilder {

	public PurchaseController build(PurchaseModel model);
}
