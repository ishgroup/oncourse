/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This subclass enables a context to maintain preferences (at runtime) which enables arbitrary logic in the application to be enabled or disabled.
 */
public class ISHDataContext extends DataContext {


	private static final Logger logger = LogManager.getLogger();

	private static final String REPLICATING_PROP = "replicating";
	private static final String TRANSACTION_KEY_PROP = "transaction_key";
	private static final String READ_ONLY_PROP = "read_only";

	// The stack was necessary when we used Cayenne parent-child contexts. But we don't use them any more.
	// The transaction keys should be moved to our replication plugin
	@Deprecated
	private Deque<String> transactionKeyStack = new ArrayDeque<>();

	/**
	 *
	 */
	public ISHDataContext() {
		super();
	}

	/**
	 * @param channel
	 * @param objectStore
	 */
	public ISHDataContext(DataChannel channel, ObjectStore objectStore) {
		super(channel, objectStore);
	}

	/**
	 * Generates new transaction key.
	 *
	 * @return transaction key
	 */
	public String generateTransactionKey() {
		var random = new SecureRandom(String.valueOf(this.hashCode()).getBytes());
		var bytes = new byte[20];
		random.nextBytes(bytes);
		return DigestUtils.md5Hex(bytes);
	}

	@Override
	public String toString() {
		return "{ISHDataContext#" + hashCode() + "|" + getUserProperties() + "}";
	}

	/**
	 * @return true if record queueing is disabled.
	 */
	public boolean getIsRecordQueueingEnabled() {
		return (Boolean) getUserProperty(REPLICATING_PROP);
	}

	/**
	 * Determines whether to queue records for replication.
	 *
	 * @param isRecordQueueingDisabled the isRecordQueueingDisabled to set
	 */
	public void setRecordQueueingEnabled(boolean isRecordQueueingDisabled) {
		setUserProperty(REPLICATING_PROP, isRecordQueueingDisabled);
	}

	/**
	 * @return true if context is read only.
	 */
	public boolean isReadOnly() {
		var readOnly = (Boolean) getUserProperty(READ_ONLY_PROP);
		return readOnly != null ? readOnly : false;
	}

	/**
	 * Determines whether context is read only, i.e. commits are not permitted.
	 */
	public void setReadOnly(boolean readOnly) {
		setUserProperty(READ_ONLY_PROP, readOnly);
	}

	/**
	 * @see org.apache.cayenne.BaseContext#onSync(ObjectContext, GraphDiff, int)
	 */
	@Override
	public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType) {
		var transactionKey = transactionKeyStack.isEmpty() ? generateTransactionKey() : transactionKeyStack.peek();

		transactionKeyStack.push(transactionKey);
		var diff = super.onSync(originatingContext, changes, syncType);
		transactionKeyStack.pop();

		return diff;
	}

	/**
	 * Method, which returns the commit key of last commit on this context within current thread.
	 *
	 * @return transaction key
	 */
	public String getTransactionKey() {
		return transactionKeyStack.peek();
	}

	/**
	 * @see DataContext#commitChanges()
	 */
	@Override
	public void commitChanges() throws CayenneRuntimeException {
		if (isReadOnly()) {
			throw new UnsupportedOperationException("Commits are not allowed for read only contexts.");
		}

		var transactionKey = transactionKeyStack.isEmpty() ? generateTransactionKey() : transactionKeyStack.peek();

		transactionKeyStack.push(transactionKey);
		super.commitChanges();
		transactionKeyStack.pop();
	}
}
