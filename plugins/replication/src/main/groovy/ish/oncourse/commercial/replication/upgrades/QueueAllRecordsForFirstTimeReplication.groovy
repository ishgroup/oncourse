/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.upgrades

import ish.common.types.ProductType
import ish.oncourse.commercial.replication.cayenne.QueuedRecordAction
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.AttachmentRelation
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.server.cayenne.ConcessionType
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactRelation
import ish.oncourse.server.cayenne.ContactRelationType
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassCourseClass
import ish.oncourse.server.cayenne.CorporatePassDiscount
import ish.oncourse.server.cayenne.CorporatePassProduct
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.CourseModule
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.DiscountConcessionType
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.DiscountMembership
import ish.oncourse.server.cayenne.DiscountMembershipRelationType
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.Field
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.FieldConfigurationLink
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.FieldHeading
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.MessageTemplate
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.SessionModule
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.StudentConcession
import ish.oncourse.server.cayenne.Survey
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRelation
import ish.oncourse.server.cayenne.TagRequirement
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.cayenne.VoucherProductCourse
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.cayenne.WaitingListSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class QueueAllRecordsForFirstTimeReplication {

    /**
     * Set of {@link Queueable} classes ordered in the way they should be put into replication queue.
     */
    protected static final Set<RecordDescriptor> ORDERED_QUEUEABLE_CLASSES = new LinkedHashSet<>()
    private static final String TRANSACTION_KEY_PREFIX = "queue_"
    static final String WILLOW_ID_COLUMN = "willowId"
    private static final Logger logger = LogManager.getLogger()

    private static final int BATCH_COUNT = 100

    static {

        // Contact related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Contact.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Student.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Tutor.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(ContactRelationType.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(ContactRelation.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Tax.class))

        // Course/Class related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Site.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Room.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Course.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CourseModule.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CourseClass.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CourseClassTutor.class))

        // Product related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(MembershipProduct.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(VoucherProduct.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Product.class, ExpressionFactory.notInExp(Product.TYPE.getName(), ProductType.MEMBERSHIP,
                ProductType.VOUCHER)))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(VoucherProductCourse.class))

        // Discount/Concession related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Discount.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(DiscountCourseClass.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(DiscountMembership.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(DiscountMembershipRelationType.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(ConcessionType.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(StudentConcession.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(DiscountConcessionType.class))

        // Enrolment related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(PriorLearning.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Enrolment.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Session.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Attendance.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Outcome.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(SessionModule.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(TutorAttendance.class))

        // Message related data
//        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Message.class))
//        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(MessagePerson.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(MessageTemplate.class))

        // SystemUser table
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(SystemUser.class))

        // Certificate related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Certificate.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CertificateOutcome.class))

        // CorporatePass related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CorporatePass.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CorporatePassCourseClass.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CorporatePassProduct.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CorporatePassDiscount.class))

        // Survey table
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Survey.class))

        // Waiting list related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(WaitingList.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(WaitingListSite.class))

        // Attachment related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Document.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(DocumentVersion.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(AttachmentRelation.class))

        // Tag related data
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Tag.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(TagRelation.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(TagRequirement.class))

        // Entity relations
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(EntityRelation.class))

        // Custom fields
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CustomFieldType.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CustomField.class))

        // Preferences
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Preference.class))

        // Applications
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Application.class))

        // Scripts and EmailTemplates
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Script.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(EmailTemplate.class))

        //Payment plans
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(CourseClassPaymentPlanLine.class))

        //Assessment tracking
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Assessment.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(AssessmentClass.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(AssessmentClassTutor.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(AssessmentSubmission.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(AssessmentClassModule.class))

        //Field configurations
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(FieldConfiguration.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(FieldHeading.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(Field.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(FieldConfigurationScheme.class))
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(FieldConfigurationLink.class))
        
        //Entity relation types
        ORDERED_QUEUEABLE_CLASSES.add(new RecordDescriptor(EntityRelationType.class))

    }

    private ISHDataContext context

    QueueAllRecordsForFirstTimeReplication(ICayenneService cayenneService) {
        this.context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
    }

    void runUpgrade() {
        try {
            ORDERED_QUEUEABLE_CLASSES.each { descriptor ->
                def qual = ExpressionFactory.matchExp(WILLOW_ID_COLUMN, null)
                if (descriptor.getQualifier() != null) {
                    qual = qual.andExp(descriptor.getQualifier())
                }
                def query = ObjectSelect.query(descriptor.getRecordClass()).where(qual)
                addQueuedRecords(query)
            }
        } catch (Exception e) {
            logger.error("Fail to queue unreplicated records", e)
            logger.catching(e)
        }

    }

    /**
     * Add {@link Queueable} record to replication queue.
     *
     * @param record
     */
    private void addRecordToReplicationQueue(Queueable record, ObjectContext context, QueuedTransaction t) {

        def result = new QueuedRecord()
        result.setLastAttemptOn(new Date())
        result.setNumberOfAttempts(0)
        result.setTableName(record.getObjectId().getEntityName())
        result.setForeignRecordId(record.getId())
        result.setWillowId(record.getWillowId())
        result.setAction(QueuedRecordAction.UPDATE)

        context.registerNewObject(result)
        result.setQueuedTransaction(t)
    }

    /**
     * The method adds QueuedTransaction/record for every entity which will be got by this SelectQuery
     *
     * @param query
     */
    void addQueuedRecords(ObjectSelect<? extends Queueable> query) {
        query.pageSize(BATCH_COUNT)

        def records = query.select(context)
        def count = 0

        QueuedTransaction t = null

        if (!records.isEmpty()) {
            t = createTransaction(context)
        }

        for (Queueable record : records) {

            if (record.isAsyncReplicationAllowed() && !inQueue(record)) {
                addRecordToReplicationQueue(record, context, t)
                count++
            }

            if (count > BATCH_COUNT) {
                context.commitChanges()
                count = 0
                t = createTransaction(context)
            }
        }

        if (count > 0) {
            context.commitChanges()
        }
    }

    private static boolean inQueue(Queueable record ) {
        return ObjectSelect.query(QueuedRecord)
                .where(QueuedRecord.TABLE_NAME.eq(record.objectId.entityName))
                .and(QueuedRecord.FOREIGN_RECORD_ID.eq(record.id))
                .and(QueuedRecord.NUMBER_OF_ATTEMPTS.lt(3))
                .selectFirst(record.objectContext) != null
    }
    
    QueuedTransaction createTransaction(ISHDataContext ishContext) {
        def transactionKey = TRANSACTION_KEY_PREFIX + ishContext.generateTransactionKey()
        QueuedTransaction t = ishContext.newObject(QueuedTransaction.class)
        def today = new Date()
        t.setCreatedOn(today)
        t.setModifiedOn(today)
        t.setTransactionKey(transactionKey)
        return t
    }

    /**
     * Container class for {@link Queueable} record class info and qualifier by which records of this type should be selected for replication.
     *
     */
    static class RecordDescriptor {

        private Class<? extends Queueable> recordClass
        private Expression qualifier

        RecordDescriptor(Class<? extends Queueable> recordClass, Expression qualifier) {
            this.recordClass = recordClass
            this.qualifier = qualifier
        }

        RecordDescriptor(Class<? extends Queueable> recordClass) {
            this.recordClass = recordClass
        }

        /**
         * @return the recordClass
         */
        Class<? extends Queueable> getRecordClass() {
            return this.recordClass
        }

        /**
         * @return the qualifier
         */
        Expression getQualifier() {
            return this.qualifier
        }

    }
}
