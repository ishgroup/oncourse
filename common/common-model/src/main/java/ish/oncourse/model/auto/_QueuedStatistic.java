package ish.oncourse.model.auto;

import ish.oncourse.model.College;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;

/**
 * Class _QueuedStatistic was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _QueuedStatistic extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ENTITY_IDENTIFIER_PROPERTY = "entityIdentifier";
    @Deprecated
    public static final String RECEIVED_TIMESTAMP_PROPERTY = "receivedTimestamp";
    @Deprecated
    public static final String STACKED_COUNT_PROPERTY = "stackedCount";
    @Deprecated
    public static final String STACKED_TRANSACTIONS_COUNT_PROPERTY = "stackedTransactionsCount";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ENTITY_IDENTIFIER = new Property<String>("entityIdentifier");
    public static final Property<Date> RECEIVED_TIMESTAMP = new Property<Date>("receivedTimestamp");
    public static final Property<Integer> STACKED_COUNT = new Property<Integer>("stackedCount");
    public static final Property<Integer> STACKED_TRANSACTIONS_COUNT = new Property<Integer>("stackedTransactionsCount");
    public static final Property<College> COLLEGE = new Property<College>("college");

    public void setEntityIdentifier(String entityIdentifier) {
        writeProperty("entityIdentifier", entityIdentifier);
    }
    public String getEntityIdentifier() {
        return (String)readProperty("entityIdentifier");
    }

    public void setReceivedTimestamp(Date receivedTimestamp) {
        writeProperty("receivedTimestamp", receivedTimestamp);
    }
    public Date getReceivedTimestamp() {
        return (Date)readProperty("receivedTimestamp");
    }

    public void setStackedCount(Integer stackedCount) {
        writeProperty("stackedCount", stackedCount);
    }
    public Integer getStackedCount() {
        return (Integer)readProperty("stackedCount");
    }

    public void setStackedTransactionsCount(Integer stackedTransactionsCount) {
        writeProperty("stackedTransactionsCount", stackedTransactionsCount);
    }
    public Integer getStackedTransactionsCount() {
        return (Integer)readProperty("stackedTransactionsCount");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


}
