/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.services

import com.google.inject.Inject
import ish.common.types.EntityMapping
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Queueable
import static ish.oncourse.server.replication.services.ReplicationUtils.getEntityClass
import static ish.oncourse.server.replication.services.ReplicationUtils.toCollection
import static ish.oncourse.server.replication.services.ReplicationUtils.toReplicatedRecord
import ish.oncourse.server.replication.updaters.IAngelUpdater
import ish.oncourse.server.replication.updaters.RelationShipCallback
import ish.oncourse.webservices.ITransactionGroupProcessor
import ish.oncourse.webservices.util.GenericDeletedStub
import ish.oncourse.webservices.util.GenericReplicatedRecord
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.util.GenericTransactionGroup
import ish.oncourse.webservices.util.StubUtils
import org.apache.cayenne.CayenneRuntimeException
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.map.DeleteRule
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.reflect.ArcProperty
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class TransactionGroupProcessorImpl implements ITransactionGroupProcessor {

    static final String WILLOW_ID_COLUMN = "willowId"
    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger()

    /**
     * AngelUpdater
     */
    private final IAngelUpdater angelUpdater

    /**
     * Single context for atomic groups.
     */
    private ObjectContext atomicContext

    /**
     * Result
     */
    private List<GenericReplicatedRecord> result

    /**
     * TransactionGroup
     */
    private GenericTransactionGroup transactionGroup

    @Inject
    TransactionGroupProcessorImpl(ICayenneService cayenneService, IAngelUpdater angelUpdater) {
        super()
        this.angelUpdater = angelUpdater
        this.atomicContext = cayenneService.getNewNonReplicatingContext()
    }

    /**
     * @see ITransactionGroupProcessor#processGroup(GenericTransactionGroup)
     */
    @Override
    List<GenericReplicatedRecord> processGroup(GenericTransactionGroup group) {

        this.transactionGroup = group
        this.result = new ArrayList<>()

        for (def currentStub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
            def replRecord = toReplicatedRecord(currentStub)
            result.add(replRecord)
        }

        try {

            while (!group.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
                def currentStub = group.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(0)
                processStub(currentStub)
            }

            atomicContext.commitChanges()

            for (def r : result) {
                def angelIdentifier = EntityMapping.getAngelEntityIdentifer(r.getStub().getEntityIdentifier())
                def savedObject = objectsByWillowId(r.getStub().getWillowId(), angelIdentifier)
                if (savedObject != null) {
                    r.getStub().setAngelId(savedObject.getId())
                }
            }

        } catch (Exception e) {
            logger.error("Failed to atomically process transaction group.", e)
            atomicContext.rollbackChanges()
            def errorMessage = StringUtils.abbreviate(e.getMessage(), 1000)
            updateReplicationStatus(result, errorMessage)
        }
        return result
    }

    /**
     * Updates replication status for the list of replicated records
     *
     * @param records replicated records
     * @param message status message
     */
    private static void updateReplicationStatus(List<GenericReplicatedRecord> records, String message) {
        for (def record : records) {
            StubUtils.setFailedStatus(record)
            if (record.getMessage() == null) {
                record.setMessage(message)
            }
        }
    }

    /**
     * Takes replicatedRecord object which corresponds to passed soap stub from prefilled replication result.
     *
     * @param soapStub - soapStub
     * @return replicated record
     */
    private GenericReplicatedRecord getReplicatedRecordForStub(GenericReplicationStub soapStub) {
        for (def r : this.result) {
            if (r.getStub().getEntityIdentifier().equalsIgnoreCase(soapStub.getEntityIdentifier())) {
                if ((r.getStub().getWillowId() != null && r.getStub().getWillowId().equals(soapStub.getWillowId())) ||
                        (r.getStub().getAngelId() != null && r.getStub().getAngelId().equals(soapStub.getAngelId()))) {
                    return r
                }

            }
        }
        return null
    }

    /**
     * Process on one single stub either Full or Delete. Creates correspondent Cayenne objects with relationships if any, or delete cayenne objects taking
     * entity relations into account.
     *
     * @param currentStub replication stub.
     * @return cayenne object which was updated/deleted.
     */
    private Queueable processStub(GenericReplicationStub currentStub) {

        logger.info("Process stub for {} with angelId:{} and willowId:{}.", currentStub.getEntityIdentifier(), currentStub.getAngelId(),
                currentStub.getWillowId())

        def replRecord = getReplicatedRecordForStub(currentStub)

        if (replRecord == null) {
            throw new IllegalArgumentException(String.format("Replication result is not set for %s with angelId:%s and willowId:%s.",
                    currentStub.getEntityIdentifier(), currentStub.getAngelId(), currentStub.getWillowId()))
        }

        def angelIdentifier = EntityMapping.getAngelEntityIdentifer(currentStub.getEntityIdentifier())

        if (currentStub.getWillowId() == null) {
            StubUtils.setFailedStatus(replRecord)
            replRecord.setMessage(String.format("Empty willowId for object: %s.", angelIdentifier))
            return null
        }

        try {
            def object = objectsByWillowId(currentStub.getWillowId(), angelIdentifier)

            if (object == null && currentStub.getAngelId() != null) {
                // we need this for payment related records
                object = objectsByAngelId(currentStub.getAngelId(), angelIdentifier)
                if (object == null && !(currentStub instanceof GenericDeletedStub)) {
                    def message = String.format("Can not find corresponded record %s (willowId:%d, angelId:%d) by angelId on angel side.", angelIdentifier, currentStub.getWillowId(), currentStub.getAngelId())
                    StubUtils.setFailedStatus(replRecord)
                    replRecord.setMessage(message)
                    throw new IllegalArgumentException(message)
                }
            }

            if (object == null) {
                if (currentStub instanceof GenericDeletedStub) {
                    // ignore object was already deleted
                    return null
                }
                // creating new object
                return createObject(currentStub)
            } else {
                if (currentStub.getAngelId() != null && !object.getId().equals(currentStub.getAngelId())) {
                    StubUtils.setFailedStatus(replRecord)
                    replRecord.setMessage(String.format("AngelId doesnt match for object: %s. Expected: %s, but got %s.", angelIdentifier, object.getId(),
                            currentStub.getAngelId()))
                    return null
                }

                if (currentStub instanceof GenericDeletedStub) {
                    deleteObjects(object)
                    return null
                }

                angelUpdater.updateEntityFromStub(currentStub, object, new ish.oncourse.server.replication.services.TransactionGroupProcessorImpl.RelationShipCallbackImpl())
                return object
            }

        } catch (CayenneRuntimeException ex) {
            // FAILURE angelId uniques
            StubUtils.setFailedStatus(replRecord)
            replRecord.setMessage(ex.getMessage())
            return null
        }
    }

    /**
     * Deletes replicable object
     *
     * @param objectToDelete object to delete
     */
    private void deleteObjects(Queueable objectToDelete) {

        def descriptor = atomicContext.getEntityResolver().getClassDescriptor(objectToDelete.getObjectId().getEntityName())

        for (def relationship : descriptor.getEntity().getRelationships()) {

            if (relationship.getDeleteRule() == DeleteRule.DENY) {

                def property = (ArcProperty) descriptor.getProperty(relationship.getName())

                def relatedObjects = toCollection(property.readProperty(objectToDelete))

                for (def r : relatedObjects) {
                    def entityIdentifier = r.getObjectId().getEntityName()
                    def relStub = takeStubFromGroupByWillowId(r.getWillowId(), entityIdentifier)
                    if (relStub != null && relStub instanceof GenericDeletedStub) {
                        processStub(relStub)
                    }
                }
            }
        }

        atomicContext.deleteObjects(objectToDelete)
    }

    /**
     * Creates replicable object
     *
     * @param currentStub soap stub
     * @return queueable object
     */
    private Queueable createObject(GenericReplicationStub currentStub) {
        def objectToUpdate = atomicContext.newObject(getEntityClass(atomicContext,
                EntityMapping.getAngelEntityIdentifer(currentStub.getEntityIdentifier())))
        angelUpdater.updateEntityFromStub(currentStub, objectToUpdate, new ish.oncourse.server.replication.services.TransactionGroupProcessorImpl.RelationShipCallbackImpl())
        return objectToUpdate
    }

    /**
     * Finds entity by willow id.
     *
     * @param entityId - entityId
     * @param entityIdentifier - entityIdentifier
     * @return list of matched queueble entities
     */
    private Queueable objectsByWillowId(Long entityId, String entityIdentifier) {
        return ObjectSelect.query(getEntityClass(atomicContext, entityIdentifier))
                .where(ExpressionFactory.matchExp(WILLOW_ID_COLUMN, entityId))
                .selectOne(atomicContext)

    }

    /**
     * Finds entity by angel id.
     *
     * @param entityId  - entityId
     * @param entityIdentifier - entityIdentifier
     * @return list of matched queueble entities
     */
    private Queueable objectsByAngelId(Long entityId, String entityIdentifier) {
        return ObjectSelect.query(getEntityClass(atomicContext, entityIdentifier))
                .where(ExpressionFactory.matchExp("id", entityId))
                .selectOne(atomicContext)
    }

    /**
     * Takes stub from transaction group. Stub is removed when it found.
     *
     * @param willowId - willowId
     * @param entityName - entityName
     * @return stub by entity name and willowid
     */
    private GenericReplicationStub takeStubFromGroupByWillowId(Long willowId, String entityName) {
        if (willowId == null) {
            return null
        }

        def willowIdentifier = EntityMapping.getWillowEntityIdentifer(entityName)

        for (def s : transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
            if (willowId.equals(s.getWillowId()) && s.getEntityIdentifier().equals(willowIdentifier)) {
                transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().remove(s)
                return s
            }
        }
        return null
    }

    /**
     * Callback for updating relationships.
     *
     */
    class RelationShipCallbackImpl implements RelationShipCallback {

        /**
         * @see RelationShipCallback#updateRelationShip(Long, Class)
         */
        @Override
        @SuppressWarnings("unchecked")
        <M extends Queueable> M updateRelationShip(Long willowId, Class<M> clazz) {

            if (willowId == null) {
                return null
            }

            def entityIdentifier = clazz.getSimpleName()
            def object = objectsByWillowId(willowId, entityIdentifier)

            if (object != null) {
                return (M) object
            }

            def stub = takeStubFromGroupByWillowId(willowId, entityIdentifier)
            if (stub != null) {
                return (M) processStub(stub)
            }

            return uncommittedObjectByWillowId(willowId, clazz)
        }

        @SuppressWarnings("unchecked")
        private <M extends Queueable> M uncommittedObjectByWillowId(Long willowId, Class<M> clazz) {

            def entityName = clazz.getSimpleName()

            for (Object obj : atomicContext.uncommittedObjects()) {
                if (obj instanceof Queueable) {
                    def entity = (Queueable) obj
                    if (willowId.equals(entity.getWillowId()) && entity.getObjectId().getEntityName().equals(entityName)) {
                        return (M) entity
                    }
                }
            }

            return null
        }
    }
}
