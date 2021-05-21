/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.upgrades

import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.map.ObjEntity
import org.apache.cayenne.query.SelectQuery
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.BeforeEach

import static org.junit.Assert.*
@DatabaseSetup(value = "ish/oncourse/commercial/replication/upgrades/recordQueuingTestDataSet.xml")
class QueueAllRecordsForFirstTimeReplicationTest extends TestWithDatabase {

	private static final List<Class<? extends Queueable>> EXCLUDED_QUEUEABLE_ENTITIES = new ArrayList<>()

    static {
		EXCLUDED_QUEUEABLE_ENTITIES.add(SaleOrder.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Invoice.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(InvoiceLine.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(PaymentIn.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(PaymentInLine.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(PaymentOut.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(ProductItem.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Membership.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Voucher.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Article.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(ArticleProduct.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(VoucherPaymentIn.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(InvoiceLineDiscount.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(EntityRelationType.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(InvoiceDueDate.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(ContactDuplicate.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Module.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Qualification.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(Message.class)
        EXCLUDED_QUEUEABLE_ENTITIES.add(MessagePerson.class)


    }

	private ICayenneService cayenneService
    

	@Test
    void testAllQueueablesAreProcessed() {
		ObjectContext context = injector.getInstance(ICayenneService.class).getSharedContext()

        Set<Class<? extends Queueable>> processedEntities = new HashSet<>(EXCLUDED_QUEUEABLE_ENTITIES)
        for (QueueAllRecordsForFirstTimeReplication.RecordDescriptor desc : QueueAllRecordsForFirstTimeReplication.ORDERED_QUEUEABLE_CLASSES) {
			processedEntities.add(desc.getRecordClass())
        }

		for (ObjEntity e : context.getChannel().getEntityResolver().getObjEntities()) {
			Class<?> clazz = e.getJavaClass()
            if (clazz.isAnnotationPresent(QueueableEntity.class) && !TagRelation.class.isAssignableFrom(clazz) &&
					!AttachmentRelation.class.isAssignableFrom(clazz) && !CustomField.class.isAssignableFrom(clazz) &&
					!FieldConfiguration.class.isAssignableFrom(clazz)) {
				if (!processedEntities.contains(clazz)) {
					fail(String.format("%s is not processed.", e.getJavaClass()))
                }
			}
		}
	}

	@Test
    void testQueueRecords() throws Exception {
		assertTrue(cayenneService.getSharedContext().select(SelectQuery.query(QueuedRecord.class)).isEmpty())

        QueueAllRecordsForFirstTimeReplication worker = new QueueAllRecordsForFirstTimeReplication(cayenneService)
        worker.runUpgrade()

        List<QueuedRecord> nonPreferenceRecords = cayenneService.getSharedContext().select(
				SelectQuery.query(QueuedRecord.class, QueuedRecord.TABLE_NAME.ne("Preference")))
        assertEquals(32, nonPreferenceRecords.size())

        List<QueuedTransaction> nonPreferenceTransactions = cayenneService.getSharedContext().select(
				SelectQuery.query(QueuedTransaction.class, QueuedTransaction.QUEUED_RECORDS.dot(QueuedRecord.TABLE_NAME).ne("Preference")))
        assertEquals(25, nonPreferenceTransactions.size())
    }

}
