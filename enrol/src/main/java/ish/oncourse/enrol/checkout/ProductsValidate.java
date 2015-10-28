package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Queueable;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ProductsValidate {
    private PurchaseController controller;
    private CorporatePass corporatePass;

    public boolean validate() {
        List<Product> validProducts = corporatePass.getValidProducts();
        if (validProducts.isEmpty()) {
            return true;
        }


        List<ProductItem> productItems = controller.getModel().getAllEnabledProductItems();
        for (ProductItem productItem : productItems) {
            if (!contains(validProducts, productItem.getProduct())) {
                controller.addError(PurchaseController.Message.corporatePassInvalidProduct,
                        productItem.getProduct().getName());
                return false;
            }
        }
        return true;
    }

    private <T extends Queueable> boolean contains(List<T> validClasses, T courseClass) {
        for (T aClass : validClasses) {
            if (aClass.getId().equals(courseClass.getId()))
                return true;
        }
        return false;
    }

    public static ProductsValidate valueOf(CorporatePass corporatePass, PurchaseController controller) {
        ProductsValidate result = new ProductsValidate();
        result.corporatePass = corporatePass;
        result.controller = controller;
        return result;
    }

}
