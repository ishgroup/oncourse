package ish.oncourse.webservices.replication.services;

import ish.common.types.EntityMapping;
import ish.oncourse.function.GetEntityTransactionByInstruction;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.StackTraceUtils;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.GENERIC_EXCEPTION;

/**
 * Main version 4 synchronous replication implementation.
 * 
 * @author anton
 */
public class ReplicationServiceImpl implements IReplicationService {

	/**
	 * Logger
	 */
	private static final Logger logger = LogManager.getLogger();

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
	private IWebSiteService webSiteService;

	@Inject
	private ITransactionStubBuilder transactionBuilder;


	@Override
	public GenericTransactionGroup getRecordByInstruction(String instruction, SupportedVersions version) throws InternalReplicationFault {

		return GetEntityTransactionByInstruction
				.valueOf(cayenneService.newContext(), stubBuilder,
						transactionBuilder, webSiteService.getCurrentCollege(),
						instruction, version)
				.get();
	}

	/**
	 * Process transaction groups from angel for replication.
	 */
	@Override
	public GenericReplicationResult sendRecords(GenericReplicationRecords req) throws InternalReplicationFault {
		try {
			List<GenericReplicatedRecord> replicatedRecords = new ArrayList<>();
			for (GenericTransactionGroup group : req.getGroups()) {
				replicatedRecords.addAll(transactionGroupAtomicUpdater.processGroup(group));
			}
			GenericReplicationResult result = PortHelper.createReplicationResult(req);
			result.getGenericReplicatedRecord().addAll(replicatedRecords);
			return result;

		} catch (Exception e) {
			logger.error("Unable to process replication records.", e);
			throw new InternalReplicationFault("Unable to process replication records.", GENERIC_EXCEPTION,
				String.format("Unable to process replication records. Willow exception: %s", e.getMessage()));
		}
	}

	/**
	 * Constructs transaction groups with willow records, for replicating back
	 * to Angel.
	 */
	@Override
	public GenericReplicationRecords getRecords(final SupportedVersions version) throws InternalReplicationFault {

		try {

			int number = 0;
			int from = 0;

			List<QueuedTransaction> transactions;
			List<QueuedRecord> queue = new LinkedList<>();

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
			List<GenericTransactionGroup> resultGroups = new ArrayList<>();

			if (!queue.isEmpty()) {
				Map<QueueKey, DFADedupper> dedupMap = new LinkedHashMap<>();

				for (QueuedRecord r : queue) {
					QueueKey key = new QueueKey(r.getEntityWillowId(), r.getEntityIdentifier());

					DFADedupper deduper = dedupMap.get(key);

					if (deduper == null) {
						deduper = new DFADedupper();
						dedupMap.put(key, deduper);
					}

					deduper.nextState(r);
				}

				List<DFADedupper> sortedDeduppers = new ArrayList<>(dedupMap.entrySet().size());
				for (Map.Entry<QueueKey, DFADedupper> entry : dedupMap.entrySet()) {
					sortedDeduppers.add(entry.getValue());
				}
				Collections.sort(sortedDeduppers);

				Map<String, GenericTransactionGroup> groupMap = new LinkedHashMap<>();

				for (DFADedupper deduper : sortedDeduppers) {

					ObjectContext ctx = cayenneService.newContext();
					List<QueuedRecord> duplicates = deduper.duplicates();

					for (QueuedRecord dup : duplicates) {
						ctx.deleteObjects(ctx.localObject(dup));
					}

					ctx.commitChanges();

					QueuedRecord record = deduper.deDuppedRecord();

					if (record == null) {
						continue;
					}

					record = ctx.localObject(record);

					int numberAttempts = (record.getNumberOfAttempts() != null) ? record.getNumberOfAttempts() : 0;
					record.setNumberOfAttempts(numberAttempts + 1);

					ctx.commitChanges();

					GenericTransactionGroup group = null;

					logger.debug("Deduper spans {} transactions.", deduper.getTransactionKeys().size());

					for (String transactionKey : deduper.getTransactionKeys()) {
						group = groupMap.get(transactionKey);
						if (group != null) {
							break;
						}
					}

					if (group == null) {
						group = PortHelper.createTransactionGroup(version);

						for (String key : deduper.getTransactionKeys()) {
							groupMap.put(key, group);
						}

						resultGroups.add(group);
					}

					try {
						GenericReplicationStub stub = stubBuilder.convert(record, PortHelper.getVersionByTransactionGroup(group));

						if (stub == null) {
							// cleanup QueuedRecord if linked object not found.
							ctx.deleteObjects(record);
							ctx.commitChanges();
						} else {
							group.getGenericAttendanceOrBinaryInfo().add(stub);
						}

					} catch (Exception se) {
						logger.error("Unable to build stub for queuedRecord with id: {}. Skipping record.", record.getId(), se);
					}
				}
			}

			GenericReplicationRecords result = PortHelper.createReplicationRecords(version);
			result.getGenericGroups().addAll(resultGroups);

			// clean remove group if it's empty
			for (GenericTransactionGroup g : new ArrayList<>(result.getGenericGroups())) {
				if (g.getGenericAttendanceOrBinaryInfo().isEmpty()) {
					result.getGroups().remove(g);
				}
			}

			return result;

		} catch (Exception e) {
			logger.error("Unable to get records for replication.", e);
			throw new InternalReplicationFault("Unable to get records for replication.", GENERIC_EXCEPTION,
				String.format("Unable to get records for replication. Willow exception: %s", e.getMessage()));
		}
	}

