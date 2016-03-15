package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;

/**
 * Created by akoiro on 15/03/2016.
 */
public class ActionEnableProductItemBuilder {
    private Contact contact;
    private Product product;
    private PurchaseController controller;

    public ActionEnableProductItem build() {
        ProductItem productItem = controller.createProductItem(contact, product);
        controller.getModel().addProductItem(productItem);
        ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(controller);
        actionEnableProductItem.setProductItem(productItem);
        actionEnableProductItem.setPrice(Money.ZERO);
        return actionEnableProductItem;
    }

    public static ActionEnableProductItemBuilder valueOf(Contact contact, Product product, PurchaseController controller) {
        ActionEnableProductItemBuilder builder = new ActionEnableProductItemBuilder();
        builder.contact = contact;
        builder.product = product;
        builder.controller = controller;
        return builder;
    }
}
