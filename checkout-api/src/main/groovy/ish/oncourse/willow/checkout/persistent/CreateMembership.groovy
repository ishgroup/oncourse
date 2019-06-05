package ish.oncourse.willow.checkout.persistent

import ish.common.types.ProductStatus
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Membership
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.Tax
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.checkout.payment.ProductItemInvoiceLine
import ish.util.ProductUtil
import org.apache.cayenne.ObjectContext

class CreateMembership {
    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Membership m
    private Contact contact
    private Invoice invoice
    private ProductStatus status
    private Tax taxOverridden

    CreateMembership(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Membership m, Contact contact, Invoice invoice, ProductStatus status, Tax taxOverridden) {
        this.context = context
        this.college = college
        this.m = m
        this.contact = contact
        this.invoice = invoice
        this.status = status
        this.taxOverridden = taxOverridden
    }

    void create() {
        MembershipProduct mp = new GetProduct(context, college, m.productId).get() as MembershipProduct
        Membership membership = context.newObject(Membership)
        membership.college = college
        membership.contact = contact
        membership.expiryDate = ProductUtil.calculateExpiryDate(new Date(), mp.expiryType, mp.expiryDays)
        membership.product = mp
        membership.status = status
        InvoiceLine invoiceLine = new ProductItemInvoiceLine(membership, contact, membership.product.priceExTax, taxOverridden).create()
        invoiceLine.invoice = invoice
    }
}
