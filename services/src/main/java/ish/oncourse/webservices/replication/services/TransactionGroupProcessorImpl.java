package ish.oncourse.webservices.replication.services;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityClass;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.toCollection;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.toReplicatedRecord;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.EntityMapping;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.sql.SQLException;
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
 * 
 * @author anton
 * 
 */
public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

	private static final Logger logger = Logger.getLogger(TransactionGroupProcessorImpl.class);

	@Inject
	private final ICayenneService cayenneService;

	@Inject
	private final IWebSiteService webSiteService;

	@Inject
	private final IWillowUpdater willowUpdater;

	private boolean isAtomic;

	private ObjectContext atomicContext;

	public TransactionGroupProcessorImpl(ICayenneService cayenneService, IWebSiteService webSiteService, IWillowUpdater willowUpdater,
			boolean isAtomic) {

		super();

		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
		this.willowUpdater = willowUpdater;
		this.isAtomic = isAtomic;
	}

	private ObjectContext getGroupContext() {
		if (isAtomic) {
			return this.atomicContext;
		}
		return cayenneService.newNonReplicatingContext();
	}

	/**
	 * @see ish.oncourse.webservices.ITransactionGroupProcessor#processGroup(TransactionGroup)
	 */
	@Override
	public List<ReplicatedRecord> processGroup(TransactionGroup transactionGroup) {
		
		if (isAtomic) {
			this.atomicContext = cayenneService.newNonReplicatingContext();
		}
		
		List<ReplicatedRecord> result = new ArrayList<ReplicatedRecord>();

		while (!transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
			ReplicationStub currentStub = transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
			processStub(getGroupContext(), currentStub, transactionGroup, result);
		}

		if (isAtomic) {
			try {
				atomicContext.commitChanges();

				ObjectContext refereshContext = cayenneService.newNonReplicatingContext();

				for (ReplicatedRecord r : result) {
					
					String willowIdentifier = EntityMapping.getWillowEntityIdentifer(r.getStub()
							.getEntityIdentifier());
					
					List<Queueable> savedObjects = objectsByAngelId(refereshContext, r.getStub().getAngelId(), willowIdentifier);
					
					Long willowId = (!savedObjects.isEmpty()) ? savedObjects.get(0).getId() : null;
					r.getStub().setWillowId(willowId);
				}
			} catch (Exception e) {
				logger.error("Failed to atomically process transaction group.", e);
				atomicContext.rollbackChanges();
				updateReplicationStatus(result, Status.FAILED, String.format("Failed to atomically process transaction group:%s", e.getMessage()));
			}
			
			this.atomicContext = null;
		}

		return result;
	}

	private static void updateReplicationStatus(List<ReplicatedRecord> records, Status status, String message) {
		for (ReplicatedRecord record : records) {
			record.setStatus(status);
			record.setMessage(message);
		}
	}

	/**
	 * Process on one single stub either Full or Delete. Creates correspondent
	 * Cayenne objects with relationships if any, or delete cayenne objects
	 * taking entity relations into account.
	 * 
	 * @param objectContext
	 *            cayenne context.
	 * @param currentStub
	 *            replication stub.
	 * @param transactionGroup
	 *            transaction group.
	 * @param result
	 *            where replicaiton result is appended.
	 * @return cayenne object which was updated/deleted.
	 */
	private Queueable processStub(ObjectContext objectContext, ReplicationStub currentStub, TransactionGroup transactionGroup,
			List<ReplicatedRecord> result) {

		logger.info(String.format("Process stub for %s with angelId:%s and willowId:%s.", currentStub.getEntityIdentifier(),
				currentStub.getAngelId(), currentStub.getWillowId()));

		ReplicatedRecord replRecord = toReplicatedRecord(currentStub);
		result.add(replRecord);

		try {

			String willowIdentifier = EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier());

			if (currentStub.getAngelId() == null) {
				replRecord.setStatus(Status.EMPTY_ANGELID);
				replRecord.setMessage(String.format("Empty angelId for object: %s.", willowIdentifier));
				return null;
			}

			List<Queueable> objects = objectsByAngelId(objectContext, currentStub.getAngelId(), willowIdentifier);

			switch (objects.size()) {
			case 0: {
				if (currentStub instanceof DeletedStub) {
					// ignore object was already deleted
					return null;
				}
				// creating new object
				return createObject(objectContext, replRecord, currentStub, transactionGroup, result);
			}
			case 1: {
				Queueable objectToUpdate = objects.get(0);

				if (currentStub.getWillowId() != null && !objectToUpdate.getId().equals(currentStub.getWillowId())) {
					replRecord.setStatus(Status.WILLOWID_NOT_MATCH);
					replRecord.setMessage(String.format("WillowId doesnt match for object: %s. Expected: %s, but got %s.",
							willowIdentifier, objectToUpdate.getId(), currentStub.getWillowId()));
					return null;
				}

				if (currentStub instanceof DeletedStub) {
					deleteObject(objectContext, replRecord, objectToUpdate, transactionGroup, result);
					return null;
				}

				updateObject(objectContext, replRecord, currentStub, objectToUpdate, transactionGroup, result);
				return objectToUpdate;

			}
			default:
				// FAILURE angelId uniques
				replRecord.setStatus(Status.UNIQUES_FAILURE);
				replRecord.setMessage(String.format("%s objects found for angelId:%s", objects.size(), currentStub.getAngelId()));
				return null;
			}
		} catch (Exception e) {
			logger.error("Generic web service failure.", e);
			replRecord.setStatus(Status.FAILED);
			replRecord.setMessage(String.format("Generic webservice exception:%s", e.getMessage()));
			return null;
		}
	}

	private void deleteObject(ObjectContext objectContext, ReplicatedRecord replRecord, Queueable objectToUpdate,
			TransactionGroup transactionGroup, List<ReplicatedRecord> result) {

		try {
			ClassDescriptor descriptor = objectContext.getEntityResolver().getClassDescriptor(objectToUpdate.getObjectId().getEntityName());

			for (ObjRelationship relationship : descriptor.getEntity().getRelationships()) {

				if (relationship.getDeleteRule() == DeleteRule.DENY) {

					ArcProperty property = (ArcProperty) descriptor.getProperty(relationship.getName());

					Collection<Queueable> relatedObjects = toCollection(property.readProperty(objectToUpdate));

					for (Queueable r : relatedObjects) {
						String entityIdentifier = r.getObjectId().getEntityName();
						ReplicationStub relStub = takeStubFromGroupByAngelId(transactionGroup, r.getAngelId(), entityIdentifier);
						if (relStub != null && relStub instanceof DeletedStub) {
							processStub(getGroupContext(), relStub, transactionGroup, result);
						} else {
							objectContext.deleteObject(r);
						}
					}
				}
			}

			objectContext.deleteObject(objectToUpdate);
			if (!isAtomic) {
				objectContext.commitChanges();
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

	private Queueable createObject(ObjectContext objectContext, ReplicatedRecord replRecord, ReplicationStub currentStub,
			TransactionGroup transactionGroup, List<ReplicatedRecord> result) {
		try {
			Queueable objectToUpdate = objectContext.newObject(getEntityClass(objectContext,
					EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier())));

			List<ReplicatedRecord> tempResult = new ArrayList<ReplicatedRecord>();

			updateObject(objectContext, replRecord, currentStub, objectToUpdate, transactionGroup, tempResult);

			result.addAll(tempResult);

			return objectToUpdate;
		} catch (CayenneRuntimeException e) {
			logger.error("Failed to commit object.", e);
			objectContext.rollbackChanges();

			if (e.getCause() instanceof SQLException) {
				SQLException sqlExp = (SQLException) e.getCause();
				if (sqlExp.getSQLState().startsWith("23")) {
					List<Queueable> existingObjects = objectsByAngelId(objectContext, currentStub.getAngelId(),
							currentStub.getEntityIdentifier());
					if (existingObjects.size() == 1) {
						Queueable existingToUpdate = existingObjects.get(0);
						updateObject(objectContext, replRecord, currentStub, existingToUpdate, transactionGroup, result);
						return existingToUpdate;
					}
				}
			}

			replRecord.setStatus(Status.FAILED);
			replRecord.setMessage(e.getMessage());

			return null;
		} catch (Exception e) {
			logger.error("Failed to commit object.", e);
			objectContext.rollbackChanges();
			replRecord.setStatus(Status.FAILED);
			replRecord.setMessage(e.getMessage());
			return null;
		}
	}

	private void updateObject(ObjectContext objectContext, ReplicatedRecord replRecord, ReplicationStub currentStub,
			Queueable objectToUpdate, TransactionGroup transactionGroup, List<ReplicatedRecord> result) {
		try {
			College currentCollege = webSiteService.getCurrentCollege();
			College college = (College) objectContext.localObject(currentCollege.getObjectId(), null);
			objectToUpdate.setCollege(college);

			willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl(objectContext, transactionGroup,
					result));

			if (!isAtomic) {
				objectContext.commitChanges();
				replRecord.getStub().setWillowId(objectToUpdate.getId());
				replRecord.getStub().setEntityIdentifier(currentStub.getEntityIdentifier());
			}

		} catch (Exception e) {
			logger.error("Failed with generic exception.", e);
			objectContext.rollbackChanges();
			replRecord.setStatus(Status.FAILED);
			replRecord.setMessage(e.getMessage());
		}
	}

	/**
	 * Finds entity by angel id.
	 * 
	 * @param objectContext
	 *            cayenne context.
	 * @param angelId
	 *            angelId
	 * @param entityName
	 *            entity name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Queueable> objectsByAngelId(ObjectContext objectContext, Long angelId, String entityName) {
		SelectQuery q = new SelectQuery(getEntityClass(objectContext, entityName));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", angelId));
		q.andQualifier(ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()));
		return objectContext.performQuery(q);
	}

	/**
	 * Takes stub from transaction group. Stub is removed when it found.
	 * 
	 * @param transactionGroup
	 *            transaction group.
	 * @param angelId
	 *            angel id
	 * @param entityName
	 *            entity name
	 * @return replication stub, null if not found.
	 */
	public static ReplicationStub takeStubFromGroupByAngelId(TransactionGroup transactionGroup, Long angelId, String entityName) {

		String angelIdentifier = EntityMapping.getAngelEntityIdentifer(entityName);

		for (ReplicationStub s : new ArrayList<ReplicationStub>(transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo())) {
			if (angelId.equals(s.getAngelId()) && s.getEntityIdentifier().equals(angelIdentifier)) {
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

			List<Queueable> list = objectsByAngelId(ctx, angelId, entityIdentifier);

			if (!list.isEmpty()) {
				return (M) list.get(0);
			} else {
				ReplicationStub stub = takeStubFromGroupByAngelId(group, angelId, entityIdentifier);
				if (stub != null) {
					ObjectContext relationContext = getGroupContext();
					M relatedObject = (M) processStub(relationContext, stub, group, result);
					return (M) Cayenne.objectForPK(ctx, relatedObject.getObjectId());
				} else {
					return uncommittedObjectByAngelId(angelId, clazz);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private <M extends Queueable> M uncommittedObjectByAngelId(Long angelId, Class<M> clazz) {
			
			String entityName = getEntityName(clazz);
			
			for (Object obj : ctx.uncommittedObjects()) {
				if (obj instanceof Queueable) {
					Queueable entity = (Queueable) obj;
					if (angelId.equals(entity.getAngelId()) && entity.getObjectId().getEntityName().equals(entityName)) {
						return (M) entity;
					}
				}
			}

			return null;
		}
	}
}
