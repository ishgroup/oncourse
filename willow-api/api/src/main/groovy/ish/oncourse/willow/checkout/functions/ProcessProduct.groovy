package ish.oncourse.willow.checkout.functions

import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.Product
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext

import static ish.common.types.ProductType.ARTICLE
import static ish.common.types.ProductType.MEMBERSHIP
import static ish.common.types.ProductType.VOUCHER

class ProcessProduct {
    
    private static final Money DEFAULT_VOUCHER_PRICE = new Money("100.00")

    ObjectContext context
    Contact contact
    College college
    String productId
    String payerId

    Article article
    Membership membership
    Voucher voucher
    Product persistentProduct
    
    ProcessProduct(ObjectContext context, Contact contact, College college, String productId, String payerId) {
        this.context = context
        this.contact = contact
        this.college = college
        this.productId = productId
        this.payerId = payerId
    }

    ProcessProduct process() {
        persistentProduct = new GetProduct(context, college, productId).get()
        ProductType productType =TypesUtil.getEnumForDatabaseValue(persistentProduct.type, ProductType.class)
        
        switch (productType) {
            case ARTICLE:
                article = new Article().with { a ->
                    a.contactId = contact.id.toString()
                    a.productId = persistentProduct.id.toString()
                    a.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().finalPriceToPayIncTax.toPlainString()
                    a
                }
                break
            case MEMBERSHIP:
                membership = new Membership().with { m ->
                    m.contactId = contact.id.toString()
                    m.productId = persistentProduct.id.toString()
                    m.price = new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().finalPriceToPayIncTax.toPlainString()
                    ValidateMembership validateMembership = new ValidateMembership(context, college).validate(persistentProduct as MembershipProduct, contact)
                    m.errors += validateMembership.errors
                    m.warnings += validateMembership.warnings
                    m
                }
                break
            case VOUCHER:
                if (payerId && payerId != contact.id.toString()) {
                    break
                }
                voucher = new Voucher().with { v ->
                    VoucherProduct voucher = persistentProduct as VoucherProduct
                    v.contactId = contact.id.toString()
                    v.productId = voucher.id.toString()

                    Money value = Money.ZERO
                    if (voucher.redemptionCourses.empty && voucher.value == null) {
                        v.price =  DEFAULT_VOUCHER_PRICE.toPlainString()
                        value = DEFAULT_VOUCHER_PRICE
                        v.value =  value.toPlainString()
                        v.isEditablePrice = true
                    } else if (voucher.value != null) {
                        v.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().finalPriceToPayIncTax.toPlainString()
                        value = voucher.value
                        v.value = value.toPlainString()
                        v.isEditablePrice = false
                    } else {
                        v.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().finalPriceToPayIncTax.toPlainString()
                        v.classes += voucher.redemptionCourses.collect{c -> c.name}
                        v.isEditablePrice = false
                    }

                    ValidateVoucher validateVoucher = new ValidateVoucher(context, college, payerId).validate(voucher as VoucherProduct, value, v.contactId)
                    v.errors += validateVoucher.errors
                    v.warnings += validateVoucher.warnings
                    v
                }
                break
            default:
                throw new IllegalArgumentException()
        }
        this
    }
    

}
