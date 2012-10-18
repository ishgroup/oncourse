package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.utils.PurchaseController;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Amount {

	@Property
	@Parameter (required = true)
	private PurchaseController purchaseController;
}
