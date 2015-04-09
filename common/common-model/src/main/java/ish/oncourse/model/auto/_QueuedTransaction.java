package ish.oncourse.model.auto;

import ish.oncourse.model.College;
import ish.oncourse.model.QueuedRecord;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;
import java.util.List;

/**
 * Class _QueuedTransaction was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _QueuedTransaction extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String TRANSACTION_KEY_PROPERTY = "transactionKey";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String QUEUED_RECORDS_PROPERTY = "queuedRecords";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> TRANSACTION_KEY = new Property<String>("transactionKey");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<QueuedRecord>> QUEUED_RECORDS = new Property<List<QueuedRecord>>("queuedRecords");

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setTransactionKey(String transactionKey) {
        writeProperty("transactionKey", transactionKey);
    }
    public String getTransactionKey() {
        return (String)readProperty("transactionKey");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToQueuedRecords(QueuedRecord obj) {
        addToManyTarget("queuedRecords", obj, true);
    }
    public void removeFromQueuedRecords(QueuedRecord obj) {
        removeToManyTarget("queuedRecords", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<QueuedRecord> getQueuedRecords() {
        return (List<QueuedRecord>)readProperty("queuedRecords");
    }


}
