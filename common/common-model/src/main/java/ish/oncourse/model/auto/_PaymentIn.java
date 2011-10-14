package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.model.Student;

/**
 * Class _PaymentIn was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PaymentIn extends CayenneDataObject {

    public static final String AMOUNT_PROPERTY = "amount";
    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String CREDIT_CARD_CVV_PROPERTY = "creditCardCVV";
    public static final String CREDIT_CARD_EXPIRY_PROPERTY = "creditCardExpiry";
    public static final String CREDIT_CARD_NAME_PROPERTY = "creditCardName";
    public static final String CREDIT_CARD_NUMBER_PROPERTY = "creditCardNumber";
    public static final String CREDIT_CARD_TYPE_PROPERTY = "creditCardType";
    public static final String DATE_BANKED_PROPERTY = "dateBanked";
    public static final String GATEWAY_REFERENCE_PROPERTY = "gatewayReference";
    public static final String GATEWAY_RESPONSE_PROPERTY = "gatewayResponse";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String SESSION_ID_PROPERTY = "sessionId";
    public static final String SOURCE_PROPERTY = "source";
    public static final String STATUS_PROPERTY = "status";
    public static final String STATUS_NOTES_PROPERTY = "statusNotes";
    public static final String TYPE_PROPERTY = "type";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String PAYMENT_IN_LINES_PROPERTY = "paymentInLines";
    public static final String PAYMENT_TRANSACTIONS_PROPERTY = "paymentTransactions";
    public static final String STUDENT_PROPERTY = "student";

    public static final String ID_PK_COLUMN = "id";

    public void setAmount(BigDecimal amount) {
        writeProperty("amount", amount);
    }
    public BigDecimal getAmount() {
        return (BigDecimal)readProperty("amount");
    }

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

    public void setCreditCardExpiry(String creditCardExpiry) {
        writeProperty("creditCardExpiry", creditCardExpiry);
    }
    public String getCreditCardExpiry() {
        return (String)readProperty("creditCardExpiry");
    }

    public void setCreditCardName(String creditCardName) {
        writeProperty("creditCardName", creditCardName);
    }
    public String getCreditCardName() {
        return (String)readProperty("creditCardName");
    }

    public void setCreditCardNumber(String creditCardNumber) {
        writeProperty("creditCardNumber", creditCardNumber);
    }
    public String getCreditCardNumber() {
        return (String)readProperty("creditCardNumber");
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

    public void setGatewayReference(String gatewayReference) {
        writeProperty("gatewayReference", gatewayReference);
    }
    public String getGatewayReference() {
        return (String)readProperty("gatewayReference");
    }

    public void setGatewayResponse(String gatewayResponse) {
        writeProperty("gatewayResponse", gatewayResponse);
    }
    public String getGatewayResponse() {
        return (String)readProperty("gatewayResponse");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setSessionId(String sessionId) {
        writeProperty("sessionId", sessionId);
    }
    public String getSessionId() {
        return (String)readProperty("sessionId");
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

    public void setType(PaymentType type) {
        writeProperty("type", type);
    }
    public PaymentType getType() {
        return (PaymentType)readProperty("type");
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


    public void addToPaymentTransactions(PaymentTransaction obj) {
        addToManyTarget("paymentTransactions", obj, true);
    }
    public void removeFromPaymentTransactions(PaymentTransaction obj) {
        removeToManyTarget("paymentTransactions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentTransaction> getPaymentTransactions() {
        return (List<PaymentTransaction>)readProperty("paymentTransactions");
    }


    public void setStudent(Student student) {
        setToOneTarget("student", student, true);
    }

    public Student getStudent() {
        return (Student)readProperty("student");
    }


    protected abstract void onPostAdd();

    protected abstract void onPreUpdate();

    protected abstract void onPrePersist();

}
