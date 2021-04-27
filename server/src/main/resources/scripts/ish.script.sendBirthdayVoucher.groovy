import org.apache.cayenne.query.SQLTemplate

records = query {
    entity "VoucherProduct"
    query "sku is \"${birthdayProdSku}\""
}
def product = records[0]
def dataContext = product.context
def counter = 0
def request = new SQLTemplate(Contact.class, "SELECT * FROM Contact WHERE DAYOFYEAR(BIRTHDATE) BETWEEN DAYOFYEAR(NOW())+1 AND DAYOFYEAR(NOW())+7")
def contacts = dataContext.performQuery(request)
contacts.each { contact ->

    //must create an invoice/invoiceline for voucher validation
    Invoice invoice = dataContext.newObject(Invoice)
    invoice.contact = contact
    invoice.amountOwing = Money.ZERO
    invoice.source = PaymentSource.SOURCE_ONCOURSE
    invoice.confirmationStatus = ConfirmationStatus.DO_NOT_SEND

    InvoiceLine invoiceLine = dataContext.newObject(InvoiceLine)
    invoiceLine.invoice = invoice
    invoiceLine.account = product.liabilityAccount
    invoiceLine.tax = product.tax
    invoiceLine.title = product.name
    invoiceLine.priceEachExTax = Money.ZERO
    invoiceLine.taxEach = Money.ZERO
    invoiceLine.discountEachExTax = Money.ZERO
    invoiceLine.quantity = 1
    invoiceLine.description = "${contact.getName(true)} (${product.sku} ${product.name})"

    // Creates the voucher from the product
    Voucher gift = dataContext.newObject(Voucher)
    gift.product = product
    gift.redeemableBy = contact
    gift.code = SecurityUtil.generateVoucherCode()
    gift.source = PaymentSource.SOURCE_ONCOURSE
    gift.status = ProductStatus.ACTIVE
    gift.invoiceLine = invoiceLine
    gift.redemptionValue = product.value
    gift.valueOnPurchase = product.value
    gift.redeemedCourseCount = 0
    gift.expiryDate = ProductUtil.calculateExpiryDate(ProductUtil.getToday(), product.expiryType, product.expiryDays)
    gift.confirmationStatus = ConfirmationStatus.DO_NOT_SEND

    message {
        template birthdayVoucherTemplate
        record contact
        voucher gift
    }

    if (++counter == 100) {
        dataContext.commitChanges()
        counter = 0
    }
}
if (counter > 0) {
    dataContext.commitChanges()
}