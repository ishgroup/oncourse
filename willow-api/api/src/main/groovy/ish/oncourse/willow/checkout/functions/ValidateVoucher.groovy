package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext


class ValidateVoucher extends Validate<Voucher>{

    String payerId

    ValidateVoucher(ObjectContext context, College college, String payerId) {
        this(context, college)
        this.payerId = payerId
    }
    
    ValidateVoucher(ObjectContext context, College college) {
        super(context, college)
    }

    @Override
    ValidateVoucher validate(Voucher voucher) {
        Money price =  voucher.price ? new Money(voucher.price) : Money.ZERO
        validate(new GetProduct(context, college, voucher.productId).get() as VoucherProduct, price, voucher.contactId)  
    }


    ValidateVoucher validate(VoucherProduct product, Money price, String contactId) {
        
        if (payerId && payerId != contactId) {
            errors << "Voucher purchase avalible for payer only: $product.name".toString()
        } else if (product.redemptionCourses.empty && product.priceExTax == null && !price.isGreaterThan(Money.ZERO)) {
            errors << "Please enter the correct price for voucher: $product.name".toString()
        } else if (!product.redemptionCourses.empty) {
            Money productPrice = new CalculatePrice(product.priceExTax, Money.ZERO, product.taxRate, product.taxAdjustment).calculate().finalPriceToPayIncTax
            if (productPrice != price) {
                errors << "Voucher price is wrong".toString()
            }
        } 
        
        this
    }
}
