package ish.oncourse.services.lifecycle;

import ish.oncourse.model.QueuedTransaction;
import org.apache.cayenne.ObjectContext;

import java.util.Map;

/**
 * Used primary in case of nested commitChanges() calls on ObjectContext. In case of nested calls of commitChanges() 
 * we create only one context for QueuedRecords/QueuedTransactions which is committed in the end.
 * @author anton
 *
 */
public class StackFrame {
	
	/**
	 * Object context.
	 */
	private ObjectContext objectContext;

	/**
	 * Transaction mapping.
	 */
	private Map<String, QueuedTransaction> transactionMapping;

	/**
	 * @param objectContext
	 * @param transactionMapping
	 */
	public StackFrame(ObjectContext objectContext, Map<String, QueuedTransaction> transactionMapping) {
		super();
		this.objectContext = objectContext;
		this.transactionMapping = transactionMapping;
	}

	/**
	 * @return the objectContext
	 */
	public ObjectContext getObjectContext() {
		return this.objectContext;
	}

	/**
	 * @param objectContext the objectContext to set
	 */
	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	/**
	 * @return the transactionMapping
	 */
	public Map<String, QueuedTransaction> getTransactionMapping() {
		return this.transactionMapping;
	}

	/**
	 * @param transactionMapping the transactionMapping to set
	 */
	public void setTransactionMapping(Map<String, QueuedTransaction> transactionMapping) {
		this.transactionMapping = transactionMapping;
	}
}
