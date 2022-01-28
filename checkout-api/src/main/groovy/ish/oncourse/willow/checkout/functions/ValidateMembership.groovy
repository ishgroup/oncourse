package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.common.types.ExpiryType

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Membership
import org.apache.cayenne.ObjectContext

import static ish.common.types.ProductStatus.ACTIVE

@CompileStatic
class ValidateMembership extends Validate<Membership>{


    ValidateMembership(ObjectContext context, College college) {
        super(context, college)
    }

    @Override
    ValidateMembership validate(Membership membership) {
        validate(new GetProduct(context, college, membership.productId).get() as MembershipProduct, new GetContact(context, college, membership.productId).get())
    }

    ValidateMembership validate(MembershipProduct product, Contact contact) {
        if (!product.isOnSale) {
            errors << "Not available for purchase".toString()
        } else if (contact.memberships.any { ACTIVE == it.status && it.product == product && it.product.expiryType == ExpiryType.LIFETIME }) {
            errors << "$contact.fullName is already has this membership $product.sku.".toString()
        }
        this
    }
}
