package ish.oncourse.willow.checkout.payment

import ish.oncourse.willow.model.checkout.CheckoutModel

class HasErrors {
    
    CheckoutModel checkoutModel
    List<String> errors = []

    HasErrors(CheckoutModel checkoutModel) {
        this.checkoutModel = checkoutModel
    }
    
    boolean hasErrors() {
        
        errors  +=  checkoutModel.purchaseItemsList.enrolments.errors.flatten()
        errors  +=  checkoutModel.purchaseItemsList.applications.errors.flatten()
        errors  +=  checkoutModel.purchaseItemsList.articles.errors.flatten()
        errors  +=  checkoutModel.purchaseItemsList.memberships.errors.flatten()
        errors  +=  checkoutModel.purchaseItemsList.vouchers.errors.flatten()

        return !errors.empty
    }
}
