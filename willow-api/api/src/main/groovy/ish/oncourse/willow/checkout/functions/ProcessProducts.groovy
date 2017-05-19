package ish.oncourse.willow.checkout.functions

import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.model.Product
import ish.util.InvoiceUtil
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

import static ish.common.types.ProductType.ARTICLE
import static ish.common.types.ProductType.MEMBERSHIP
import static ish.common.types.ProductType.VOUCHER

class ProcessProducts {

    final static  Logger logger = LoggerFactory.getLogger(ProcessClasses.class)
    private static final Money DEFAULT_VOUCHER_PRICE = new Money("100.00");

    ObjectContext context
    Contact contact
    College college
    List<String> productIds

    List<Article> articles = []
    List<Membership> memberships = []
    List<Voucher> vouchers = []
    
    ProcessProducts(ObjectContext context, Contact contact, College college, List<String> productIds) {
        this.context = context
        this.contact = contact
        this.college = college
        this.productIds = productIds
    }

    ProcessProducts process() {

        if (productIds.unique().size() < productIds.size()) {
            logger.error("product list contains duplicate entries: $productIds")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'product list contains duplicate entries')).build())
        }
        
        productIds.each { id ->
            Product p = new GetProduct(context, college, id).get()

            ProductType productType =TypesUtil.getEnumForDatabaseValue(p.type, ProductType.class)
            switch (productType) {
                case ARTICLE:
                    articles << new Article().with { a ->
                        a.contactId = contact.id.toString()
                        a.productId = p.id.toString()
                        a.price = calculatePrice(p).toString()
                        a
                    }
                    break
                case MEMBERSHIP:
                    memberships << new Membership().with { m ->
                        m.contactId = contact.id.toString()
                        m.productId = p.id.toString()
                        m.price = calculatePrice(p).toString()
                        ValidateMembership validateMembership = new ValidateMembership(context, college).validate(p as MembershipProduct, contact)
                        m.errors += validateMembership.errors
                        m.warnings += validateMembership.warnings
                        m
                    }
                    break
                case VOUCHER:
                    vouchers << new Voucher().with { v ->
                        VoucherProduct voucher = p as VoucherProduct
                        v.contactId = contact.id.toString()
                        v.productId = voucher.id.toString()

                        Money value = Money.ZERO
                        if (voucher.redemptionCourses.empty && voucher.priceExTax == null) {
                            v.price =  DEFAULT_VOUCHER_PRICE.toString()
                            value = DEFAULT_VOUCHER_PRICE
                            v.value =  value.toString()
                        } else if (p.priceExTax != null) {
                            v.price = calculatePrice(p).toString()
                            value = voucher.value
                            v.value = value.toString()
                        } else {
                            v.price = calculatePrice(p).toString()
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
        }
        
        this
    }
    
    private Money calculatePrice(Product p) {
        InvoiceLine invoiceLine = new InvoiceLine()
        InvoiceUtil.fillInvoiceLine(invoiceLine, p.priceExTax, Money.ZERO, p.taxRate, p.taxAdjustment)
        invoiceLine.priceEachIncTax
    }
    
}
