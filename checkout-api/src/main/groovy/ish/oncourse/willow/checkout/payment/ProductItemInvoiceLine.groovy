package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Product
import ish.oncourse.model.ProductItem
import ish.oncourse.model.Tax
import ish.util.InvoiceUtil

@CompileStatic
class ProductItemInvoiceLine {

    ProductItem productItem
    Contact contact
    Money priceExTax
    private Tax taxOverride

    ProductItemInvoiceLine(ProductItem productItem, Contact contact, Money priceExTax, Tax taxOverride) {
        this.productItem = productItem
        this.contact = contact
        this.priceExTax = priceExTax
        this.taxOverride = taxOverride
    }

    InvoiceLine create() {

        Product product = productItem.product
        InvoiceLine invoiceLine = productItem.objectContext.newObject(InvoiceLine)
        invoiceLine.description = "$contact.fullName ($product.sku $product.name)"
        invoiceLine.title = "$contact.fullName $product.name"
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, taxOverride?.rate?:product.taxRate, taxOverride ? Money.ZERO : product.taxAdjustment, BigDecimal.ONE)
        productItem.invoiceLine = invoiceLine
        invoiceLine.college = productItem.college

        return invoiceLine
    }
}
