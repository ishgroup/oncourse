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
package ish.oncourse.server.services;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.tx.TransactionDescriptor;
import org.apache.cayenne.tx.TransactionManager;
import org.apache.cayenne.tx.TransactionPropagation;

import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly;

/**
 * {@link IAutoIncrementService} implementation that uses stored procedure
 * to atomically get next batch of Ids for students and invoices.
 */
public class ClusteredAutoincrementService implements IAutoIncrementService {

    private static final int DEFAULT_BATCH_SIZE = 20;

    private final ICayenneService  cayenneService;
    private final Queue<Long> studentIdPool = new ConcurrentLinkedQueue<>();
    private final Queue<Long> invoiceIdPool = new ConcurrentLinkedQueue<>();

    @Inject
    public ClusteredAutoincrementService(ICayenneService  cayenneService) {
        this.cayenneService = cayenneService;
    }

    @Override
    public Long getNextStudentNumber() {
        return getNextIdFromPool(studentIdPool, "student");
    }

    @Override
    public Long getNextInvoiceNumber() {
        return getNextIdFromPool(invoiceIdPool, "invoice");
    }

    private Long getNextIdFromPool(Queue<Long> pool, String name) {
        if (validateOnly.get() != null && validateOnly.get()) {
            return 100L;
        }
        Long id;
        while((id = pool.poll()) == null)  {
            fillInPool(pool, name);
        }
        return id;
    }

    private void fillInPool(Queue<Long> pool, String name) {
        long nextId = nextId(name);
        for(int i=0; i<DEFAULT_BATCH_SIZE; i++) {
            pool.offer(nextId + i);
        }
    }

    long nextId(String name) {
        DataContext context = cayenneService.getNewNonReplicatingContext();
        TransactionManager transactionManager = cayenneService.getServerRuntime().getInjector()
                .getInstance(TransactionManager.class);

        // Always use clean transaction with exact isolation level to ensure sequence integrity
        TransactionDescriptor descriptor = new TransactionDescriptor(
                Connection.TRANSACTION_REPEATABLE_READ,
                TransactionPropagation.REQUIRES_NEW
        );

        return transactionManager.performInTransaction(() -> {
            // Select with row lock + update
            long currentValue = SQLSelect
                    .scalarQuery("SELECT nextId FROM SequenceSupport " +
                            "WHERE tableName = '$tableName' FOR UPDATE", Long.class)
                    .paramsArray(name)
                    .selectOne(context);
            SQLExec.query("UPDATE SequenceSupport SET nextId = nextId + $batchsize WHERE tableName = '$tname'")
                    .paramsArray(DEFAULT_BATCH_SIZE, name)
                    .execute(context);
            return currentValue;
        }, descriptor);
    }
}
