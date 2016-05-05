package ish.oncourse.webservices.replication.services;

import ish.common.types.EntityMapping;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedStatistic;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.util.GenericBinaryDataStub;
import ish.oncourse.webservices.util.GenericDeletedStub;
import ish.oncourse.webservices.util.GenericQueuedStatisticStub;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.StubUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.DeleteRule;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.reflect.ArcProperty;
import org.apache.cayenne.reflect.ClassDescriptor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.*;

/**
 * The core replication class responsible for replaying data changes.
 *
 * @author anton
 */
public class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

    static final String MESSAGE_TEMPLATE_NO_STUB = "Cannot delete object willowId:%d and identifier:%s\nbecause there is relationship to object willowId:%d and identifier:%s!";
    static final String MESSAGE_TEMPLATE_NO_ANGELID = "Cannot delete object willowId:%d and identifier:%s\nbecause there is relationship to object willowId:%d and identifier:%s but without angelId!";
	public static final String MERGE_KEY = "MERGE";
    /**
	 * Logger
	 */
	private static final Logger logger = LogManager.getLogger();

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
	private ICayenneService cayenneService;
	
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

    private QueuedStatisticProcessor queuedStatisticProcessor;
    private AttachmentProcessor attachmentProcessor;

	/**
	 * Constructor
	 */
	@Inject
	public TransactionGroupProcessorImpl(ICayenneService cayenneService, IWebSiteService webSiteService,
		IWillowUpdater willowUpdater, IFileStorageAssetService fileStorageAssetService) {
		super();
		this.webSiteService = webSiteService;
		this.willowUpdater = willowUpdater;
		this.cayenneService = cayenneService;
        this.atomicContext = this.cayenneService.newNonReplicatingContext();
        this.queuedStatisticProcessor = new QueuedStatisticProcessor(this.atomicContext,webSiteService,willowUpdater,this, false);
        this.attachmentProcessor = new AttachmentProcessor(fileStorageAssetService, webSiteService);
	}

	/**
	 * @see ish.oncourse.webservices.ITransactionGroupProcessor#processGroup(GenericTransactionGroup)
	 */
	@Override
	public List<GenericReplicatedRecord> processGroup(GenericTransactionGroup group) {
		return processGroup(group, 0);
	}
	
	private List<GenericReplicatedRecord> processGroup(GenericTransactionGroup group, int attempt) {
		this.transactionGroup = group;
		this.result = new ArrayList<>();
		
		// fill QueuedStatistic stubs with default values, this normally should be done by angel 
		// before it sends the stubs but it was not the case for angel versions up to 5.0
		queuedStatisticProcessor.fillQueuedStatisticStubs(group);

		List<GenericReplicationStub> stubs = new LinkedList<>(group.getGenericAttendanceOrBinaryDataOrBinaryInfo());
		
        try {
			if (transactionGroup.getTransactionKeys().contains(MERGE_KEY)) {
				processMergeTransaction(group);
			} else {
				processRegularTransaction(group);
			}
		} catch (Exception e) {
			atomicContext.rollbackChanges();
			String message = getErrorMessageBy(e);
			logger.error(message, e);
			int index = ExceptionUtils.indexOfType(e, SQLIntegrityConstraintViolationException.class);
			if (index != -1 && attempt < 3) {
				logger.error("Try to process transaction group one more time since concurrent processing detected");
				group.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(stubs);
				return processGroup(group, ++attempt);
			}
            updateReplicationStatusToFailed(result, message);
		}
		return result;
	}
	
	private void processRegularTransaction(GenericTransactionGroup group) {

		for (GenericReplicationStub currentStub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			GenericReplicatedRecord replRecord = toReplicatedRecord(currentStub, false);
			result.add(replRecord);
		}

		while (!group.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
			GenericReplicationStub currentStub = group.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(0);
			processStub(currentStub);
		}
		atomicContext.commitChanges();
		queuedStatisticProcessor.cleanupStatistic();
		fillWillowIds();
	}

	private void processMergeTransaction(GenericTransactionGroup group) {
		GenericReplicatedRecord contactDuplicateRec = null;
		try {
			MergeProcessor processor = MergeProcessor.valueOf(atomicContext, group.getGenericAttendanceOrBinaryDataOrBinaryInfo());
			
			GenericReplicatedRecord studentToUpdateRec = null;
			GenericReplicatedRecord tutorToUpdateRec = null;

			List<GenericReplicationStub> contactsToUpdate = processor.getStubBy(Contact.class.getSimpleName(), false);
			if (contactsToUpdate.size() > 0) {
				//not only Contact (with UPDATE action) for merge can be in merge transaction group, ContactRelation -> toContact/fromContact also can be in merge transaction group
				for (GenericReplicationStub contactToUpdate : contactsToUpdate) {
					processSingleStub(contactToUpdate, group);
				}
			} else {
				throw new IllegalStateException("Merge transaction does not contains Contact to update");
			}
			
			contactDuplicateRec = processSingleStub(processor.getContactDuplicateStub(), group);
			
			List<GenericReplicationStub> studentsToUpdate = processor.getStubBy(Student.class.getSimpleName(), false);
			if (studentsToUpdate.size() == 1) {
				studentToUpdateRec = processSingleStub(studentsToUpdate.get(0), group);
			} else if (studentsToUpdate.size() > 1) {
				throw new IllegalStateException("Merge transaction  contains more than one Student to update");
			}

			List<GenericReplicationStub> tutorsToUpdate = processor.getStubBy(Tutor.class.getSimpleName(), false);
			if (tutorsToUpdate.size() == 1) {
				tutorToUpdateRec = processSingleStub(tutorsToUpdate.get(0), group);
			} else if (tutorsToUpdate.size() > 1) {
				throw new IllegalStateException("Merge transaction  contains more than one Tutor to update");
			}

			atomicContext.commitChanges();
			fillWillowIds();

			processor.processMerge(contactDuplicateRec, studentToUpdateRec, tutorToUpdateRec);

			try {
				processRegularTransaction(group);
			} catch (Exception e) {
				logger.error("Merge operation was completed on willow side. Unexpected error occurred during process merge stubs by regular mechanism.", e);
				throw e;
			}
		} finally {
			//leave contactDuplicate for angel response only
			result.clear();
			if (contactDuplicateRec != null) {
				result.add(contactDuplicateRec);
			}
		}
	}
	
	private GenericReplicatedRecord processSingleStub(GenericReplicationStub stub, GenericTransactionGroup group) {
		GenericReplicatedRecord replicationRec = toReplicatedRecord(stub, false);
		result.add(replicationRec);
		group.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(stub);
		processStub(stub);
		return replicationRec;
	}

    private void fillWillowIds() {
        for (GenericReplicatedRecord r : result) {
            String willowIdentifier = EntityMapping.getWillowEntityIdentifer(r.getStub().getEntityIdentifier());

            if (QueuedStatistic.class.getSimpleName().equals(willowIdentifier))
                continue;
            if (willowIdentifier.equals("BinaryData"))
                continue;

            List<Queueable> savedObjects = objectsByAngelId(r.getStub().getAngelId(), willowIdentifier);
            if (!savedObjects.isEmpty()) {
                r.getStub().setWillowId(savedObjects.get(0).getId());
            }
        }
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
			StubUtils.setFailedStatus(record);
			if (record.getMessage() == null) {
				record.setMessage(message);
			}
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

		logger.info("Process stub for {} with angelId: {} and willowId: {}.", currentStub.getEntityIdentifier(),
				currentStub.getAngelId(), currentStub.getWillowId());

		GenericReplicatedRecord replRecord = getReplicatedRecordForStub(currentStub);

		if (replRecord == null) {
			throw new IllegalArgumentException(String.format("Replication result is not set for %s with angelId:%s and willowId:%s.", currentStub.getEntityIdentifier(),
					currentStub.getAngelId(), currentStub.getWillowId()));
		}

		if (currentStub instanceof GenericQueuedStatisticStub) {
            return queuedStatisticProcessor.process((GenericQueuedStatisticStub)currentStub);
		}

        if (currentStub instanceof GenericBinaryDataStub)
        {
            return attachmentProcessor.processBinaryDataStub((GenericBinaryDataStub) currentStub, createRelationShipCallback());
        }

        /**
         *  AttachmentData DeleteStub should be ignored because correspondent file will be deleted with deleting BinaryInfo
         */
        if (currentStub instanceof GenericDeletedStub && currentStub.getEntityIdentifier().equals("AttachmentData"))
        {
            return null;
        }

        String willowIdentifier = EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier());

        if (currentStub.getAngelId() == null) {
            String message = String.format("Empty angelId for object object: %s willowId: %s", willowIdentifier, currentStub.getWillowId());
			StubUtils.setFailedStatus(replRecord);
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
			} else if (currentStub.getWillowId() != null) {
				String message = String.format("Can not find corresponded record %s (willowId:%d, angelId:%d, collegeId:%d) by willowId on willow side.", willowIdentifier, currentStub.getWillowId(), currentStub.getAngelId(), webSiteService.getCurrentCollege().getId() );
				StubUtils.setFailedStatus(replRecord);
				replRecord.setMessage(message);
				throw new IllegalArgumentException(message);
			}
            return createObject(currentStub);
		}
		case 1: {
			Queueable objectToUpdate = objects.get(0);

			if (currentStub.getWillowId() != null && !objectToUpdate.getId().equals(currentStub.getWillowId())) {

                String message = String.format("WillowId doesn't match for object: %s. Expected: %s, but got %s.", willowIdentifier,
						objectToUpdate.getId(), currentStub.getWillowId());
				StubUtils.setFailedStatus(replRecord);
                replRecord.setMessage(message);
                throw new IllegalArgumentException(message);
			}

            if (currentStub instanceof GenericDeletedStub) {
				deleteObject(objectToUpdate);
                if (objectToUpdate instanceof BinaryInfo)
                    attachmentProcessor.deletedBinaryDataBy((BinaryInfo) objectToUpdate);
				return null;
			} else {
				willowUpdater.updateEntityFromStub(currentStub, objectToUpdate, new RelationShipCallbackImpl());
				return objectToUpdate;
			}
        }
		default:
			// FAILURE angelId unique
            String message = String.format("%s objects found for angelId:%s with entity %s", objects.size(), currentStub.getAngelId(), currentStub.getEntityIdentifier());
			StubUtils.setFailedStatus(replRecord);
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
	Queueable createObject(GenericReplicationStub currentStub) {

		try {
			Queueable objectToUpdate = atomicContext.newObject(getEntityClass(atomicContext,
					EntityMapping.getWillowEntityIdentifer(currentStub.getEntityIdentifier())));
			College currentCollege = atomicContext.localObject(webSiteService.getCurrentCollege());
			objectToUpdate.setCollege(currentCollege);
			willowUpdater.setCayenneService(cayenneService);
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
		return (List<Queueable>) ObjectSelect.query(getEntityClass(atomicContext, entityName))
				.where(ExpressionFactory.matchDbExp("angelId", angelId))
				.and(ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()))
				.select(atomicContext);
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
			records = (List<Queueable>) ObjectSelect.query(getEntityClass(atomicContext, entityName))
					.where(ExpressionFactory.matchDbExp("id", willowId))
					.and(ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()))
					.select(atomicContext);
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

		for (GenericReplicationStub s : new ArrayList<>(transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo())) {
			if (angelId.equals(s.getAngelId()) && s.getEntityIdentifier().equals(angelIdentifier)) {
				transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(s);
				return s;
			}
		}
		return null;
	}

    RelationShipCallback createRelationShipCallback()
    {
        return new RelationShipCallbackImpl();
    }

	/**
	 * Callback for updating relationships.
	 *
	 * @author anton
	 */
	class RelationShipCallbackImpl implements RelationShipCallback {

		/**
		 * @see ish.oncourse.webservices.replication.updaters.RelationShipCallback#updateRelationShip(java.lang.Long,java.lang.Class)
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
			College college = webSiteService.getCurrentCollege();
			String message = String.format("collegeId: %s, Uncommitted object with angelId:%s and entityName:%s wasn't found.", college.getId(), angelId, entityName);
			throw new IllegalArgumentException(message);
		}
	}
}
