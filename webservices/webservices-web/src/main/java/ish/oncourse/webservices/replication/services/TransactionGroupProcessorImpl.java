package ish.oncourse.webservices.replication.services;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.services.ICollegeRequestService;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

	private static final Logger logger = Logger.getLogger(TransactionGroupProcessorImpl.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeRequestService collegeRequestService;

	@Inject
	private IWillowUpdater willowUpdater;

	@Override
	public List<ReplicatedRecord> processGroup(TransactionGroup group) {
		ObjectContext ctx = cayenneService.newNonReplicatingContext();

		List<ReplicatedRecord> result = new ArrayList<ReplicatedRecord>();

		while (!group.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
			ReplicationStub stub = group.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
			updateRecord(ctx, stub, group, result);
		}

		return result;
	}

	private Queueable updateRecord(ObjectContext objectContext, ReplicationStub stub, TransactionGroup group, List<ReplicatedRecord> result) {
		ReplicatedRecord replRecord = new ReplicatedRecord();
		replRecord.setStatus(Status.SUCCESS);
		replRecord.setStub(toHollow(stub));
		result.add(replRecord);

		Queueable objectToUpdate = null;

		if (stub.getWillowId() != null) {
			objectToUpdate = findEntityByWillowId(objectContext, stub.getWillowId(), stub.getEntityIdentifier());
			if (objectToUpdate == null) {
				String message = String.format("Cannot find object:%s by willowId:%s", stub.getEntityIdentifier(), stub.getWillowId());

				logger.error(message);

				replRecord.setStatus(Status.WILLOWID_NOT_FOUND);
				replRecord.setMessage(message);
			} else {
				if (stub.getAngelId() == null || !stub.getAngelId().equals(objectToUpdate.getAngelId())) {

					String message = String.format("AngelId doesn't match. Got %s while expected %s.", stub.getAngelId(),
							objectToUpdate.getAngelId());

					logger.error(message);

					objectToUpdate = null;
					replRecord.setStatus(Status.ANGELID_NOT_MATCH);
					replRecord.setMessage(message);
				}
			}
		} else {
			if (stub.getAngelId() != null) {
				objectToUpdate = findEntityByAngelId(objectContext, stub.getAngelId(), stub.getEntityIdentifier());

				if (objectToUpdate != null) {

					String message = String.format("Have willowId:null but found existing record for entity:%s and angelId:%s",
							stub.getEntityIdentifier(), stub.getAngelId());

					logger.error(message);

					objectToUpdate = null;
					replRecord.setStatus(Status.DANGLING_OBJECT);
					replRecord.setMessage(message);
				} else {
					objectToUpdate = objectContext.newObject(getEntityClass(objectContext, stub.getEntityIdentifier()));
				}
			} else {
				String message = String.format("Both angelId and willowId are empty for object %s.", stub.getEntityIdentifier());

				logger.error(message);

				replRecord.setStatus(Status.EMPTY_IDS);
				replRecord.setMessage(message);
			}
		}

		if (objectToUpdate != null) {
			try {
				if (stub instanceof DeletedStub) {
					objectContext.deleteObject(objectToUpdate);
				} else {
					College college = (College) objectContext.localObject(collegeRequestService.getRequestingCollege().getObjectId(), null);
					objectToUpdate.setCollege(college);
					willowUpdater.updateEntityFromStub(stub, objectToUpdate, new RelationShipCallbackImpl(objectContext, group, result));
				}

				objectContext.commitChangesToParent();
				replRecord.getStub().setWillowId(objectToUpdate.getId());
			} catch (Exception e) {
				logger.error("Failed to commit object.", e);
				objectContext.rollbackChanges();
				replRecord.setStatus(Status.FAILED);
				replRecord.setMessage(e.getMessage());
			}
		}

		return objectToUpdate;
	}

	private Class<? extends Queueable> getEntityClass(ObjectContext objectContext, String entityIdentifier) {
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
				.getObjEntity(entityIdentifier).getJavaClass();
		return entityClass;
	}

	private Queueable findEntityByWillowId(ObjectContext objectContext, Long entityId, String entityIdentifier) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityIdentifier));
		q.andQualifier(ExpressionFactory.matchDbExp("id", entityId));
		q.andQualifier(ExpressionFactory.matchExp("college", collegeRequestService.getRequestingCollege()));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}

	private Queueable findEntityByAngelId(ObjectContext objectContext, Long entityId, String entityIdentifier) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityIdentifier));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", entityId));
		q.andQualifier(ExpressionFactory.matchExp("college", collegeRequestService.getRequestingCollege()));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}

	private static HollowStub toHollow(ReplicationStub stub) {

		HollowStub hollowStub = new HollowStub();
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier());
		hollowStub.setAngelId(stub.getAngelId());

		Date today = new Date();

		hollowStub.setModified(today);
		hollowStub.setCreated(today);

		return hollowStub;
	}

	private class RelationShipCallbackImpl implements RelationShipCallback {

		private ObjectContext ctx;

		private List<ReplicatedRecord> result;

		private TransactionGroup group;

		public RelationShipCallbackImpl(ObjectContext ctx, TransactionGroup group, List<ReplicatedRecord> result) {
			super();
			this.ctx = ctx;
			this.group = group;
			this.result = result;
		}

		/**
		 * @see ish.oncourse.server.replication.updater.RelationShipCallback#updateRelationShip(java.lang.Long,
		 *      java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public <M extends Queueable> M updateRelationShip(Long angelId, Class<M> clazz) {

			String entityIdentifier = getEntityName(clazz);

			ReplicationStub stub = findInTransactionGroup(angelId, entityIdentifier);

			if (stub != null) {
				ObjectContext relationContext = cayenneService.newNonReplicatingContext();
				M relatedObject = (M) updateRecord(relationContext, stub, group, result);
				M local = (M) ctx.localObject(relatedObject.getObjectId(), null);
				return local;
			} else {
				return (M) findEntityByAngelId(ctx, angelId, entityIdentifier);
			}
		}

		private ReplicationStub findInTransactionGroup(Long entityId, String entityIdentifier) {
			List<ReplicationStub> stubs = new ArrayList<ReplicationStub>(group.getAttendanceOrBinaryDataOrBinaryInfo());
			for (ReplicationStub s : stubs) {
				if (entityId.equals(s.getAngelId()) && entityIdentifier.equals(s.getEntityIdentifier())) {
					group.getAttendanceOrBinaryDataOrBinaryInfo().remove(s);
					return s;
				}
			}
			return null;
		}
	}
}
