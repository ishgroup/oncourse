/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.cayenne

import ish.oncourse.commercial.replication.cayenne.glue._QueuedRecord
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tutor
import org.apache.cayenne.Cayenne
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectIdQuery
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 */
class QueuedRecord extends _QueuedRecord implements Comparable<QueuedRecord> {


    private static final Logger logger = LogManager.getLogger()

    /**
     * Maximum retry number.
     */
    public static final Integer MAX_NUMBER_OF_RETRY = 3

    // TODO this should become an enumeration for tableName
    public static final List<String> EntityOrder = Arrays.asList(Tutor.ENTITY_NAME,
            Site.ENTITY_NAME,
            Room.ENTITY_NAME,
            Course.ENTITY_NAME,
            CourseClass.ENTITY_NAME,
            Session.ENTITY_NAME,
            Student.ENTITY_NAME,
            Message.ENTITY_NAME,
            MessagePerson.ENTITY_NAME,
            Enrolment.ENTITY_NAME,
            Outcome.ENTITY_NAME,
            Certificate.ENTITY_NAME,
            CertificateOutcome.ENTITY_NAME)

    @Override
    void postAdd() {
        super.postAdd()
        logger.debug("onEntityCreation")
        if (getNumberOfAttempts() == null) {
            setNumberOfAttempts(0)
        }
    }

    @Override
    protected void postUpdate() {
        super.postUpdate()
        if (getLinkedRecord() != null) {
            setWillowId(getLinkedRecord().getWillowId())
        }
    }

    @Override
    protected void preUpdate() {
        checkAndTrimErrorMessage()
        super.preUpdate()
    }

    private void checkAndTrimErrorMessage() {
        // need to trim error message to fill it in a database.
        if (getErrorMessage() != null) {
            String errorMessage = getErrorMessage()
            setErrorMessage(StringUtils.abbreviate(errorMessage, 1024))
        }
    }

    @Override
    void setErrorMessage(@Nullable final String errorMessage) {
        super.setErrorMessage(errorMessage != null ? StringUtils.abbreviate(errorMessage, 1024) : null)
    }

    @Override
    protected void prePersist() {
        checkAndTrimErrorMessage()
        super.prePersist()
        if (getLinkedRecord() != null) {
            setWillowId(getLinkedRecord().getWillowId())
        }
    }

    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof QueuedRecord)) {
            return false
        }

        QueuedRecord q2 = (QueuedRecord) obj
        if (getTableName() == null) {
            if (q2.getTableName() != null) {
                return false
            }
        } else if (getTableName() != q2.getTableName()) {
            return false
        }

        if (getForeignRecordId() == null) {
            return q2.getForeignRecordId() == null
        } else return getForeignRecordId() == q2.getForeignRecordId()

    }

    @Override
    int hashCode() {
        String tableName = getTableName()
        Number recordID = getForeignRecordId()
        return ((tableName == null ? "" : tableName) + (recordID == null ? "0" : recordID.toString())).hashCode()
    }

    /**
     * @return
     */
    @Nullable
    Queueable getLinkedRecord() {

        @SuppressWarnings("unchecked")
        Class<? extends Queueable> entityClass = (Class<? extends Queueable>) getObjectContext().getEntityResolver().getObjEntity(getTableName())
                .getJavaClass()

        Expression exp = ExpressionFactory.matchDbExp("id", getForeignRecordId())

        logger.debug("getLinkedRecord: {} {}", entityClass, exp)

        Queueable dirtyObject = ObjectSelect.query(entityClass)
                .where(exp)
                .selectOne(getObjectContext())

        // check for delete, when dirtyObject is null
        if (dirtyObject != null) {
            // force record refetch from the database
            logger.debug("refreshing dirty object: {}", dirtyObject)

            ObjectIdQuery refetchQuery = new ObjectIdQuery(dirtyObject.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH)
            Queueable result = (Queueable) Cayenne.objectForQuery(getObjectContext(), refetchQuery)
            logger.debug("returning refreshed object: {}", result)
            return result
        }
        logger.debug("dirty object not found in the database")
        return null
    }

    @Override
    protected void onPreRemove() {
        if (getQueuedTransaction() != null) {
            getQueuedTransaction().setModifiedOn(new Date())
        }
    }

    int compareTo(@Nonnull QueuedRecord q2) {
        String thisTableName = getTableName()
        String q2TableName = q2.getTableName()
        int index1 = EntityOrder.indexOf(thisTableName)
        int index2 = EntityOrder.indexOf(q2TableName)

        // adjust entity names not found to entityOrder.size.
        if (index1 < 0) {
            index1 = EntityOrder.size()
        }
        if (index2 < 0) {
            index2 = EntityOrder.size()
        }

        // initial test based on ordered entities.
        int comparison = index1 - index2
        if (comparison < 0) {
            return -1
        } else if (comparison > 0) {
            return 1
        }

        // if still equal compare table names
        if (thisTableName == null && q2TableName == null) {
            return Integer.compare(Cayenne.intPKForObject(this), Cayenne.intPKForObject(q2))
        } else if (thisTableName == null) {
            return 1
        } else if (q2TableName == null) {
            return -1
        } else if ((comparison = thisTableName <=> q2TableName) != 0) {
            return comparison
        }

        // if still equal compare soap primary keys in ascending order.
        // If they're equal return 0 to guarantee uniqueness
        if (getForeignRecordId() == q2.getForeignRecordId()) {
            return 0
        }

        // if still equal compare queued record pks.
        // this way we guarantee that newer records replicate first.
        // which is especially important that deleted records replicate as
        // they were deleted.
        Integer pkA = Cayenne.intPKForObject(this)
        Integer pkB = Cayenne.intPKForObject(q2)

        return pkA <=> pkB
    }

    @Nonnull
    @Override
    String toString() {
        StringBuilder buff = new StringBuilder()
        buff.append("<QueuedRecord[")
        if (getObjectContext() == null) {
            buff.append("NO_PK")
        } else {
            buff.append(getPrimaryKeyValue())
        }
        buff.append("] ")
        buff.append(getTableName()).append(' ').append(getForeignRecordId())
        buff.append(" foreignPK[").append(getForeignRecordId()).append("]")
        buff.append(">")
        return buff.toString()
    }

    /**
     * @return
     */
    @Override
    QueuedRecordAction getAction() {
        return super.getAction()
    }

    /**
     * @return the date and time this record was created
     */
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return
     */
    @Override
    String getErrorMessage() {
        return super.getErrorMessage()
    }

    /**
     * @return
     */
    @Nonnull
    @Override
    Long getForeignRecordId() {
        return super.getForeignRecordId()
    }

    /**
     * @return
     */
    @Override
    Date getLastAttemptOn() {
        return super.getLastAttemptOn()
    }

    /**
     * @return the date and time this record was modified
     */
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return
     */
    @Nonnull
    @Override
    Integer getNumberOfAttempts() {
        return super.getNumberOfAttempts()
    }

    /**
     * @return
     */
    @Nonnull
    @Override
    String getTableName() {
        return super.getTableName()
    }

    /**
     * @return
     */
    @Override
    Long getTransactionId() {
        return super.getTransactionId()
    }



    /**
     * @return
     */
    @Nonnull
    @Override
    QueuedTransaction getQueuedTransaction() {
        return super.getQueuedTransaction()
    }

    @Override
    boolean isAuditAllowed() {
        return false
    }
}
