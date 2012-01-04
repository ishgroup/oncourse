package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.QueueKey;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.StackTraceUtils;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.soap.v4.FaultCode;
import ish.oncourse.webservices.soap.v4.ReplicationFault;
import ish.oncourse.webservices.v4.stubs.replication.FaultReason;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Main version 4 synchronous replication implementation.
 * 
 * @author anton
 */
public class ReplicationServiceImpl implements IReplicationService {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ReplicationServiceImpl.class);

	/**
	 * Maximum transaction number allowed to be sent to angel.
	 */
	private static final int TRANSACTION_BATCH_SIZE = 50;

	@Inject
	private IWillowQueueService queueService;

	@Inject
	private IWillowStubBuilder stubBuilder;

	@Inject
	private ITransactionGroupProcessor transactionGroupAtomicUpdater;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ITransactionGroupValidator groupValidator;

	/**
	 * Process transaction groups from angel for replication.
	 */
	@Override
	public ReplicationResult sendRecords(ReplicationRecords req) throws ReplicationFault {
		try {
			List<ReplicatedRecord> replicatedRecords = new ArrayList<ReplicatedRecord>();

			for (TransactionGroup group : req.getGroups()) {
				replicatedRecords.addAll(transactionGroupAtomicUpdater.processGroup(group));
			}

			ReplicationResult result = new ReplicationResult();
			result.getReplicatedRecord().addAll(replicatedRecords);

			return result;

		} catch (Exception e) {
			logger.error("Unable to process replication records.", e);
			FaultReason faultReason = new FaultReason();
			faultReason.setDetailMessage(String.format("Unable to process replication records. Willow exception: %s", e.getMessage()));
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);
			throw new ReplicationFault("Unable to process replication records.", faultReason);
		}
	}

	/**
	 * Constructs transaction groups with willow records, for replicating back
	 * to Angel.
	 */
	@Override
	public ReplicationRecords getRecords() throws ReplicationFault {

		try {

			int number = 0;
			int from = 0;

			List<QueuedTransaction> transactions;
			List<QueuedRecord> queue = new LinkedList<QueuedRecord>();

			do {
				transactions = queueService.getReplicationQueue(from, TRANSACTION_BATCH_SIZE);

				for (QueuedTransaction t : transactions) {
					if (!t.shouldSkipTransaction()) {
						List<QueuedRecord> queuedRecords = t.getQueuedRecords();
						queue.addAll(queuedRecords);
						number++;
					}
				}

				from += TRANSACTION_BATCH_SIZE;
			} while (number < TRANSACTION_BATCH_SIZE && transactions.size() == TRANSACTION_BATCH_SIZE);

			// now we have records to process
			List<TransactionGroup> resultGroups = new ArrayList<TransactionGroup>();

			if (!queue.isEmpty()) {
				Map<QueueKey, DFADedupper> dedupMap = new LinkedHashMap<QueueKey, DFADedupper>();

				for (QueuedRecord r : queue) {
					QueueKey key = new QueueKey(r.getEntityWillowId(), r.getEntityIdentifier());

					DFADedupper deduper = dedupMap.get(key);

					if (deduper == null) {
						deduper = new DFADedupper();
						dedupMap.put(key, deduper);
					}

					deduper.nextState(r);
				}

				List<DFADedupper> sortedDeduppers = new ArrayList<DFADedupper>(dedupMap.entrySet().size());
				for (Map.Entry<QueueKey, DFADedupper> entry : dedupMap.entrySet()) {
					sortedDeduppers.add(entry.getValue());
				}
				Collections.sort(sortedDeduppers);
				
				Map<String, TransactionGroup> groupMap = new LinkedHashMap<String, TransactionGroup>();

				for (DFADedupper deduper : sortedDeduppers) {

					ObjectContext ctx = cayenneService.newContext();
					List<QueuedRecord> duplicates = deduper.duplicates();

					for (QueuedRecord dup : duplicates) {
						ctx.deleteObject(ctx.localObject(dup.getObjectId(), null));
					}

					ctx.commitChanges();

					QueuedRecord record = deduper.deDuppedRecord();

					if (record == null) {
						continue;
					}

					record = (QueuedRecord) ctx.localObject(record.getObjectId(), record);

					int numberAttempts = (record.getNumberOfAttempts() != null) ? record.getNumberOfAttempts() : 0;
					record.setNumberOfAttempts(numberAttempts + 1);

					ctx.commitChanges();

					TransactionGroup group = null;
					
					logger.debug(String.format("Deduper spans %s transactions.", deduper.getTransactionKeys().size()));

					for (String transactionKey : deduper.getTransactionKeys()) {
						group = groupMap.get(transactionKey);
						if (group != null) {
							break;
						}
					}

					if (group == null) {
						group = new TransactionGroup();

						for (String key : deduper.getTransactionKeys()) {
							groupMap.put(key, group);
						}

						resultGroups.add(group);
					}

					try {
						ReplicationStub stub = stubBuilder.convert(record);

						if (stub == null) {
							// cleanup QueuedRecord if linked object not found.
							ctx.deleteObject(record);
							ctx.commitChanges();
						} else {
							group.getAttendanceOrBinaryDataOrBinaryInfo().add(stub);
						}

					} catch (Exception se) {
						logger.error(String.format("Unable to build stub for queuedRecord with id:%s. Skipping record.", record.getId()),
								se);
					}
				}
			}

			ReplicationRecords result = new ReplicationRecords();
			result.getGroups().addAll(groupValidator.validateAndReturnFixedGroups(resultGroups));

			return result;

		} catch (Exception e) {
			logger.error("Unable to get records for replication.", e);
			FaultReason faultReason = new FaultReason();
			faultReason.setDetailMessage(String.format("Unable to get records for replication. Willow exception: %s", e.getMessage()));
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);
			throw new ReplicationFault("Unable to get records for replication.", faultReason);
		}
	}

	/**
	 * Confirms replication from Angel, if record was replicated successfully
	 * updates angelId, otherwise increases attempts count.
	 */
	@Override
	public int sendResults(ReplicationResult request) throws ReplicationFault {

		try {

			ObjectContext ctx = cayenneService.newNonReplicatingContext();

			int numberDeleted = 0;

			for (ReplicatedRecord record : request.getReplicatedRecord()) {

				SelectQuery q = new SelectQuery(QueuedRecord.class);

				q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_WILLOW_ID_PROPERTY, record.getStub().getWillowId()));
				q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, record.getStub().getEntityIdentifier()));
				q.andQualifier(ExpressionFactory.greaterExp(QueuedRecord.NUMBER_OF_ATTEMPTS_PROPERTY, 0));

				@SuppressWarnings("unchecked")
				List<QueuedRecord> list = ctx.performQuery(q);

				for (QueuedRecord queuedRecord : list) {
					try {

						if (record.getStatus() == Status.SUCCESS && record.getStub().getAngelId() != null) {

							try {
								@SuppressWarnings("unchecked")
								Class<? extends Queueable> entityClass = (Class<? extends Queueable>) ctx.getEntityResolver()
										.getObjEntity(record.getStub().getEntityIdentifier()).getJavaClass();

								if (queuedRecord.getAction() != QueuedRecordAction.DELETE) {
									Queueable object = (Queueable) Cayenne.objectForPK(ctx, entityClass, record.getStub().getWillowId());
									object.setAngelId(record.getStub().getAngelId());
								}

								ctx.commitChanges();

							} catch (CayenneRuntimeException ce) {
								logger.error(String.format("Duplicate angelId:%s for entity:%s with willowId:%s", record.getStub()
										.getAngelId(), record.getStub().getEntityIdentifier(), record.getStub().getWillowId()), ce);
								ctx.rollbackChanges();
							}

							ctx.deleteObject(queuedRecord);

						} else {
							queuedRecord.setErrorMessage(record.getMessage());

							if (QueuedRecord.MAX_NUMBER_OF_RETRY.equals(queuedRecord.getNumberOfAttempts())) {
								logger.error(String
										.format("Max number of retries has been reached for QueuedRecord entityIdentifier:%s angelId:%s willowId:%s",
												record.getStub().getEntityIdentifier(), record.getStub().getAngelId(), record.getStub()
														.getWillowId()));
							}
						}

						ctx.commitChanges();

						numberDeleted++;

					} catch (CayenneRuntimeException ce) {
						logger.error("Can not cleanup QueuedRecord.", ce);
						ctx.rollbackChanges();
						queuedRecord.setErrorMessage(StringUtils.abbreviate(
								"cayenne exception at services:" + StackTraceUtils.stackTraceAsString(ce), 1000));
						ctx.commitChanges();
					}
				}
			}

			queueService.cleanEmptyTransactions();

			return numberDeleted;

		} catch (Exception e) {
			logger.error("Unable to confirm replication results.", e);

			FaultReason faultReason = new FaultReason();
			faultReason.setDetailMessage(String.format("Unable to confirm replication results. Willow exception: %s", e.getMessage()));
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);

			throw new ReplicationFault("Unable to confirm replication results.", faultReason);
		}
	}
}
