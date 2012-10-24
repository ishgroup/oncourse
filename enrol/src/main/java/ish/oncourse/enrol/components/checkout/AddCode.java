package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import org.apache.tapestry5.annotations.Parameter;

public class AddCode {
	@Parameter(required = true)
	private PurchaseController purchaseController;
}
