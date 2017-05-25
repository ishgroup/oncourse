package ish.oncourse.willow.checkout.payment

import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Product
import ish.oncourse.model.ProductItem
import ish.util.InvoiceUtil

class ProductItemInvoiceLine {

    ProductItem productItem
    Contact contact
    Money priceExTax

    ProductItemInvoiceLine(ProductItem productItem, Contact contact, Money priceExTax) {
        this.productItem = productItem
        this.contact = contact
        this.priceExTax = priceExTax
    }

    InvoiceLine create() {

        Product product = productItem.product
        InvoiceLine invoiceLine = productItem.objectContext.newObject(InvoiceLine)
        invoiceLine.description = "$contact.fullName ($product.sku $product.name)"
        invoiceLine.title = "$contact.fullName $product.name"
        invoiceLine.quantity = BigDecimal.ONE
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, product.taxRate, product.taxAdjustment)
        productItem.invoiceLine = invoiceLine
        invoiceLine.college = productItem.college

        return invoiceLine
    }
}
