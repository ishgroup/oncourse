/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.DataChannelSyncFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.tx.TransactionManager;

public class SingleTransactionContextFilter implements DataChannelSyncFilter {
    @Override
    public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
        var handler = new InvoiceLineOrPaymentChangesDetector(originatingContext);
        changes.apply(handler);

        if (handler.isInvoiceLineOrPaymentInterfaceDetected()) {
            Injector injector = ServerRuntime.builder().build().getInjector();
            TransactionManager transactionManager = injector.getInstance(TransactionManager.class);
            return transactionManager.performInTransaction(() -> filterChain.onSync(originatingContext, changes, syncType));
        }
        return filterChain.onSync(originatingContext, changes, syncType);
    }
}
