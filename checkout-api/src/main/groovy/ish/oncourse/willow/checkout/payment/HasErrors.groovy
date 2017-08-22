package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.ContactNode

@CompileStatic
class HasErrors {
    
    CheckoutModel checkoutModel

    HasErrors(CheckoutModel checkoutModel) {
        this.checkoutModel = checkoutModel
    }
    
    boolean hasErrors() {
        List<ContactNode> nodes = checkoutModel.contactNodes
        return !nodes.enrolments.errors.flatten().empty ||
          !nodes.applications.errors.flatten().empty ||
          !nodes.articles.errors.flatten().empty ||
          !nodes.memberships.errors.flatten().empty ||
          !nodes.vouchers.errors.flatten().empty 
    }
}
