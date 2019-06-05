package ish.oncourse.willow.checkout.payment

import ish.math.Money
import ish.oncourse.model.*
import ish.util.InvoiceUtil
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

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

        List<Product> products = productItems.collect{p -> p.product}

        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.description = createInvoiceLineDescription(contact, products)
        invoiceLine.title = createInvoiceLineTitle(contact, products)
        InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, taxOverride?.rate?:products[0].taxRate, taxOverride ? Money.ZERO : products[0].taxAdjustment, new BigDecimal(products.size()))
        productItems.each {p -> p.invoiceLine = invoiceLine}
        invoiceLine.college = products[0].college

        return invoiceLine
    }

    private String createInvoiceLineDescription(Contact contact, List<Product> products) {
        List<String> joinList = new ArrayList<>()
        products.each {p -> joinList.add("$p.sku $p.name".toString())}
        String productsDescription = StringUtils.join(joinList, " ")
        return "$contact.fullName ($productsDescription)"
    }

    private String createInvoiceLineTitle(Contact contact, List<Product> products) {
        List<String> joinList = new ArrayList<>()
        products.each {p -> joinList.add("$p.name".toString())}
        String productsTitle = StringUtils.join(joinList, " ")
        return "$contact.fullName $productsTitle"
    }
}
