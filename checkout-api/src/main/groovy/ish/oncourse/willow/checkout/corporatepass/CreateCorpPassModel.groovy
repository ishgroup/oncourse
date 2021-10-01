package ish.oncourse.willow.checkout.corporatepass

import groovy.transform.CompileStatic
import ish.common.GetInvoiceDueDate
import ish.common.types.ConfirmationStatus
import ish.common.types.EnrolmentStatus
import ish.common.types.ProductStatus
import ish.oncourse.enrol.checkout.model.UpdateInvoiceAmount
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CorporatePass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.WebSite
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.persistent.CreateApplication
import ish.oncourse.willow.checkout.persistent.CreateArticle
import ish.oncourse.willow.checkout.persistent.CreateEnrolment
import ish.oncourse.willow.checkout.persistent.CreateInvoice
import ish.oncourse.willow.checkout.persistent.CreateMembership
import ish.oncourse.willow.checkout.persistent.CreateVoucher
import ish.oncourse.willow.checkout.persistent.CreateWaitingList
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.persistence.Preferences
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class CreateCorpPassModel {

    final static Logger logger = LoggerFactory.getLogger(CreateCorpPassModel)

    private ObjectContext context
    private College college
    private WebSite webSite
    private String reference
    private CorporatePass pass
    private CheckoutModel checkoutModel

    private Invoice mainInvoice
    private List<Invoice> paymentPlan = []

    CreateCorpPassModel(ObjectContext context, College college, WebSite webSite, String reference, CorporatePass pass, CheckoutModel checkoutModel) {
        this.context = context
        this.webSite = this.context.localObject(webSite)
        this.college = this.context.localObject(college)
        this.reference = reference
        this.pass = pass
        this.checkoutModel = checkoutModel
    }

    CreateCorpPassModel create() {
        processNodes()
        updateSumm()
        adjustSortOrder()
        this
    }

    private void processNodes() {
        checkoutModel.contactNodes.each { node ->
            Contact contact = new GetContact(context, college, node.contactId).get(false)

            node.enrolments.findAll{it.selected}.each { e ->
                new CreateEnrolment(context, college, e, contact, EnrolmentStatus.SUCCESS, ConfirmationStatus.NOT_SENT, pass.contact.taxOverride, { Enrolment enrolment, InvoiceLine il ->
                    if (enrolment.courseClass.paymentPlanLines.empty) {
                        il.invoice = getInvoice()
                    } else {
                        Invoice paymentPlanInvoice = createInvoice()
                        il.invoice = paymentPlanInvoice
                        paymentPlan << paymentPlanInvoice
                    }}).create()
            }

            node.applications.findAll{it.selected}.each { a ->
                new CreateApplication(context, college, a, contact).create()
            }
            node.articles.findAll{it.selected}.each { a ->
                new CreateArticle(context, college, a, contact, getInvoice(), ProductStatus.ACTIVE, pass.contact.taxOverride).create()
            }
            node.memberships.findAll{it.selected}.each { m ->
                new CreateMembership(context, college, m, contact, getInvoice(), ProductStatus.ACTIVE, pass.contact.taxOverride).create()
            }
            node.vouchers.findAll{it.selected}.each { v ->
                new CreateVoucher(context, college, v, contact, getInvoice(), ProductStatus.ACTIVE,  ConfirmationStatus.NOT_SENT).create()
            }
            node.waitingLists.findAll { it.selected }.each { w ->
                new CreateWaitingList(context, college, w, contact).create()
            }
        }
    }


    private void updateSumm() {
        if (mainInvoice) {
            UpdateInvoiceAmount.valueOf(mainInvoice, pass).update()
            mainInvoice.updateAmountOwing()
            adjustDueDate()
        }
        
        paymentPlan.each { invoice ->
            UpdateInvoiceAmount.valueOf(invoice, pass).update()
            invoice.updateAmountOwing()
        }
    }

    private Invoice getInvoice() {
        if (!mainInvoice) {
            mainInvoice = createInvoice()
        }
        mainInvoice
    }


    private Invoice createInvoice() {
        new CreateInvoice(context, college, webSite, pass.contact)
                .forCorpPassModel(pass, reference)
    }
    
    private void  adjustDueDate() {
        Integer defaultTerms = new GetPreference(college, Preferences.ACCOUNT_INVOICE_TERMS, context).integerValue
        Integer contactTerms = pass.contact.invoiceTerms
        Date dueDate = GetInvoiceDueDate.valueOf(defaultTerms, contactTerms).get()
        mainInvoice.dateDue = dueDate
    }

    private void adjustSortOrder() {
        adjustSortOrder(mainInvoice)
        for (Invoice invoice : paymentPlan) {
            adjustSortOrder(invoice)
        }
    }
    
    private void adjustSortOrder(Invoice invoice) {
        if (invoice) {
            List<InvoiceLine> invoiceLines = invoice.invoiceLines
            for (int i = 0; i < invoiceLines.size(); i++) {
                InvoiceLine invoiceLine = invoiceLines[i]
                invoiceLine.sortOrder = i
            }
        }
    }
}
