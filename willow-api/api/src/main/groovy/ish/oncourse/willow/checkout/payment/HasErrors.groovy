package ish.oncourse.willow.checkout.payment

import ish.oncourse.willow.model.checkout.CheckoutModel

class HasErrors {
    
    CheckoutModel checkoutModel
    List<String> errors = []

    HasErrors(CheckoutModel checkoutModel) {
        this.checkoutModel = checkoutModel
    }
    
    boolean hasErrors() {
        
        errors  +=  checkoutModel.purchaseItemsList.enrolments.errors
        errors  +=  checkoutModel.purchaseItemsList.applications.errors
        errors  +=  checkoutModel.purchaseItemsList.articles.errors
        errors  +=  checkoutModel.purchaseItemsList.memberships.errors
        errors  +=  checkoutModel.purchaseItemsList.vouchers.errors

        return !errors.empty
    }
}
