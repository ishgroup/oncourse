package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.*
import ish.util.InvoiceUtil
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

@CompileStatic
class ProductsItemInvoiceLine {

    ObjectContext context
    List<ProductItem> productItems
    Contact contact
    Money priceExTax
    private Tax taxOverride

    ProductsItemInvoiceLine(ObjectContext context, List<ProductItem> productItems, Contact contact, Money priceExTax, Tax taxOverride) {
        this.context = context
        this.productItems = productItems
        this.contact = contact
        this.priceExTax = priceExTax
        this.taxOverride = taxOverride
    }

    InvoiceLine create() {

        Product product = productItems[0].product
        BigDecimal quantity = new BigDecimal(productItems.size())
        
        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.description = "$contact.fullName ($product.sku $product.name)${quantity > 1 ? ", quantity:$quantity" :''}"
        invoiceLine.title = "$contact.fullName $product.name"
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, taxOverride?.rate?:product.taxRate, taxOverride ? Money.ZERO : product.taxAdjustment, quantity)
        productItems.each {p -> p.invoiceLine = invoiceLine}
        invoiceLine.college = product.college

        return invoiceLine
    }
    
}
