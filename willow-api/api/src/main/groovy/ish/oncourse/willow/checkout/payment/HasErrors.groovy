package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import ish.oncourse.willow.model.checkout.CheckoutModel

@CompileStatic
class HasErrors {
    
    CheckoutModel checkoutModel
    List<String> errors = []

    HasErrors(CheckoutModel checkoutModel) {
        this.checkoutModel = checkoutModel
    }
    
    boolean hasErrors() {
        
        errors  +=  checkoutModel.purchaseItemsList.enrolments.errors.flatten() as List<String>
        errors  +=  checkoutModel.purchaseItemsList.applications.errors.flatten() as List<String>
        errors  +=  checkoutModel.purchaseItemsList.articles.errors.flatten() as List<String>
        errors  +=  checkoutModel.purchaseItemsList.memberships.errors.flatten() as List<String>
        errors  +=  checkoutModel.purchaseItemsList.vouchers.errors.flatten() as List<String>

        return !errors.empty
    }
}
