package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentOutTransaction;

/**
 * Class _PaymentOut was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PaymentOut extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String CREDIT_CARD_CVV_PROPERTY = "creditCardCVV";
    public static final String CREDIT_CARD_TYPE_PROPERTY = "creditCardType";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PAYMENT_IN_TXN_REFERENCE_PROPERTY = "paymentInTxnReference";
    public static final String SOURCE_PROPERTY = "source";
    public static final String STATUS_PROPERTY = "status";
    public static final String TOTAL_AMOUNT_PROPERTY = "totalAmount";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String PAYMENT_OUT_TRANSACTIONS_PROPERTY = "paymentOutTransactions";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
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

    public void setSource(String source) {
        writeProperty("source", source);
    }
    public String getSource() {
        return (String)readProperty("source");
    }

    public void setStatus(PaymentStatus status) {
        writeProperty("status", status);
    }
    public PaymentStatus getStatus() {
        return (PaymentStatus)readProperty("status");
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        writeProperty("totalAmount", totalAmount);
    }
    public BigDecimal getTotalAmount() {
        return (BigDecimal)readProperty("totalAmount");
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


}