	/**
	 * Confirms replication from Angel, if record was replicated successfully
	 * updates angelId, otherwise increases attempts count.
	 */
	@Override
	public int sendResults(GenericReplicationResult request) throws InternalReplicationFault {

		try {

			ObjectContext ctx = cayenneService.newNonReplicatingContext();

			int numberDeleted = 0;

			for (GenericReplicatedRecord record : request.getReplicatedRecord()) {
				String entityIdentifier = record.getStub().getEntityIdentifier();
				if (EntityMapping.BINARY_RELATION_MAPPING.values().contains(entityIdentifier)) {
					entityIdentifier = BinaryInfoRelation.class.getSimpleName();
				} else if (CustomField.class.getSimpleName().equals(entityIdentifier)) {
					entityIdentifier = ContactCustomField.class.getSimpleName();
				}
				
				List<QueuedRecord> list = ObjectSelect.query(QueuedRecord.class)
						.where(QueuedRecord.ENTITY_WILLOW_ID.eq(record.getStub().getWillowId()))
						.and(QueuedRecord.ENTITY_IDENTIFIER.eq(entityIdentifier))
						.and(QueuedRecord.NUMBER_OF_ATTEMPTS.gt(0))
						.select(ctx);

				for (QueuedRecord queuedRecord : list) {
					try {

						if (StubUtils.hasSuccessStatus(record)) {
							if (queuedRecord.getAction() != QueuedRecordAction.DELETE) {
								Long collegeid = null;
								try {
									@SuppressWarnings("unchecked")
									Class<? extends Queueable> entityClass = (Class<? extends Queueable>) ctx.getEntityResolver()
											.getObjEntity(entityIdentifier).getJavaClass();
									Queueable object = (Queueable) Cayenne.objectForPK(ctx, entityClass, record.getStub().getWillowId());
									if (object == null) {
										//This can be the result of willow entity deletion when receive the response from angel. 
										//In this case queued record will be removed on next replication iteration
										String message = String.format("Unable to load %s entity with %s willowid to setup %s angelid.", 
												entityIdentifier, record.getStub().getWillowId(), record.getStub().getAngelId());
										logger.error(message);
										queuedRecord.setErrorMessage(message);
										//also setup received angleid to queued record to be able fix the entity in the issue case.
										queuedRecord.setAngelId(record.getStub().getAngelId());
									} else {
										//we should delete only the queued records for elements which we can update
										ctx.deleteObjects(queuedRecord);
										collegeid = object.getCollege().getId();
										object.setAngelId(record.getStub().getAngelId());
									}
									ctx.commitChanges();
								} catch (CayenneRuntimeException ce) {
									ctx.rollbackChanges();
									String message = String.format("Failed to update entity:%s with angelId:%s and willowId:%s for college:%s after replication to angel.", 
											entityIdentifier,record.getStub().getAngelId(), record.getStub().getWillowId(), collegeid); 
									logger.error(message, ce);
									queuedRecord.setErrorMessage(message);									
								}
							} else {
								ctx.deleteObjects(queuedRecord);
							}
							
						} else {
							queuedRecord.setErrorMessage(record.getMessage());
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
			throw new InternalReplicationFault("Unable to confirm replication results.", GENERIC_EXCEPTION,
				String.format("Unable to confirm replication results. Willow exception: %s", e.getMessage()));
		}
	}
}
