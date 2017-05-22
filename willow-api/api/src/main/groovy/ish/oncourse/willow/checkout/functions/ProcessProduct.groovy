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

    Article article
    Membership membership
    Voucher voucher
    Product persistentProduct
    
    ProcessProduct(ObjectContext context, Contact contact, College college, String productId) {
        this.context = context
        this.contact = contact
        this.college = college
        this.productId = productId
    }

    ProcessProduct process() {
        Product persistentProduct = new GetProduct(context, college, productId).get()
        ProductType productType =TypesUtil.getEnumForDatabaseValue(persistentProduct.type, ProductType.class)
        
        switch (productType) {
            case ARTICLE:
                article = new Article().with { a ->
                    a.contactId = contact.id.toString()
                    a.productId = persistentProduct.id.toString()
                    a.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().toPlainString()
                    a
                }
                break
            case MEMBERSHIP:
                membership = new Membership().with { m ->
                    m.contactId = contact.id.toString()
                    m.productId = p.id.toString()
                    m.price = new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().toPlainString()
                    ValidateMembership validateMembership = new ValidateMembership(context, college).validate(persistentProduct as MembershipProduct, contact)
                    m.errors += validateMembership.errors
                    m.warnings += validateMembership.warnings
                    m
                }
                break
            case VOUCHER:
                voucher = new Voucher().with { v ->
                    VoucherProduct voucher = persistentProduct as VoucherProduct
                    v.contactId = contact.id.toString()
                    v.productId = voucher.id.toString()

                    Money value = Money.ZERO
                    if (voucher.redemptionCourses.empty && voucher.priceExTax == null) {
                        v.price =  DEFAULT_VOUCHER_PRICE.toString()
                        value = DEFAULT_VOUCHER_PRICE
                        v.value =  value.toPlainString()
                    } else if (persistentProduct.priceExTax != null) {
                        v.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().toPlainString()
                        value = voucher.value
                        v.value = value.toPlainString()
                    } else {
                        v.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment).calculate().toPlainString()
                        v.classes += voucher.redemptionCourses.collect{c -> c.name}
                    }

                    ValidateVoucher validateVoucher = new ValidateVoucher(context, college).validate(voucher as VoucherProduct, value)
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
