package ish.oncourse.server.lifecycle

import ish.oncourse.server.cayenne.QueuedTransaction
import org.apache.cayenne.ObjectContext

class StackFrame {
    
    /**
     * Object context.
     */
    private ObjectContext objectContext

    /**
     * Transaction mapping.
     */
    private Map<String, QueuedTransaction> transactionMapping

    /**
     * @param objectContext
     * @param transactionMapping
     */
    StackFrame(ObjectContext objectContext, Map<String, QueuedTransaction> transactionMapping) {
        super()
        this.objectContext = objectContext
        this.transactionMapping = transactionMapping
    }

    /**
     * @return the objectContext
     */
    ObjectContext getObjectContext() {
        return this.objectContext
    }

    /**
     * @param objectContext the objectContext to set
     */
    void setObjectContext(ObjectContext objectContext) {
        this.objectContext = objectContext
    }

    /**
     * @return the transactionMapping
     */
    Map<String, QueuedTransaction> getTransactionMapping() {
        return this.transactionMapping
    }

    /**
     * @param transactionMapping the transactionMapping to set
     */
    void setTransactionMapping(Map<String, QueuedTransaction> transactionMapping) {
        this.transactionMapping = transactionMapping
    }
}
