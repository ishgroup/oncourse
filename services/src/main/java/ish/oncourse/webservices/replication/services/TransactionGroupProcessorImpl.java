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
import org.apache.cayenne.DeleteDenyException;
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
 */
public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(TransactionGroupProcessorImpl.class);

	/**
	 * WebSiteService
	 */
	private final IWebSiteService webSiteService;

	/**
	 * WillowUpdater
	 */
	private final IWillowUpdater willowUpdater;

	/**
	 * Atomic context
	 */
	private ObjectContext atomicContext;

	/**
	 * Replication result
	 */
	private List<ReplicatedRecord> result;

	/**
	 * Transaction group
	 */
	private TransactionGroup transactionGroup;

	/**
	 * Constructor
	 */
	@Inject
	public TransactionGroupProcessorImpl(ICayenneService cayenneService, IWebSiteService webSiteService, IWillowUpdater willowUpdater) {
		super();
		this.webSiteService = webSiteService;
		this.willowUpdater = willowUpdater;
		this.atomicContext = cayenneService.newNonReplicatingContext();
	}

	/**
	 * @see ish.oncourse.webservices.ITransactionGroupProcessor#processGroup(TransactionGroup)
	 */
	@Override
	public List<ReplicatedRecord> processGroup(TransactionGroup group) {

		this.result = new ArrayList<ReplicatedRecord>();
		this.transactionGroup = group;

		while (!group.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
			ReplicationStub currentStub = group.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
			processStub(currentStub);
		}

		try {
			atomicContext.commitChanges();

			for (ReplicatedRecord r : result) {
				String willowIdentifier = EntityMapping.getWillowEntityIdentifer(r.getStub().getEntityIdentifier());
				List<Queueable> savedObjects = objectsByAngelId(r.getStub().getAngelId(), willowIdentifier);
				if (!savedObjects.isEmpty()) {
					r.getStub().setWillowId(savedObjects.get(0).getId());
				}
			}

		} catch (Exception e) {
			logger.error("Failed to atomically process transaction group.", e);
			atomicContext.rollbackChanges();
			updateReplicationStatus(result, Status.FAILED,
					String.format("Failed to atomically process transaction group:%s", e.getMessage()));
		}

		return result;
	}

	/**
	 * Updates replication status for the list of replicated records
	 * 
	 * @param records replicated records
	 * @param status replication status
	 * @param message status message
	 */
	private static void updateReplicationStatus(List<ReplicatedRecord> records, Status status, String message) {
		for (ReplicatedRecord record : records) {
			record.setStatus(status);
			if(record.getMessage() == null) {
				record.setMessage(message);
			}
		}
	}

	/**
	 * Process on one single stub either Full or Delete. Creates correspondent Cayenne objects with relationships if any, or delete cayenne
	 * objects taking entity relations into account.
	 * 
	 * @param currentStub replication stub.
	 * @return cayenne object which was updated/deleted.
	 */
	private Queueable processStub(ReplicationStub currentStub) {

		logger.info(String.format("Process stub for %s with angelId:%s and willowId:%s.", currentStub.getEntityIdentifier(),
				currentStub.getAngelId(), currentStub.getWillowId()));

		ReplicatedRecord replRecord = toReplicatedRecord(currentStub);
		result.add(replRecord);

		try {

			String willowIdentifier = EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier());

			if (currentStub.getAngelId() == null) {
				replRecord.setStatus(Status.FAILED);
				replRecord.setMessage(String.format("Empty angelId for object: %s.", willowIdentifier));
				return null;
			}

			List<Queueable> objects = objectsByAngelId(currentStub.getAngelId(), willowIdentifier);

			switch (objects.size()) {
			case 0: {
				if (currentStub instanceof DeletedStub) {
					// ignore object was already deleted
					return null;
				}
				// creating new object
				return createObject(currentStub);
			}
			case 1: {
				Queueable objectToUpdate = objects.get(0);

				if (currentStub.getWillowId() != null && !objectToUpdate.getId().equals(currentStub.getWillowId())) {
					replRecord.setStatus(Status.FAILED);
					replRecord.setMessage(String.format("WillowId doesnt match for object: %s. Expected: %s, but got %s.",
							willowIdentifier, objectToUpdate.getId(), currentStub.getWillowId()));
					return null;
				}

				if (currentStub instanceof DeletedStub) {
					deleteObject(objectToUpdate);
					return null;
				} else {
					willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl());
					return objectToUpdate;
				}
			}
			default:
				// FAILURE angelId uniques
				replRecord.setStatus(Status.FAILED);
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

	/**
	 * Deletes replicable object
	 * 
	 * @param objectToDelete object to delete
	 */
	private void deleteObject(Queueable objectToDelete) {

		ClassDescriptor descriptor = atomicContext.getEntityResolver().getClassDescriptor(objectToDelete.getObjectId().getEntityName());
		boolean shouldDelete = true;

		for (ObjRelationship relationship : descriptor.getEntity().getRelationships()) {

			if (relationship.getDeleteRule() == DeleteRule.DENY) {
				ArcProperty property = (ArcProperty) descriptor.getProperty(relationship.getName());
				Collection<Queueable> relatedObjects = toCollection(property.readProperty(objectToDelete));

				for (Queueable r : relatedObjects) {
					String entityIdentifier = r.getObjectId().getEntityName();
					if (r.getAngelId() == null) {
						// log the record details to have the ability to
						// erase it from the existing dump if we shouldn't
						// delete it.
						logger.error("The record with willowId:" + r.getId() + " and identifier:" + entityIdentifier + " has null angelId!");
					}

					ReplicationStub relStub = takeStubFromGroupByAngelId(r.getAngelId(), entityIdentifier);
					if (relStub != null && relStub instanceof DeletedStub) {
						processStub(relStub);
					} else {
						shouldDelete = false;
						break;
					}
				}
			}
		}

		if (shouldDelete) {
			ObjectContext deleteContext = null;
			try {
				deleteContext = atomicContext.createChildContext();
				Queueable local = (Queueable) deleteContext.localObject(objectToDelete.getObjectId(), null);
				deleteContext.deleteObject(local);
				deleteContext.commitChangesToParent();
			} catch (DeleteDenyException de) {
				logger.error(String.format("Unable to delete record with angelId:%s and identifier:%s.", objectToDelete.getAngelId(),
						objectToDelete.getObjectId().getEntityName()), de);
				if (deleteContext != null) {
					deleteContext.rollbackChangesLocally();
				}
			}
		}
	}

	/**
	 * Creates replicable object
	 * 
	 * @param currentStub soap stub
	 * @return queueable object
	 */
	private Queueable createObject(ReplicationStub currentStub) {

		try {
			Queueable objectToUpdate = atomicContext.newObject(getEntityClass(atomicContext,
					EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier())));
			College currentCollege = (College) atomicContext.localObject(webSiteService.getCurrentCollege().getObjectId(), null);
			objectToUpdate.setCollege(currentCollege);
			willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl());
			return objectToUpdate;
		} catch (CayenneRuntimeException e) {
			if (e.getCause() instanceof SQLException) {
				SQLException sqlExp = (SQLException) e.getCause();
				if (sqlExp.getSQLState().startsWith("23")) {
					List<Queueable> existingObjects = objectsByAngelId(currentStub.getAngelId(), currentStub.getEntityIdentifier());
					if (existingObjects.size() == 1) {
						Queueable existingToUpdate = existingObjects.get(0);
						willowUpdater.updateEntityFromStub(currentStub, existingToUpdate, new RelationShipCallbackImpl());
						return existingToUpdate;
					}
				}
			}
			throw e;
		}
	}

	/**
	 * Finds entity by angel id.
	 * 
	 * @param angelId Angel identifier
	 * @param entityName Entity name
	 * @return list of queueable
	 */
	@SuppressWarnings("unchecked")
	private List<Queueable> objectsByAngelId(Long angelId, String entityName) {
		SelectQuery q = new SelectQuery(getEntityClass(atomicContext, entityName));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", angelId));
		q.andQualifier(ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()));
		return atomicContext.performQuery(q);
	}

	/**
	 * Takes stub from transaction group. Stub is removed when it found.
	 * 
	 * @param angelId angel id
	 * @param entityName entity name
	 * @return replication stub, null if not found.
	 */
	public ReplicationStub takeStubFromGroupByAngelId(Long angelId, String entityName) {
		if (angelId == null) {
			return null;
		}

		String angelIdentifier = EntityMapping.getAngelEntityIdentifer(entityName);

		for (ReplicationStub s : new ArrayList<ReplicationStub>(transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo())) {
			if (angelId.equals(s.getAngelId()) && s.getEntityIdentifier().equals(angelIdentifier)) {
				transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().remove(s);
				return s;
			}
		}

		return null;
	}

	/**
	 * Callback for updating relationships.
	 * 
	 * @author anton
	 */
	class RelationShipCallbackImpl implements RelationShipCallback {

		/**
		 * @see ish.oncourse.server.replication.updater.RelationShipCallback#updateRelationShip(java.lang.Long, java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public <M extends Queueable> M updateRelationShip(Long angelId, Class<M> clazz) {

			String entityIdentifier = getEntityName(clazz);
			List<Queueable> list = objectsByAngelId(angelId, entityIdentifier);

			if (!list.isEmpty()) {
				return (M) list.get(0);
			} else {
				ReplicationStub stub = takeStubFromGroupByAngelId(angelId, entityIdentifier);
				if (stub != null) {
					M relatedObject = (M) processStub(stub);
					return (M) Cayenne.objectForPK(atomicContext, relatedObject.getObjectId());
				} else {
					return uncommittedObjectByAngelId(angelId, clazz);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private <M extends Queueable> M uncommittedObjectByAngelId(Long angelId, Class<M> clazz) {

			String entityName = getEntityName(clazz);

			for (Object obj : atomicContext.uncommittedObjects()) {
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
