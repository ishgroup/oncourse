package ish.oncourse.willow.checkout.persistent

import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CorporatePass
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentInLine
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.time.DateUtils

class CreateInvoice {

    private ObjectContext context
    private College college
    private WebSite webSite
    private Contact contact

    private Invoice invoice

    CreateInvoice(ObjectContext context, College college, WebSite webSite, Contact contact) {
        this.context = context
        this.college = college
        this.webSite = webSite
        this.contact = contact

    }

    private void create() {
        invoice = context.newObject(Invoice)
        // fill the invoice with default values
        invoice.invoiceDate = DateUtils.setHours(new Date(), 12)
        invoice.dateDue = new Date()
        invoice.amountOwing = Money.ZERO
        invoice.source = PaymentSource.SOURCE_WEB
        invoice.college = college
        invoice.webSite = webSite
        invoice.contact = contact
        invoice.billToAddress = contact.address

    }

    Invoice forPaymentModel(PaymentIn paymentIn, String sessionId) {
        create()

        invoice.confirmationStatus = ConfirmationStatus.DO_NOT_SEND
        invoice.sessionId = sessionId

        PaymentInLine paymentInLine = context.newObject(PaymentInLine)
        paymentInLine.invoice = invoice
        paymentInLine.paymentIn = paymentIn
        paymentInLine.amount = Money.ZERO
        paymentInLine.college = college

        invoice
    }

    Invoice forCorpPassModel(CorporatePass corporatePass, String reference) {
        create()

        invoice.confirmationStatus = ConfirmationStatus.NOT_SENT
        invoice.corporatePassUsed = corporatePass
        invoice.customerReference = reference

        invoice
    }

}
