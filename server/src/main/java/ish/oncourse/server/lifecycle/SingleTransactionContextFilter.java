/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import org.apache.cayenne.*;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.tx.TransactionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.ReentrantLock;

public class SingleTransactionContextFilter implements DataChannelSyncFilter {

    private static final Logger logger = LogManager.getLogger();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
        var handler = new InvoiceLineOrPaymentChangesDetector(originatingContext);
        changes.apply(handler);

        if (syncType != DataChannel.ROLLBACK_CASCADE_SYNC && handler.isInvoiceLineOrPaymentInterfaceDetected()) {
            Injector injector = ServerRuntime.builder().build().getInjector();
            TransactionManager transactionManager = injector.getInstance(TransactionManager.class);
            lock.lock();
            try {
                return transactionManager.performInTransaction(() -> filterChain.onSync(originatingContext, changes, syncType));
            } catch (CayenneRuntimeException e) {
                if(e.getCause().getClass().equals(IllegalStateException.class)
                        && e.getMessage().contains("Current status: STATUS_MARKED_ROLLEDBACK")) {
                    logger.warn("try to rollback changes into transaction detected!");
                    originatingContext.rollbackChanges();
                } else {
                    throw e;
                }
            } finally {
                lock.unlock();
            }

        }
        return filterChain.onSync(originatingContext, changes, syncType);
    }
}
