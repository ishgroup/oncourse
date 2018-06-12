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
        
        return !nodes*.enrolments*.findAll{it.selected}*.errors.flatten().empty ||
          !nodes*.applications*.findAll{it.selected}*.errors.flatten().empty ||
          !nodes*.articles*.findAll{it.selected}*.errors.flatten().empty ||
          !nodes*.memberships*.findAll{it.selected}*.errors.flatten().empty ||
          !nodes*.vouchers*.findAll{it.selected}*.errors.flatten().empty || 
          !nodes*.waitingLists*.findAll{it.selected}*.errors.flatten().empty || 
          !checkoutModel.validationErrors.formErrors.empty || 
          !checkoutModel.validationErrors.fieldsErrors.empty
    }
}
