package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.QueueKey;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.builders.replication.IWillowStubBuilder;
import ish.oncourse.webservices.services.replication.DFADedupper;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.services.replication.WillowStubBuilderFactory;
import ish.oncourse.webservices.services.replication.WillowUpdaterFactory;
import ish.oncourse.webservices.updaters.replication.IWillowUpdater;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReplicationPortType", serviceName = "ReplicationService", portName = "ReplicationPort", targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/")
public class ReplicationPortTypeImpl implements ReplicationPortType {

	private static final Logger logger = Logger.getLogger(ReplicationPortTypeImpl.class);

	@Inject
	@Autowired
	private IWillowQueueService queueService;

	@Inject
	@Autowired
	private WillowStubBuilderFactory stubBuilderFactory;

	@Inject
	@Autowired
	private WillowUpdaterFactory updaterFactory;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	@SuppressWarnings("unchecked")
	@Override
	@WebMethod(operationName = "sendRecords")
	public ReplicationResult sendRecords(ReplicationRecords req) {

		ReplicationResult result = new ReplicationResult();

		List<ReplicatedRecord> replicatedRecords = new ArrayList<ReplicatedRecord>();

		for (TransactionGroup group : req.getGroups()) {
			ObjectContext ctx = cayenneService.newNonReplicatingContext();

			IWillowUpdater updater = updaterFactory.newReplicationUpdater(ctx.createChildContext(), group);

			while (!group.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
				ReplicationStub stub = group.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
				updater.updateRecord(stub, replicatedRecords);
			}

			ctx.commitChanges();
		}

		result.getReplicatedRecord().addAll(replicatedRecords);

		return result;
	}

	@Override
	@WebMethod(operationName = "getRecords")
	public ReplicationRecords getRecords() {

		ReplicationRecords result = new ReplicationRecords();

		List<QueuedRecord> queue = queueService.getReplicationQueue();

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

		IWillowStubBuilder builder = stubBuilderFactory.newReplicationStubBuilder();

		Map<String, TransactionGroup> groupMap = new LinkedHashMap<String, TransactionGroup>();

		for (Map.Entry<QueueKey, DFADedupper> entry : dedupMap.entrySet()) {
			
			DFADedupper deduper = entry.getValue();
			
			ObjectContext ctx = cayenneService.newContext();
			List<QueuedRecord> duplicates = deduper.duplicates();
			ctx.deleteObjects(duplicates);
			ctx.commitChanges();
			
			QueuedRecord record = deduper.deDuppedRecord();

			if (record == null) {
				continue;
			}

			String transactionKey = deduper.getTransactionKeys().first();
			TransactionGroup group = groupMap.get(transactionKey);

			if (group == null) {
				group = new TransactionGroup();

				for (String key : deduper.getTransactionKeys()) {
					groupMap.put(key, group);
				}

				result.getGroups().add(group);
			}

			group.getAttendanceOrBinaryDataOrBinaryInfo().add(builder.convert(record));
		}

		return result;
	}

	@Override
	@WebMethod(operationName = "sendResults")
	@Oneway
	public void sendResults(ReplicationResult request) {
		try {
			ObjectContext ctx = cayenneService.newNonReplicatingContext();

			for (ReplicatedRecord record : request.getReplicatedRecord()) {

				SelectQuery q = new SelectQuery(QueuedRecord.class);

				q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_WILLOW_ID_PROPERTY, record.getStub().getWillowId()));
				q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, record.getStub()
						.getEntityIdentifier()));

				QueuedRecord queuedRecord = (QueuedRecord) Cayenne.objectForQuery(ctx, q);

				if (record.getStatus() == Status.SUCCESS && record.getStub().getAngelId() != null) {

					@SuppressWarnings("unchecked")
					Class<? extends Queueable> entityClass = (Class<? extends Queueable>) ctx.getEntityResolver()
							.getObjEntity(record.getStub().getEntityIdentifier()).getJavaClass();
					Queueable object = (Queueable) Cayenne.objectForPK(ctx, entityClass, record.getStub().getWillowId());
					object.setAngelId(record.getStub().getAngelId());
					ctx.deleteObject(queuedRecord);
				} else {
					Integer numberAttempts = (queuedRecord.getNumberOfAttempts() != null) ? queuedRecord.getNumberOfAttempts()
							: 0;
					queuedRecord.setNumberOfAttempts(numberAttempts + 1);
					queuedRecord.setLastAttemptTimestamp(new Date());
				}
			}

			ctx.commitChanges();
		} catch (Exception e) {
			logger.error("Unable to confirm replication results.", e);
		}
	}
}
