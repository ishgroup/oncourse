package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Product
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext

@CompileStatic
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
        persistentProduct = new GetProduct(context, college, voucher.productId).get()
        validate(persistentProduct as VoucherProduct, voucher.price, voucher.total,  voucher.contactId, voucher.quantity)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    ValidateVoucher validate(VoucherProduct product, Double price, Double total, String contactId, Integer quantity) {
        if (!product.isOnSale) {
            errors << "Not available for purchase".toString()
        } else if (payerId && payerId != contactId) {
            errors << "Voucher purchase avalible for payer only: $product.name".toString()
        } else if (product.redemptionCourses.empty && product.priceExTax == null && !price.toMoney().isGreaterThan(Money.ZERO)) {
            errors << "Please enter the correct price for voucher: $product.name".toString()
        } else if (!product.redemptionCourses.empty) {
            Money productPrice = new CalculatePrice(product.priceExTax, Money.ZERO, product.taxRate, product.taxAdjustment, new BigDecimal(quantity)).calculate().finalPriceToPayIncTax
            if (productPrice != total.toMoney()) {
                errors << "Voucher price is wrong".toString()
            }
        } 
        
        this
    }
}
