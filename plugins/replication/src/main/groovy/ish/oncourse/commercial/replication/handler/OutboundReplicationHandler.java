/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.handler;

import com.google.inject.Inject;
import ish.common.types.ContactDuplicateStatus;
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder;
import ish.oncourse.commercial.replication.cayenne.QueueKey;
import ish.oncourse.commercial.replication.cayenne.QueuedRecord;
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction;
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction;
import ish.oncourse.commercial.replication.modules.ISoapPortLocator;
import ish.oncourse.commercial.replication.services.DedupperFactory;
import ish.oncourse.commercial.replication.services.IAngelQueueService;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.ISHDataContext;
import ish.oncourse.server.cayenne.ContactDuplicate;
import ish.oncourse.server.cayenne.Queueable;
import ish.oncourse.webservices.soap.v23.ReplicationFault;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v23.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v23.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v23.stubs.replication.TransactionGroup;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectById;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;


import java.util.*;
import java.util.stream.Collectors;


public class OutboundReplicationHandler implements ReplicationHandler {
    private static final int TRANSACTION_BATCH_SIZE = 20;

    private final IAngelQueueService queueService;

    private final ICayenneService cayenneService;

    private final ISoapPortLocator soapPortLocator;

    private final IAngelStubBuilder stubBuilder;

    private static final String MERGE_KEY = "MERGE";

    private static final String CONTACTS_MERGE_MARKER = ContactDuplicate.class.getSimpleName();

    /**
     * @param queueService angel queue
     * @param cayenneService orm service
     * @param soapPortLocator guice provider for soap client
     * @param stubBuilder angel stub builder
     */
    @Inject
    public OutboundReplicationHandler(IAngelQueueService queueService, ICayenneService cayenneService, ISoapPortLocator soapPortLocator,
                                      IAngelStubBuilder stubBuilder) {
        super();
        this.queueService = queueService;
        this.cayenneService = cayenneService;
        this.soapPortLocator = soapPortLocator;
        this.stubBuilder = stubBuilder;
    }

    /**
     * @see ReplicationHandler#replicate()
     */
    @Override
    public void replicate() {
        var queueSize = this.queueService.getNumberOfTransactions();
        logger.info("Outbound replication started. The number of transactions:{}", queueSize);
        if (queueSize == 0) {
            return;
        }
        var number = 0;
        List<QueuedTransaction> transactions;
        do {
            if (this.queueService.isReplicationBlocked()) {
                break;
            }
            transactions = this.queueService.getReplicationQueue(number, TRANSACTION_BATCH_SIZE);
            List<QueuedRecord> currentBatch = new LinkedList<>();
            for (var t : transactions) {
                var queuedRecords = getQueuedRecordBy(t);
                if (!shouldSkipTransaction(queuedRecords)) {
                    currentBatch.addAll(queuedRecords);
                }
            }
            processCurrentBatch(currentBatch);
            number += TRANSACTION_BATCH_SIZE;
        } while (number < queueSize);
        this.queueService.cleanEmptyTransactions();
        logger.info("Outbound replication finished.");
    }

    private List<QueuedRecord> getQueuedRecordBy(QueuedTransaction transaction) {
        var qualifier = ExpressionFactory.matchExp(QueuedRecord.QUEUED_TRANSACTION.getName(), transaction);
        var q = SelectQuery.query(QueuedRecord.class, qualifier);
        q.addOrdering(new Ordering("db:" + QueuedRecord.ID_PK_COLUMN, SortOrder.ASCENDING));
        return this.cayenneService.getSharedContext().select(q);
    }

