package ish.oncourse.model.auto;

import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.AbstractInvoice;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.InvoiceDueDate;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.WebSite;

/**
 * Class _SaleOrder was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _SaleOrder extends AbstractInvoice {

    private static final long serialVersionUID = 1L; 

    public static final String AUTHORISED_REBILLING_CARD_PROPERTY = "authorisedRebillingCard";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String CORPORATE_PASS_USED_PROPERTY = "corporatePassUsed";
    public static final String INVOICE_DUE_DATES_PROPERTY = "invoiceDueDates";
    public static final String INVOICE_LINES_PROPERTY = "invoiceLines";
    public static final String PAYMENT_IN_LINES_PROPERTY = "paymentInLines";
    public static final String WEB_SITE_PROPERTY = "webSite";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<PaymentIn> AUTHORISED_REBILLING_CARD = Property.create("authorisedRebillingCard", PaymentIn.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Contact> CONTACT = Property.create("contact", Contact.class);
    public static final Property<CorporatePass> CORPORATE_PASS_USED = Property.create("corporatePassUsed", CorporatePass.class);
    public static final Property<List<InvoiceDueDate>> INVOICE_DUE_DATES = Property.create("invoiceDueDates", List.class);
    public static final Property<List<InvoiceLine>> INVOICE_LINES = Property.create("invoiceLines", List.class);
    public static final Property<List<PaymentInLine>> PAYMENT_IN_LINES = Property.create("paymentInLines", List.class);
    public static final Property<WebSite> WEB_SITE = Property.create("webSite", WebSite.class);

    public void setAuthorisedRebillingCard(PaymentIn authorisedRebillingCard) {
        setToOneTarget("authorisedRebillingCard", authorisedRebillingCard, true);
    }

    public PaymentIn getAuthorisedRebillingCard() {
        return (PaymentIn)readProperty("authorisedRebillingCard");
    }


    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setContact(Contact contact) {
        setToOneTarget("contact", contact, true);
    }

    public Contact getContact() {
        return (Contact)readProperty("contact");
    }


    public void setCorporatePassUsed(CorporatePass corporatePassUsed) {
        setToOneTarget("corporatePassUsed", corporatePassUsed, true);
    }

    public CorporatePass getCorporatePassUsed() {
        return (CorporatePass)readProperty("corporatePassUsed");
    }


    public void addToInvoiceDueDates(InvoiceDueDate obj) {
        addToManyTarget("invoiceDueDates", obj, true);
    }
    public void removeFromInvoiceDueDates(InvoiceDueDate obj) {
        removeToManyTarget("invoiceDueDates", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceDueDate> getInvoiceDueDates() {
        return (List<InvoiceDueDate>)readProperty("invoiceDueDates");
    }


    public void addToInvoiceLines(InvoiceLine obj) {
        addToManyTarget("invoiceLines", obj, true);
    }
    public void removeFromInvoiceLines(InvoiceLine obj) {
        removeToManyTarget("invoiceLines", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLine> getInvoiceLines() {
        return (List<InvoiceLine>)readProperty("invoiceLines");
    }


    public void addToPaymentInLines(PaymentInLine obj) {
        addToManyTarget("paymentInLines", obj, true);
    }
    public void removeFromPaymentInLines(PaymentInLine obj) {
        removeToManyTarget("paymentInLines", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentInLine> getPaymentInLines() {
        return (List<PaymentInLine>)readProperty("paymentInLines");
    }


    public void setWebSite(WebSite webSite) {
        setToOneTarget("webSite", webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty("webSite");
    }


}
