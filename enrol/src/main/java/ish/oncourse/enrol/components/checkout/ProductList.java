package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class ProductList {

	@Property
	@Parameter(required = true)
	private PurchaseController purchaseController;
	
	@Parameter(required = true)
	@Property
	private Contact contact;

	@Property
	@Parameter(required = false)
	private Block blockToRefresh;

	
	@Inject
	private Request request;
	
	@Property
	private ish.oncourse.model.ProductItem productItem;

	@Property
	private Integer index;

	public Boolean getChecked()
	{
		return  purchaseController.getModel().isProductItemEnabled(productItem);
	}

	public ProductItem.ProductItemDelegate getProductItemDelegate() {
		return new ProductItem.ProductItemDelegate() {
			@Override
			public void onChange(Integer contactIndex, Integer productItemIndex) {
				Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
				ish.oncourse.model.ProductItem productItem = purchaseController.getModel().getProductItemBy(contact, productItemIndex);
				Boolean isSelected = purchaseController.getModel().isProductItemEnabled(productItem);
				ActionParameter actionParameter = new ActionParameter(isSelected ? Action.disableProductItem : Action.enableProductItem);
				actionParameter.setValue(productItem);
				purchaseController.performAction(actionParameter);
			}
		};
	}

	public Integer getContactIndex()
	{
		return purchaseController.getModel().getContacts().indexOf(contact);
	}

	public List<ish.oncourse.model.ProductItem> getProductItems() {
		return purchaseController.getModel().getAllProductItems(contact);
	}
}
