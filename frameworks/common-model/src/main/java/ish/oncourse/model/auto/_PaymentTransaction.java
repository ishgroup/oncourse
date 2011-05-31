package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.PaymentIn;

/**
 * Class _PaymentTransaction was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PaymentTransaction extends CayenneDataObject {

    public static final String CREATED_PROPERTY = "created";
    public static final String IS_FINALISED_PROPERTY = "isFinalised";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String RESPONSE_PROPERTY = "response";
    public static final String SESSION_ID_PROPERTY = "sessionId";
    public static final String TXN_REFERENCE_PROPERTY = "txnReference";
    public static final String PAYMENT_PROPERTY = "payment";

    public static final String ID_PK_COLUMN = "id";

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setIsFinalised(Boolean isFinalised) {
        writeProperty("isFinalised", isFinalised);
    }
    public Boolean getIsFinalised() {
        return (Boolean)readProperty("isFinalised");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setResponse(String response) {
        writeProperty("response", response);
    }
    public String getResponse() {
        return (String)readProperty("response");
    }

    public void setSessionId(String sessionId) {
        writeProperty("sessionId", sessionId);
    }
    public String getSessionId() {
        return (String)readProperty("sessionId");
    }

    public void setTxnReference(String txnReference) {
        writeProperty("txnReference", txnReference);
    }
    public String getTxnReference() {
        return (String)readProperty("txnReference");
    }

    public void setPayment(PaymentIn payment) {
        setToOneTarget("payment", payment, true);
    }

    public PaymentIn getPayment() {
        return (PaymentIn)readProperty("payment");
    }


    protected abstract void onPrePersist();

}
