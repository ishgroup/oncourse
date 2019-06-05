package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Product
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext

class ValidateVoucher extends Validate<Voucher>{

    String payerId
    Product persistentProduct

    ValidateVoucher(ObjectContext context, College college, String payerId) {
        this(context, college)
        this.payerId = payerId
    }
    
    ValidateVoucher(ObjectContext context, College college) {
        super(context, college)
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    ValidateVoucher validate(Voucher voucher) {
        Money price =  voucher.price?.toMoney() ?: Money.ZERO
        persistentProduct = new GetProduct(context, college, voucher.productId).get()
        validate(persistentProduct as VoucherProduct, price, voucher.contactId, voucher.quantity)
    }


    ValidateVoucher validate(VoucherProduct product, Money price, String contactId, BigDecimal quantity) {
        
        if (payerId && payerId != contactId) {
            errors << "Voucher purchase avalible for payer only: $product.name".toString()
        } else if (product.redemptionCourses.empty && product.priceExTax == null && !price.isGreaterThan(Money.ZERO)) {
            errors << "Please enter the correct price for voucher: $product.name".toString()
        } else if (!product.redemptionCourses.empty) {
            Money productPrice = new CalculatePrice(product.priceExTax, Money.ZERO, product.taxRate, product.taxAdjustment, quantity).calculate().finalPriceToPayIncTax
            if (productPrice != price) {
                errors << "Voucher price is wrong".toString()
            }
        } 
        
        this
    }
}
