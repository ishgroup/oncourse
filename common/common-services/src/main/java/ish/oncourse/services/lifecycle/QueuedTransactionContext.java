package ish.oncourse.services.lifecycle;

import ish.oncourse.model.QueuedTransaction;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

class QueuedTransactionContext {

	/**
	 * Current context
	 */
	private ObjectContext currentObjectContext;

	/**
	 * Transaction map.
	 */
	private Map<String, QueuedTransaction> transactionMap = new HashMap<String, QueuedTransaction>();

	/**
	 * Constructor
	 * 
	 * @param currentObjectContext
	 */
	public QueuedTransactionContext(ObjectContext currentObjectContext) {
		super();
		this.currentObjectContext = currentObjectContext;
	}

	/**
	 * Constructor
	 */
	private QueuedTransactionContext(ObjectContext currentObjectContext, Map<String, QueuedTransaction> transactionMap) {
		this(currentObjectContext);
		this.transactionMap = transactionMap;
	}

	public void setCurrentObjectContext(ObjectContext newContext) {
		this.currentObjectContext = newContext;
	}

	public ObjectContext getCurrentObjectContext() {
		return currentObjectContext;
	}

	/**
	 * Assigns transaction to key
	 * 
	 * @param key
	 * @param transaction
	 */
	public void assignTransactionToKey(String key, QueuedTransaction transaction) {
		transactionMap.put(key, transaction);
	}

	/**
	 * Gets transaction for key
	 * 
	 * @param key
	 * @return transaction
	 */
	public QueuedTransaction getTransactionForKey(String key) {
		return transactionMap.get(key);
	}

	/**
	 * Creates shallow copy
	 * 
	 * @return shallow copy
	 */
	public QueuedTransactionContext shallowCopy() {
		return new QueuedTransactionContext(currentObjectContext, transactionMap);
	}
}
