package ish.oncourse.enrol.components.checkout;

import java.util.List;

import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ProductItem;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ProductList {

	@Property
	@Parameter(required = true)
	private PurchaseController purchaseController;
	
	@Parameter(required = true)
	@Property
	private Contact contact;
	
	@Inject
	private Request request;
	
	@Property
	private ProductItem productItem;
	
	public List<ProductItem> getProductItems() {
		return purchaseController.getModel().getAllProductItems(contact);
	}
	
	public StreamResponse onActionFromTick(String productItemIndex) {
		if (!request.isXHR())
			return null;
		if (!StringUtils.isNumeric(productItemIndex))
			return null;

		Integer index = new Integer(productItemIndex);
		ProductItem productItem = purchaseController.getModel().getProductItemBy(contact, index);
		Boolean isSelected = purchaseController.getModel().isProductItemEnabled(productItem);
		ActionParameter actionParameter = new ActionParameter(isSelected ? Action.DISABLE_PRODUCT_ITEM : Action.ENABLE_PRODUCT_ITEM);
		actionParameter.setValue(productItem);
		purchaseController.performAction(actionParameter);
        return null;
    }
}
