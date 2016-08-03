package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ProductItem;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.enterVoucherPrice;

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

	public ProductListItem.ProductItemDelegate getProductItemDelegate() {
		return new ProductListItem.ProductItemDelegate() {
			@Override
			public void onChange(Integer contactIndex, Integer productItemIndex, Money price) {

             	Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
				ProductItem productItem = purchaseController.getModel().getProductItemBy(contact, productItemIndex);
             	Boolean isSelected = purchaseController.getModel().isProductItemEnabled(productItem);
				ActionParameter actionParameter = new ActionParameter(isSelected ? Action.disableProductItem : Action.enableProductItem);
				actionParameter.setValue(productItem);
                if(price!=null && !isSelected)
                    actionParameter.setValue(price);
                else if(price==null && !isSelected)
                    actionParameter.setValue(Money.ZERO);
				purchaseController.performAction(actionParameter);
			}
			
			@Override
			public void onUpdate(Integer contactIndex, Integer productItemIndex, Money price) {
				
				Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
				ProductItem productItem = purchaseController.getModel().getProductItemBy(contact, productItemIndex);
				if (price == null) {
					String message = enterVoucherPrice.getMessage(purchaseController.getMessages(), productItem.getProduct().getName());
					purchaseController.getErrors().put(enterVoucherPrice.name(), message);
					return;
				}
				
				ActionParameter actionParameter = new ActionParameter(Action.disableProductItem);
				actionParameter.setValue(productItem);
				purchaseController.performAction(actionParameter);
				
				actionParameter = new ActionParameter(Action.enableProductItem);
				actionParameter.setValue(productItem);
				actionParameter.setValue(price);
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
