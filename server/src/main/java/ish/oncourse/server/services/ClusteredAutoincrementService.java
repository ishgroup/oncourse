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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import org.apache.cayenne.query.SQLSelect;

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

    private long nextId(String name) {
        return SQLSelect.scalarQuery("{call sequence_value('$tableName', $batchSize)}", Long.class)
                .paramsArray(name, DEFAULT_BATCH_SIZE)
                .selectOne(cayenneService.getNewNonReplicatingContext());
    }
}
