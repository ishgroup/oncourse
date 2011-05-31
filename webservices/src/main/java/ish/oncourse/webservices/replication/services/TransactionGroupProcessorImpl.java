package ish.oncourse.webservices.replication.services;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityClass;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.toCollection;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.toReplicatedRecord;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.services.ICollegeRequestService;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DeleteRule;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.reflect.ArcProperty;
import org.apache.cayenne.reflect.ClassDescriptor;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * The core replication class responsible for replaying data changes.
 * @author anton
 *
 */
public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

	private static final Logger logger = Logger.getLogger(TransactionGroupProcessorImpl.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeRequestService collegeRequestService;

	@Inject
	private IWillowUpdater willowUpdater;

	/**
	 * @see ish.oncourse.webservices.ITransactionGroupProcessor#processGroup(TransactionGroup)
	 */
	@Override
	public List<ReplicatedRecord> processGroup(TransactionGroup transactionGroup) {

		List<ReplicatedRecord> result = new ArrayList<ReplicatedRecord>();

		while (!transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
			ReplicationStub currentStub = transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
			ObjectContext objectContext = cayenneService.newNonReplicatingContext();
			processStub(objectContext, currentStub, transactionGroup, result);
		}

		return result;
	}

	/**
	 * Process on one single stub either Full or Delete. Creates correspondent Cayenne objects with relationships if any, or delete cayenne objects
	 * taking entity relations into account.
	 * @param objectContext cayenne context.
	 * @param currentStub replication stub.
	 * @param transactionGroup transaction group.
	 * @param result where replicaiton result is appended.
	 * @return cayenne object which was updated/deleted.
	 */
	private Queueable processStub(ObjectContext objectContext, ReplicationStub currentStub, TransactionGroup transactionGroup,
			List<ReplicatedRecord> result) {

		ReplicatedRecord replRecord = toReplicatedRecord(currentStub);
		result.add(replRecord);

		Queueable objectToUpdate = lookupObject(objectContext, currentStub, replRecord);

		if (objectToUpdate != null) {
			try {
				if (currentStub instanceof DeletedStub) {
					ClassDescriptor descriptor = objectContext.getEntityResolver().getClassDescriptor(
							objectToUpdate.getObjectId().getEntityName());

					for (ObjRelationship relationship : descriptor.getEntity().getRelationships()) {

						if (relationship.getDeleteRule() == DeleteRule.DENY) {

							ArcProperty property = (ArcProperty) descriptor.getProperty(relationship.getName());

							Collection<Queueable> relatedObjects = toCollection(property.readProperty(objectToUpdate));

							for (Queueable r : relatedObjects) {
								String entityIdentifier = r.getObjectId().getEntityName();
								ReplicationStub relStub = takeStubFromGroupByAngelId(transactionGroup, r.getAngelId(), entityIdentifier);
								if (relStub != null && relStub instanceof DeletedStub) {
									ObjectContext relationContext = cayenneService.newNonReplicatingContext();
									processStub(relationContext, relStub, transactionGroup, result);
								}
							}
						}
					}

					objectContext.deleteObject(objectToUpdate);
					objectContext.commitChanges();

				} else {
					College college = (College) objectContext.localObject(collegeRequestService.getRequestingCollege().getObjectId(), null);
					objectToUpdate.setCollege(college);
					
					willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl(objectContext,
							transactionGroup, result));
					
					objectContext.commitChanges();
					
					replRecord.getStub().setWillowId(objectToUpdate.getId());
					replRecord.getStub().setEntityIdentifier(currentStub.getEntityIdentifier());
				}
			} catch (CayenneRuntimeException e) {
				logger.error("Failed to commit object.", e);
				objectContext.rollbackChanges();
				replRecord.setStatus(Status.FAILED);
				replRecord.setMessage(e.getMessage());
			} catch (Exception e) {
				logger.error("Failed with generic exception.", e);
				objectContext.rollbackChanges();
				replRecord.setStatus(Status.FAILED);
				replRecord.setMessage(e.getMessage());
			}
		}

		return objectToUpdate;
	}

	/**
	 * 
	 * @param objectContext
	 * @param stub
	 * @param replRecord
	 * @return
	 */
	private Queueable lookupObject(ObjectContext objectContext, final ReplicationStub stub, final ReplicatedRecord replRecord) {

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

					String message = String.format("Have willowId:null but found existing record for entity:%s and angelId:%s.",
							stub.getEntityIdentifier(), stub.getAngelId());

					logger.warn(message);
					
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

		return objectToUpdate;
	}

	/**
	 * Finds entity by willow id.
	 * 
	 * @param objectContext cayenne context.
	 * @param willowId
	 * @param entityName
	 * @return
	 */
	private Queueable findEntityByWillowId(ObjectContext objectContext, Long willowId, String entityName) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityName));
		q.andQualifier(ExpressionFactory.matchDbExp("id", willowId));
		q.andQualifier(ExpressionFactory.matchExp("college", collegeRequestService.getRequestingCollege()));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}
	
	
	/**
	 * Finds entity by angel id.
	 * 
	 * @param objectContext cayenne context.
	 * @param angelId angelId
	 * @param entityName entity name
	 * @return
	 */
	private Queueable findEntityByAngelId(ObjectContext objectContext, Long angelId, String entityName) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityName));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", angelId));
		q.andQualifier(ExpressionFactory.matchExp("college", collegeRequestService.getRequestingCollege()));
		return (Queueable) Cayenne.objectForQuery(objectContext, q);
	}

	/**
	 * Takes stub from transaction group. Stub is removed when it found.
	 * @param transactionGroup transaction group.
	 * @param angelId angel id
	 * @param entityName entity name
	 * @return replication stub, null if not found.
	 */
	public static ReplicationStub takeStubFromGroupByAngelId(TransactionGroup transactionGroup, Long angelId, String entityName) {
		
		for (ReplicationStub s : new ArrayList<ReplicationStub>(transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo())) {
			if (angelId.equals(s.getAngelId()) && entityName.equals(s.getEntityIdentifier())) {
				transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().remove(s);
				return s;
			}
		}

		return null;
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

			ReplicationStub stub = takeStubFromGroupByAngelId(group, angelId, entityIdentifier);

			if (stub != null) {
				ObjectContext relationContext = cayenneService.newNonReplicatingContext();
				M relatedObject = (M) processStub(relationContext, stub, group, result);
				return (M) Cayenne.objectForPK(ctx, relatedObject.getObjectId());
			} else {
				return (M) findEntityByAngelId(ctx, angelId, entityIdentifier);
			}
		}
	}
}
