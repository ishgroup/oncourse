package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.Product
import ish.oncourse.model.Tax
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.functions.field.GetProductFields
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
    Integer quantityVal
    String payerId

    Article article
    Membership membership
    Voucher voucher
    Product persistentProduct
    private Tax taxOverridden
    
    ProcessProduct(ObjectContext context, Contact contact, College college, String productId, Integer quantity, String payerId, Tax taxOverridden) {
        this.context = context
        this.contact = contact
        this.college = college
        this.productId = productId
        this.payerId = payerId
        this.quantityVal = quantity
        this.taxOverridden = taxOverridden
    }
    
    @CompileStatic(TypeCheckingMode.SKIP)
    ProcessProduct process() {
        persistentProduct = new GetProduct(context, college, productId).get()
        ProductType productType = TypesUtil.getEnumForDatabaseValue(persistentProduct.type, ProductType.class)
        
        switch (productType) {
            case ARTICLE:
                article = new Article().with { a ->
                    a.contactId = contact.id.toString()
                    a.productId = persistentProduct.id.toString()
                    a.selected = true
                    a.quantity = quantityVal
                    a.total = new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, taxOverridden, persistentProduct, new BigDecimal(quantityVal)).calculate().finalPriceToPayIncTax.doubleValue()
                    a.price = new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, taxOverridden, persistentProduct, BigDecimal.ONE).calculate().finalPriceToPayIncTax.doubleValue()
                    a.fieldHeadings = new GetProductFields(persistentProduct, productType).get()
                    a
                }
                ValidateArticle validate = new ValidateArticle(context, college, taxOverridden).validate(article)
                article.errors += validate.errors
                article.warnings += validate.warnings
                break
            case MEMBERSHIP:
                membership = new Membership().with { m ->
                    m.contactId = contact.id.toString()
                    m.productId = persistentProduct.id.toString()
                    m.selected = true
                    m.price = new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, taxOverridden, persistentProduct, BigDecimal.ONE).calculate().finalPriceToPayIncTax.doubleValue()
                    m.fieldHeadings = new GetProductFields(persistentProduct, productType).get()

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
                    v.selected = true

                    v.quantity = quantityVal
                    
                    if (voucher.redemptionCourses.empty && voucher.value == null) {
                        v.price =  DEFAULT_VOUCHER_PRICE.doubleValue()
                        v.value = v.price
                        v.total = DEFAULT_VOUCHER_PRICE.multiply(new BigDecimal(quantityVal)).toBigDecimal()
                        v.isEditablePrice = true
                    } else if (voucher.value != null) {
                        v.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment, BigDecimal.ONE).calculate().finalPriceToPayIncTax.doubleValue()
                        v.total =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment, new BigDecimal(quantityVal)).calculate().finalPriceToPayIncTax.doubleValue()
                        v.value = voucher.value.doubleValue()
                        v.isEditablePrice = false
                    } else {
                        v.price =  new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment, BigDecimal.ONE).calculate().finalPriceToPayIncTax.doubleValue()
                        v.total = new CalculatePrice(persistentProduct.priceExTax, Money.ZERO, persistentProduct.taxRate, persistentProduct.taxAdjustment, new BigDecimal(quantityVal)).calculate().finalPriceToPayIncTax.doubleValue()
                        v.classes += voucher.redemptionCourses.collect{c -> c.name}
                        v.isEditablePrice = false
                    }
                    v.fieldHeadings = new GetProductFields(persistentProduct, productType).get()

                    ValidateVoucher validateVoucher = new ValidateVoucher(context, college, payerId).validate(voucher as VoucherProduct, v.price, v.total, v.contactId,  quantityVal)
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
