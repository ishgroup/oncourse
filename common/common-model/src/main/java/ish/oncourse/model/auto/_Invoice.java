package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.WebSite;

/**
 * Class _Invoice was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Invoice extends CayenneDataObject {

    public static final String AMOUNT_OWING_PROPERTY = "amountOwing";
    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String BILL_TO_ADDRESS_PROPERTY = "billToAddress";
    public static final String CREATED_PROPERTY = "created";
    public static final String CUSTOMER_PO_PROPERTY = "customerPO";
    public static final String CUSTOMER_REFERENCE_PROPERTY = "customerReference";
    public static final String DATE_DUE_PROPERTY = "dateDue";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String INVOICE_DATE_PROPERTY = "invoiceDate";
    public static final String INVOICE_NUMBER_PROPERTY = "invoiceNumber";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PUBLIC_NOTES_PROPERTY = "publicNotes";
    public static final String SHIPPING_ADDRESS_PROPERTY = "shippingAddress";
    public static final String SOURCE_PROPERTY = "source";
    public static final String TOTAL_EX_GST_PROPERTY = "totalExGst";
    public static final String TOTAL_GST_PROPERTY = "totalGst";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String CORPORATE_PASS_USED_PROPERTY = "corporatePassUsed";
    public static final String INVOICE_LINES_PROPERTY = "invoiceLines";
    public static final String PAYMENT_IN_LINES_PROPERTY = "paymentInLines";
    public static final String WEB_SITE_PROPERTY = "webSite";

    public static final String ID_PK_COLUMN = "id";

    public void setAmountOwing(Money amountOwing) {
        writeProperty(AMOUNT_OWING_PROPERTY, amountOwing);
    }
    public Money getAmountOwing() {
        return (Money)readProperty(AMOUNT_OWING_PROPERTY);
    }

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setBillToAddress(String billToAddress) {
        writeProperty(BILL_TO_ADDRESS_PROPERTY, billToAddress);
    }
    public String getBillToAddress() {
        return (String)readProperty(BILL_TO_ADDRESS_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setCustomerPO(String customerPO) {
        writeProperty(CUSTOMER_PO_PROPERTY, customerPO);
    }
    public String getCustomerPO() {
        return (String)readProperty(CUSTOMER_PO_PROPERTY);
    }

    public void setCustomerReference(String customerReference) {
        writeProperty(CUSTOMER_REFERENCE_PROPERTY, customerReference);
    }
    public String getCustomerReference() {
        return (String)readProperty(CUSTOMER_REFERENCE_PROPERTY);
    }

    public void setDateDue(Date dateDue) {
        writeProperty(DATE_DUE_PROPERTY, dateDue);
    }
    public Date getDateDue() {
        return (Date)readProperty(DATE_DUE_PROPERTY);
    }

    public void setDescription(String description) {
        writeProperty(DESCRIPTION_PROPERTY, description);
    }
    public String getDescription() {
        return (String)readProperty(DESCRIPTION_PROPERTY);
    }

    public void setInvoiceDate(Date invoiceDate) {
        writeProperty(INVOICE_DATE_PROPERTY, invoiceDate);
    }
    public Date getInvoiceDate() {
        return (Date)readProperty(INVOICE_DATE_PROPERTY);
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        writeProperty(INVOICE_NUMBER_PROPERTY, invoiceNumber);
    }
    public Long getInvoiceNumber() {
        return (Long)readProperty(INVOICE_NUMBER_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setPublicNotes(String publicNotes) {
        writeProperty(PUBLIC_NOTES_PROPERTY, publicNotes);
    }
    public String getPublicNotes() {
        return (String)readProperty(PUBLIC_NOTES_PROPERTY);
    }

    public void setShippingAddress(String shippingAddress) {
        writeProperty(SHIPPING_ADDRESS_PROPERTY, shippingAddress);
    }
    public String getShippingAddress() {
        return (String)readProperty(SHIPPING_ADDRESS_PROPERTY);
    }

    public void setSource(PaymentSource source) {
        writeProperty(SOURCE_PROPERTY, source);
    }
    public PaymentSource getSource() {
        return (PaymentSource)readProperty(SOURCE_PROPERTY);
    }

    public void setTotalExGst(Money totalExGst) {
        writeProperty(TOTAL_EX_GST_PROPERTY, totalExGst);
    }
    public Money getTotalExGst() {
        return (Money)readProperty(TOTAL_EX_GST_PROPERTY);
    }

    public void setTotalGst(Money totalGst) {
        writeProperty(TOTAL_GST_PROPERTY, totalGst);
    }
    public Money getTotalGst() {
        return (Money)readProperty(TOTAL_GST_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setContact(Contact contact) {
        setToOneTarget(CONTACT_PROPERTY, contact, true);
    }

    public Contact getContact() {
        return (Contact)readProperty(CONTACT_PROPERTY);
    }


    public void setCorporatePassUsed(CorporatePass corporatePassUsed) {
        setToOneTarget(CORPORATE_PASS_USED_PROPERTY, corporatePassUsed, true);
    }

    public CorporatePass getCorporatePassUsed() {
        return (CorporatePass)readProperty(CORPORATE_PASS_USED_PROPERTY);
    }


    public void addToInvoiceLines(InvoiceLine obj) {
        addToManyTarget(INVOICE_LINES_PROPERTY, obj, true);
    }
    public void removeFromInvoiceLines(InvoiceLine obj) {
        removeToManyTarget(INVOICE_LINES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLine> getInvoiceLines() {
        return (List<InvoiceLine>)readProperty(INVOICE_LINES_PROPERTY);
    }


    public void addToPaymentInLines(PaymentInLine obj) {
        addToManyTarget(PAYMENT_IN_LINES_PROPERTY, obj, true);
    }
    public void removeFromPaymentInLines(PaymentInLine obj) {
        removeToManyTarget(PAYMENT_IN_LINES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentInLine> getPaymentInLines() {
        return (List<PaymentInLine>)readProperty(PAYMENT_IN_LINES_PROPERTY);
    }


    public void setWebSite(WebSite webSite) {
        setToOneTarget(WEB_SITE_PROPERTY, webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty(WEB_SITE_PROPERTY);
    }


    protected abstract void onPostAdd();

    protected abstract void onPreUpdate();

    protected abstract void onPrePersist();

}
