package ish.oncourse.willow.checkout.functions

import ish.common.types.ExpiryType

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Membership
import org.apache.cayenne.ObjectContext

import static ish.common.types.ProductStatus.ACTIVE

class ValidateMembership extends Validate<Membership>{


    ValidateMembership(ObjectContext context, College college) {
        super(context, college)
    }

    @Override
    ValidateMembership validate(Membership membership) {
        validate(new GetProduct(context, college, membership.productId).get() as MembershipProduct, new GetContact(context, college, membership.productId).get())
    }

    ValidateMembership validate(MembershipProduct product, Contact contact) {
        if (contact.memberships.any { ACTIVE == it.status && it.product == product && it.product.expiryType == ExpiryType.LIFETIME }) {
            errors << "$contact.fullName is already has this membership $product.sku."
        }
        this
    }
    
    private boolean validateVoucher(VoucherProduct product, Money price) {
        if (product.redemptionCourses.empty && product.priceExTax == null && !price.isGreaterThan(Money.ZERO)) {
           errors << "Please enter the correct price for voucher: $product.name"
        }
    }

}