    /**
     * Check if any record within transaction has reached max retry level.
     *
     * @return true/false
     */
    private boolean shouldSkipTransaction(List<QueuedRecord> queuedRecords) {
        for (var r : queuedRecords) {
            if (r.getNumberOfAttempts() >= QueuedRecord.MAX_NUMBER_OF_RETRY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dedups queued records, fills replication request with soap stubs.
     *
     * @param currentBatch current batch of QueuedRecords
     */
    private void dedupAndFillReplicationRequest(GenericReplicationRecords replicationRequest, List<QueuedRecord> currentBatch,
                                                Map<QueueKey, QueuedRecord> currentBatchMap) {
        try {
            ObjectContext ctx = this.cayenneService.getNewNonReplicatingContext();

            List<GenericTransactionGroup> mergeGroups  = new LinkedList<>();

            var contactDuplicate = currentBatch.stream().filter(qr -> CONTACTS_MERGE_MARKER.equals(qr.getTableName()) && QueuedRecordAction.CREATE.equals(qr.getAction())).collect(Collectors.toList());
            contactDuplicate.stream().forEach(cd -> {
                var mergeTransaction = cd.getQueuedTransaction();
                var mergeBatch = mergeTransaction.getQueuedRecords();
                currentBatch.removeIf(qr -> qr.getQueuedTransaction().equals(mergeTransaction));

                GenericTransactionGroup group = new TransactionGroup();
                group.getTransactionKeys().add(mergeTransaction.getTransactionKey());
                group.getTransactionKeys().add(MERGE_KEY);
                mergeGroups.add(group);

                mergeBatch.stream().forEach( qr -> {
                    qr = ctx.localObject(qr);
                    qr.setNumberOfAttempts(qr.getNumberOfAttempts() + 1);

                    group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(qr));
                });
                ctx.commitChanges();
                currentBatchMap.put(new QueueKey(cd.getForeignRecordId(), cd.getTableName()), cd);
            });

            Map<String, GenericTransactionGroup> groupMap = new LinkedHashMap<>();
            var dedupperFactory = DedupperFactory.valueOf(currentBatch, ctx);
            for (var deduper : dedupperFactory.assembleDeduppers()) {
                var duplicates = deduper.duplicates();
                for (var dup : duplicates) {
                    ctx.deleteObjects(ctx.localObject(dup));
                }
                ctx.commitChanges();
                var record = deduper.deDuppedRecord();
                if (record == null) {
                    continue;
                }
                record = ctx.localObject(record);
                var numberAttempts = record.getNumberOfAttempts() != null ? record.getNumberOfAttempts() : 0;
                record.setNumberOfAttempts(numberAttempts + 1);
                ctx.commitChanges();
                GenericTransactionGroup group = null;
                for (var transactionKey : deduper.getTransactionKeys()) {
                    group = groupMap.get(transactionKey);
                    if (group != null) {
                        break;
                    }
                }
                if (group == null) {
                    group = new TransactionGroup();
                    for (var key : deduper.getTransactionKeys()) {
                        group.getTransactionKeys().add(key);
                        groupMap.put(key, group);
                    }
                    replicationRequest.getGenericGroups().add(group);
                }
                var stub = this.stubBuilder.convert(record);
                if (stub != null) {
                    group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stub);
                    currentBatchMap.put(new QueueKey(record.getForeignRecordId(), record.getTableName()), record);
                } else {
                    // cleanup QueuedRecord if linked object is null.
                    ctx.deleteObjects(record);
                    ctx.commitChanges();
                }
            }
            // clean remove group if it's empty
            for (var g : new ArrayList<>(replicationRequest.getGenericGroups())) {
                if (g.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
                    replicationRequest.getGroups().remove(g);
                }
            }
            replicationRequest.getGenericGroups().addAll(mergeGroups);
        } catch (Exception e) {
            logger.error("Queued record deduplication has failed. Skipping the current batch.", e);
            // Clearing groups from replication request to skip current batch.
            replicationRequest.getGroups().clear();
        }
    }

    /**
     * Process current queue batch, records are sent to willow, their willowId updated.
     *
     * @param currentBatch current batch
     */
    private void processCurrentBatch(List<QueuedRecord> currentBatch) {
        logger.info("Number QueuedRecord: {}", currentBatch.size());
        if (!currentBatch.isEmpty()) {
            var replicationRequest = new ReplicationRecords();
            Map<QueueKey, QueuedRecord> currentBatchMap = new LinkedHashMap<>();
            dedupAndFillReplicationRequest(replicationRequest, currentBatch, currentBatchMap);
            var numberOfGroups = replicationRequest.getGroups().size();
            logger.info("Number of transaction groups: {}", numberOfGroups);
            if (numberOfGroups != 0) {
                try {
                    var port = this.soapPortLocator.replicationPort();
                    logger.debug("Sending transaction groups to willow....");
                    GenericReplicationResult response = port.sendRecords(replicationRequest);
                    ObjectContext ctx = this.cayenneService.getNewNonReplicatingContext();
                    logger.debug("The number of replicated from willow:{}", response.getReplicatedRecord().size());
                    var replicationEnabled = ((ISHDataContext) ctx).getIsRecordQueueingEnabled();
                    assert !replicationEnabled;
                    for (var record : response.getGenericReplicatedRecord()) {
                        try {
                            logger.debug("Processing record with entityIdentifier:{} angelId:{} willowId:{} and status:{}.", record.getStub()
                                    .getEntityIdentifier(), record.getStub().getAngelId(), record.getStub().getWillowId(),
                                    ((ReplicatedRecord) record).getStatus());
                            var key = new QueueKey(record.getStub().getAngelId(), record.getStub().getEntityIdentifier());
                            if (currentBatchMap.get(key) != null) {
                                var qr = ctx.localObject(currentBatchMap.remove(key));
                                if (StubUtils.hasSuccessStatus(record) && record.getStub().getAngelId() != null) {
                                    var entityClass = ctx.getEntityResolver().getObjEntity(record.getStub().getEntityIdentifier()).getJavaClass();
                                    if (qr.getAction() != QueuedRecordAction.DELETE) {
                                        var object = (Queueable) SelectById.query(entityClass, record.getStub().getAngelId()).selectOne(ctx);
                                        if (object != null) {
                                            object.setWillowId(record.getStub().getWillowId());
                                            if (object instanceof ContactDuplicate) {
                                                var contactDuplicate = (ContactDuplicate)object;
                                                contactDuplicate.setStatus(ContactDuplicateStatus.PROCESSED);
                                                ctx.deleteObjects(qr.getQueuedTransaction().getQueuedRecords());
                                            }
                                        } else {
                                            logger.warn("Unable to set willowId, object was deleted entityIdentifier:{} angelId:{} willowId:{}",
                                                    record.getStub().getEntityIdentifier(), record.getStub().getAngelId(), record.getStub().getWillowId());
                                        }
                                    }
                                    ctx.deleteObjects(qr);
                                } else {
                                    qr.setLastAttemptOn(new Date());
                                    String errorMessage;
                                    if (record.getMessage().trim().length() > 1024) {
                                        errorMessage = record.getMessage().trim().substring(0, 1024);
                                    } else {
                                        errorMessage = record.getMessage().trim();
                                    }
                                    qr.setErrorMessage(errorMessage);
                                    if  (CONTACTS_MERGE_MARKER.equals(qr.getTableName())) {
                                        qr.getQueuedTransaction().getQueuedRecords().forEach( r -> r.setErrorMessage(errorMessage));
                                    }

                                    if (QueuedRecord.MAX_NUMBER_OF_RETRY.equals(qr.getNumberOfAttempts())) {
                                        logger.error("Max number of retries has been reached for QueuedRecord entityIdentifier:{} angelId:{} willowId:{}",
                                                record.getStub().getEntityIdentifier(), record.getStub().getAngelId(), record.getStub().getWillowId());
                                    }
                                }
                            }
                            ctx.commitChanges();
                        } catch (CayenneRuntimeException ce) {
                            logger.error("Can not cleanup QueuedRecord.", ce);
                            ctx.rollbackChanges();
                        }
                    }
                } catch (ReplicationFault e) {
                    logger.error("Outbound replication has failed. Message from server:{}", e.getFaultInfo().getDetailMessage(), e);
                } catch (Exception e) {
                    // most likely timeout, network failure or anything unexpected.
                    logger.error("Outbound replication failed with generic exception.", e);
                }
            }
        }
    }
}