package ish.oncourse.model.auto;

import ish.oncourse.model.PaymentIn;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;

/**
 * Class _PaymentTransaction was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PaymentTransaction extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String IS_FINALISED_PROPERTY = "isFinalised";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String RESPONSE_PROPERTY = "response";
    @Deprecated
    public static final String SOAP_RESPONSE_PROPERTY = "soapResponse";
    @Deprecated
    public static final String TXN_REFERENCE_PROPERTY = "txnReference";
    @Deprecated
    public static final String PAYMENT_PROPERTY = "payment";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Boolean> IS_FINALISED = new Property<Boolean>("isFinalised");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> RESPONSE = new Property<String>("response");
    public static final Property<String> SOAP_RESPONSE = new Property<String>("soapResponse");
    public static final Property<String> TXN_REFERENCE = new Property<String>("txnReference");
    public static final Property<PaymentIn> PAYMENT = new Property<PaymentIn>("payment");

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

    public void setSoapResponse(String soapResponse) {
        writeProperty("soapResponse", soapResponse);
    }
    public String getSoapResponse() {
        return (String)readProperty("soapResponse");
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


    protected abstract void onPreUpdate();

    protected abstract void onPrePersist();

}
