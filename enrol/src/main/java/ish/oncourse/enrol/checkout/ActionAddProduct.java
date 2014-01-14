package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;


public class ActionAddProduct extends APurchaseAction {

    private Product product;

    @Override
    protected void makeAction() {

        getModel().addProduct(product);

        Contact contact = getModel().getPayer();
        ProductItem productItem = getModel().getProductItemBy(contact, product);
        if (productItem == null) {
            productItem = getController().createProductItem(contact, product);
            getModel().addProductItem(productItem);

            ActionEnableProductItem action = PurchaseController.Action.enableProductItem.createAction(getController());
            action.setProductItem(productItem);
            action.setPrice(Money.ZERO);
            action.action();
        }
    }

    @Override
    protected void parse() {
        product = getParameter().getValue(Product.class);
    }

    @Override
    protected boolean validate() {
        return !getModel().getProducts().contains(product);
    }

	public void setProduct(Product product) {
		this.product = product;
	}
}
