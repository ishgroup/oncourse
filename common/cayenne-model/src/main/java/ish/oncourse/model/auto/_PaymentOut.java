package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.common.types.ConfirmationStatus;
import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentOutTransaction;

/**
 * Class _PaymentOut was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PaymentOut extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CONFIRMATION_STATUS_PROPERTY = "confirmationStatus";
    public static final String CREATED_PROPERTY = "created";
    public static final String CREDIT_CARD_CVV_PROPERTY = "creditCardCVV";
    public static final String CREDIT_CARD_TYPE_PROPERTY = "creditCardType";
    public static final String DATE_BANKED_PROPERTY = "dateBanked";
    public static final String DATE_PAID_PROPERTY = "datePaid";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PAYMENT_IN_TXN_REFERENCE_PROPERTY = "paymentInTxnReference";
    public static final String SOURCE_PROPERTY = "source";
    public static final String STATUS_PROPERTY = "status";
    public static final String STATUS_NOTES_PROPERTY = "statusNotes";
    public static final String TOTAL_AMOUNT_PROPERTY = "totalAmount";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String PAYMENT_OUT_TRANSACTIONS_PROPERTY = "paymentOutTransactions";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<ConfirmationStatus> CONFIRMATION_STATUS = Property.create("confirmationStatus", ConfirmationStatus.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<String> CREDIT_CARD_CVV = Property.create("creditCardCVV", String.class);
    public static final Property<CreditCardType> CREDIT_CARD_TYPE = Property.create("creditCardType", CreditCardType.class);
    public static final Property<Date> DATE_BANKED = Property.create("dateBanked", Date.class);
    public static final Property<Date> DATE_PAID = Property.create("datePaid", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> PAYMENT_IN_TXN_REFERENCE = Property.create("paymentInTxnReference", String.class);
    public static final Property<PaymentSource> SOURCE = Property.create("source", PaymentSource.class);
    public static final Property<PaymentStatus> STATUS = Property.create("status", PaymentStatus.class);
    public static final Property<String> STATUS_NOTES = Property.create("statusNotes", String.class);
    public static final Property<Money> TOTAL_AMOUNT = Property.create("totalAmount", Money.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Contact> CONTACT = Property.create("contact", Contact.class);
    public static final Property<List<PaymentOutTransaction>> PAYMENT_OUT_TRANSACTIONS = Property.create("paymentOutTransactions", List.class);

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setConfirmationStatus(ConfirmationStatus confirmationStatus) {
        writeProperty("confirmationStatus", confirmationStatus);
    }
    public ConfirmationStatus getConfirmationStatus() {
        return (ConfirmationStatus)readProperty("confirmationStatus");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setCreditCardCVV(String creditCardCVV) {
        writeProperty("creditCardCVV", creditCardCVV);
    }
    public String getCreditCardCVV() {
        return (String)readProperty("creditCardCVV");
    }

    public void setCreditCardType(CreditCardType creditCardType) {
        writeProperty("creditCardType", creditCardType);
    }
    public CreditCardType getCreditCardType() {
        return (CreditCardType)readProperty("creditCardType");
    }

    public void setDateBanked(Date dateBanked) {
        writeProperty("dateBanked", dateBanked);
    }
    public Date getDateBanked() {
        return (Date)readProperty("dateBanked");
    }

    public void setDatePaid(Date datePaid) {
        writeProperty("datePaid", datePaid);
    }
    public Date getDatePaid() {
        return (Date)readProperty("datePaid");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setPaymentInTxnReference(String paymentInTxnReference) {
        writeProperty("paymentInTxnReference", paymentInTxnReference);
    }
    public String getPaymentInTxnReference() {
        return (String)readProperty("paymentInTxnReference");
    }

    public void setSource(PaymentSource source) {
        writeProperty("source", source);
    }
    public PaymentSource getSource() {
        return (PaymentSource)readProperty("source");
    }

    public void setStatus(PaymentStatus status) {
        writeProperty("status", status);
    }
    public PaymentStatus getStatus() {
        return (PaymentStatus)readProperty("status");
    }

    public void setStatusNotes(String statusNotes) {
        writeProperty("statusNotes", statusNotes);
    }
    public String getStatusNotes() {
        return (String)readProperty("statusNotes");
    }

    public void setTotalAmount(Money totalAmount) {
        writeProperty("totalAmount", totalAmount);
    }
    public Money getTotalAmount() {
        return (Money)readProperty("totalAmount");
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


    public void addToPaymentOutTransactions(PaymentOutTransaction obj) {
        addToManyTarget("paymentOutTransactions", obj, true);
    }
    public void removeFromPaymentOutTransactions(PaymentOutTransaction obj) {
        removeToManyTarget("paymentOutTransactions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentOutTransaction> getPaymentOutTransactions() {
        return (List<PaymentOutTransaction>)readProperty("paymentOutTransactions");
    }


    protected abstract void onPostAdd();

    protected abstract void onPrePersist();

}
