package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext


class ValidateVoucher extends Validate<Voucher>{
    
    ValidateVoucher(ObjectContext context, College college) {
        super(context, college)
    }

    @Override
    ValidateVoucher validate(Voucher voucher) {
        Money price =  voucher.price ? new Money(voucher.price) : Money.ZERO
        validate(new GetProduct(context, college, voucher.productId).get() as VoucherProduct, price)    }


    ValidateVoucher validate(VoucherProduct product, Money price ) {
        
        if (product.redemptionCourses.empty && product.priceExTax == null && !price.isGreaterThan(Money.ZERO)) {
            errors << "Please enter the correct price for voucher: $product.name".toString()
        }
        this
    }
}
