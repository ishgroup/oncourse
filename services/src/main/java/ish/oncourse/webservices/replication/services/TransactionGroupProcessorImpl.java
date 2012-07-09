package ish.oncourse.webservices.replication.services;

import ish.common.types.EntityMapping;
import ish.oncourse.model.*;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.v4.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.util.*;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.*;

/**
 * The core replication class responsible for replaying data changes.
 *
 * @author anton
 */
public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

    static final String MESSAGE_TEMPLATE_NO_STUB = "Cannot delete object willowId:%d and identifier:%s\nbecause there is relationship to object willowId:%d and identifier:%s!";
    static final String MESSAGE_TEMPLATE_NO_ANGELID = "Cannot delete object willowId:%d and identifier:%s\nbecause there is relationship to object willowId:%d and identifier:%s but without has null angelId!";

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
    private IFileStorageAssetService fileStorageAssetService;

    /**
	 * Atomic context
	 */
	private ObjectContext atomicContext;

	/**
	 * Replication result
	 */
	private List<GenericReplicatedRecord> result;

	/**
	 * Transaction group
	 */
	private GenericTransactionGroup transactionGroup;


	/**
	 * Constructor
	 */
	@Inject
	public TransactionGroupProcessorImpl(ICayenneService cayenneService,
                                         IWebSiteService webSiteService,
                                         IWillowUpdater willowUpdater,
                                         IFileStorageAssetService fileStorageAssetService) {
		super();
		this.webSiteService = webSiteService;
		this.willowUpdater = willowUpdater;
        this.fileStorageAssetService = fileStorageAssetService;
        this.atomicContext = cayenneService.newNonReplicatingContext();
	}

	/**
	 * @see ish.oncourse.webservices.ITransactionGroupProcessor#processGroup(GenericTransactionGroup)
	 */
	@Override
	public List<GenericReplicatedRecord> processGroup(GenericTransactionGroup group) {
		this.transactionGroup = group;
		this.result = new ArrayList<GenericReplicatedRecord>();

		for (GenericReplicationStub currentStub : group.getAttendanceOrBinaryDataOrBinaryInfo()) {
			GenericReplicatedRecord replRecord = toReplicatedRecord(currentStub, false);
			result.add(replRecord);
		}

		try {

			while (!group.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
				GenericReplicationStub currentStub = group.getAttendanceOrBinaryDataOrBinaryInfo().remove(0);
				processStub(currentStub);
			}

			atomicContext.commitChanges();

			for (GenericReplicatedRecord r : result) {
				String willowIdentifier = EntityMapping.getWillowEntityIdentifer(r.getStub().getEntityIdentifier());
				//no reason in upgrade generated data
				if (!QueuedStatistic.class.getSimpleName().equals(willowIdentifier)) {
					List<Queueable> savedObjects = objectsByAngelId(r.getStub().getAngelId(), willowIdentifier);
					if (!savedObjects.isEmpty()) {
						r.getStub().setWillowId(savedObjects.get(0).getId());
					}
				}
			}

		} catch (Exception e) {
            String message = getErrorMessageBy(e);
			atomicContext.rollbackChanges();
            logger.error(message, e);
            updateReplicationStatusToFailed(result, message);
		}
		return result;
	}


    private String getErrorMessageBy(Exception e)
    {
        College college = null;

        /**
         * The catch has been introduced to exclude IllegalStateException when session expired.
         */
        try
        {
            if (webSiteService != null)
            {
                college = webSiteService.getCurrentCollege();
            }
        }
        catch (Exception e1)
        {
            /**
             * Log the exception to get information why it can not get college.
             */
            logger.error(e1.getMessage(), e);
        }


        if (college != null)
        {
            return  String.format("Failed to process transaction group: %s and collegeId: %s",
                    e.getMessage(),
                    college.getId());
        }
        else
        {
            return  String.format("Failed to process transaction group: %s",
                    e.getMessage());
        }
    }

	/**
	 * Updates replication status for the list of replicated records
	 *
	 * @param records
	 *            replicated records
	 * @param message
	 *            status message
	 */
	private static void updateReplicationStatusToFailed(List<GenericReplicatedRecord> records, String message) {
		for (GenericReplicatedRecord record : records) {
			record.setFailedStatus();
			if (record.getMessage() == null) {
				record.setMessage(message);
			}
		}
	}

	private void cleanupStatistic(GenericQueuedStatisticStub statisticStub) {
		final SelectQuery q = new SelectQuery(QueuedStatistic.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedStatistic.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		q.andQualifier(ExpressionFactory.lessExp(QueuedStatistic.RECEIVED_TIMESTAMP_PROPERTY, statisticStub.getReceivedTimestamp()));
		@SuppressWarnings("unchecked")
		List<QueuedStatistic> statisticForDelete = atomicContext.performQuery(q);
		if (!statisticForDelete.isEmpty()) {
			atomicContext.deleteObjects(statisticForDelete);
		}
	}

	/**
	 * Process on one single stub either Full or Delete. Creates correspondent
	 * Cayenne objects with relationships if any, or delete cayenne objects
	 * taking entity relations into account.
	 *
	 * @param currentStub
	 *            replication stub.
	 * @return cayenne object which was updated/deleted.
	 */
	private Queueable processStub(GenericReplicationStub currentStub) {

		logger.info(String.format("Process stub for %s with angelId:%s and willowId:%s.", currentStub.getEntityIdentifier(),
				currentStub.getAngelId(), currentStub.getWillowId()));

		GenericReplicatedRecord replRecord = getReplicatedRecordForStub(currentStub);

		if (replRecord == null) {
			throw new IllegalArgumentException(String.format("Replication result is not set for %s with angelId:%s and willowId:%s.", currentStub.getEntityIdentifier(),
					currentStub.getAngelId(), currentStub.getWillowId()));
		}

		if (currentStub instanceof GenericQueuedStatisticStub) {
			final GenericQueuedStatisticStub statisticStub = (GenericQueuedStatisticStub) currentStub;
			if (Boolean.TRUE.equals(statisticStub.isCleanupStub())) {
				//we should commit current statistic changes before commit
				atomicContext.commitChanges();
				cleanupStatistic(statisticStub);
				return null;
			} else {
				final List<QueuedStatistic> objects = statisticByEntity(statisticStub.getStackedEntityIdentifier());
				switch (objects.size()) {
				case 0:
					return createObject(currentStub);
				case 1:
					QueuedStatistic objectToUpdate = objects.get(0);
					willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl());
					return objectToUpdate;
				default:
					//we should not throw and exception because even if this occurs on next replication data will be correct.
					String message = String.format("%s statistic objects found for entity:%s", objects.size(),
						statisticStub.getStackedEntityIdentifier());
		            logger.warn(message);
		            return null;
				}
			}
		}

		String willowIdentifier = EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier());

		if (currentStub.getAngelId() == null) {
            String message = String.format("Empty angelId for object object: %s willowId: %s", willowIdentifier, currentStub.getWillowId());
            replRecord.setFailedStatus();
            replRecord.setMessage(message);
            throw new IllegalArgumentException(message);
		}

		List<Queueable> objects = objectsByAngelId(currentStub.getAngelId(), willowIdentifier);

		if (objects.isEmpty()) {
			//we need this since a lot of old records from angel has angelId=null.
			objects = objectsByWillowId(currentStub.getWillowId(), willowIdentifier);
		}

		switch (objects.size()) {
		case 0: {
			if (currentStub instanceof GenericDeletedStub) {
				// ignore object was already deleted
				return null;
			}
			// creating new object
            Queueable objectToUpdate = createObject(currentStub);
            updateFileStorage(currentStub, objectToUpdate);
			return objectToUpdate;
		}
		case 1: {
			Queueable objectToUpdate = objects.get(0);

			if (currentStub.getWillowId() != null && !objectToUpdate.getId().equals(currentStub.getWillowId())) {

                String message = String.format("WillowId doesn't match for object: %s. Expected: %s, but got %s.", willowIdentifier,
						objectToUpdate.getId(), currentStub.getWillowId());
                replRecord.setFailedStatus();
                replRecord.setMessage(message);
                throw new IllegalArgumentException(message);
			}

            if (currentStub instanceof GenericDeletedStub) {

				deleteObject(objectToUpdate);
                updateFileStorage(currentStub, objectToUpdate);
				return null;
			} else {
				willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl());
                updateFileStorage(currentStub,objectToUpdate);
				return objectToUpdate;
			}
        }
		default:
			// FAILURE angelId unique
            String message = String.format("%s objects found for angelId:%s with entity %s", objects.size(), currentStub.getAngelId(), currentStub.getEntityIdentifier());
            replRecord.setFailedStatus();
            replRecord.setMessage(message);
            throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Takes replicatedRecord object which corresponds to passed soap stub from prefilled replication result.
	 * @param soapStub  - stub
	 * @return - record for the stub
	 */
	private GenericReplicatedRecord getReplicatedRecordForStub(GenericReplicationStub soapStub) {
		for (GenericReplicatedRecord r : this.result) {
			if (r.getStub().getEntityIdentifier().equalsIgnoreCase(soapStub.getEntityIdentifier())
					&& r.getStub().getAngelId().equals(soapStub.getAngelId())) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Deletes replicable object.
     * The method:
     * 1. finds all relationships for the objectToDelete which have DeleteRule.DENY
     * 2. gets all related objects for every relationship
     * 3. finds and proccid
	 *
	 * @param objectToDelete
	 *            object to delete
	 */
	private void deleteObject(Queueable objectToDelete) {

		ClassDescriptor descriptor = atomicContext.getEntityResolver().getClassDescriptor(objectToDelete.getObjectId().getEntityName());

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
                        String message = String.format(MESSAGE_TEMPLATE_NO_ANGELID,objectToDelete.getId(), objectToDelete.getObjectId().getEntityName(), r.getId(), entityIdentifier);
                        throw new IllegalArgumentException(message);
					}

					GenericReplicationStub relStub = takeStubFromGroupByAngelId(r.getAngelId(), entityIdentifier);

					if (relStub != null)
                    {
						processStub(relStub);
					}
                    else
                    {
                        String message = String.format(MESSAGE_TEMPLATE_NO_STUB, objectToDelete.getId(), objectToDelete.getObjectId().getEntityName(), r.getId(), entityIdentifier);
                        throw new IllegalArgumentException(message);
                    }
				}
			}
		}

		atomicContext.deleteObject(objectToDelete);
	}

	/**
	 * Creates replicable object
	 *
	 * @param currentStub
	 *            soap stub
	 * @return queueable object
	 */
	private Queueable createObject(GenericReplicationStub currentStub) {

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
	 * @param angelId
	 *            Angel identifier
	 * @param entityName
	 *            Entity name
	 * @return list of queueable
	 */
	@SuppressWarnings("unchecked")
	private List<Queueable> objectsByAngelId(Long angelId, String entityName) {
		SelectQuery q = new SelectQuery(getEntityClass(atomicContext, entityName));
		q.andQualifier(ExpressionFactory.matchDbExp("angelId", angelId));
		q.andQualifier(ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()));
		return atomicContext.performQuery(q);
	}

	@SuppressWarnings("unchecked")
	private List<QueuedStatistic> statisticByEntity(final String entityName) {
		SelectQuery q = new SelectQuery(QueuedStatistic.class);
		q.andQualifier(ExpressionFactory.matchDbExp(QueuedStatistic.ENTITY_IDENTIFIER_PROPERTY, entityName));
		q.andQualifier(ExpressionFactory.matchExp(QueuedStatistic.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		return atomicContext.performQuery(q);
	}

	/**
	 * Finds entity by willow id.
	 * @param willowId primary key in willow system
	 * @param entityName Entity identifier
	 * @return - list Queueable's objects
	 */
	@SuppressWarnings({"unchecked" })
	private List<Queueable> objectsByWillowId(Long willowId, String entityName) {
		List<Queueable> records = Collections.emptyList();
		if (willowId != null) {
			SelectQuery q = new SelectQuery(getEntityClass(atomicContext, entityName));
			q.andQualifier(ExpressionFactory.matchDbExp("id", willowId));
			q.andQualifier(ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()));
			records = atomicContext.performQuery(q);
		}
		return records;
	}

	/**
	 * Takes stub from transaction group. Stub is removed when it found.
	 *
	 * @param angelId
	 *            angel id
	 * @param entityName
	 *            entity name
	 * @return replication stub, null if not found.
	 */
	public GenericReplicationStub takeStubFromGroupByAngelId(Long angelId, String entityName) {
		if (angelId == null) {
			return null;
		}

		String angelIdentifier = EntityMapping.getAngelEntityIdentifer(entityName);

		for (GenericReplicationStub s : new ArrayList<GenericReplicationStub>(transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo())) {
			if (angelId.equals(s.getAngelId()) && s.getEntityIdentifier().equals(angelIdentifier)) {
				transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo().remove(s);
				return s;
			}
		}
		return null;
	}

    /**
     * The method puts or deletes content to/from file storage.
     */
    private void updateFileStorage(GenericReplicationStub stub, Queueable entity)
    {
        //the file storage processing
        try {
            if (entity instanceof  BinaryInfo && stub instanceof GenericDeletedStub) {
                    fileStorageAssetService.delete((BinaryInfo)entity);
            }
            //TODO: the conde should be adjusted after we will stop saving BinaryData to the database.
            else  if (entity instanceof BinaryData && stub instanceof GenericBinaryDataStub) {
            	GenericBinaryDataStub binaryDataStub = (GenericBinaryDataStub) stub;
               fileStorageAssetService.put(binaryDataStub.getContent(), ((BinaryData) entity).getBinaryInfo());
            }
        } catch (Throwable e) {
            logger.error(String.format("Cannot update file storage with the stub %s and entity %s.", stub, entity),e);
        }
    }

	/**
	 * Callback for updating relationships.
	 *
	 * @author anton
	 */
	class RelationShipCallbackImpl implements RelationShipCallback {

		/**
		 * @see ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback#updateRelationShip(java.lang.Long,java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public <M extends Queueable> M updateRelationShip(Long angelId, Class<M> clazz) {

            if (angelId == null) {
                return null;
            }

            String entityIdentifier = getEntityName(clazz);
            List<Queueable> list = objectsByAngelId(angelId, entityIdentifier);

            if (!list.isEmpty()) {
                return (M) list.get(0);
            } else {
            	GenericReplicationStub stub = takeStubFromGroupByAngelId(angelId, entityIdentifier);
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

            assert angelId != null;
			String entityName = getEntityName(clazz);

			for (Object obj : atomicContext.uncommittedObjects()) {
				if (obj instanceof Queueable) {
					Queueable entity = (Queueable) obj;
					if (angelId.equals(entity.getAngelId()) && entity.getObjectId().getEntityName().equals(entityName)) {
						return (M) entity;
					}
				}
			}
            String message = String.format("Uncommitted object with angelId:%s and entityName:%s wasn't found.", angelId, entityName);
            throw new IllegalArgumentException(message);
		}
	}
}
