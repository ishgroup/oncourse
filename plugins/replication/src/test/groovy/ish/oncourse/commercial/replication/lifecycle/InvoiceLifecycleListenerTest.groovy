/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.lifecycle

import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.commercial.replication.cayenne.QueuedTransaction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.scripting.GroovyScriptService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.query.SortOrder
import org.apache.commons.lang3.time.DateUtils
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertEquals

class InvoiceLifecycleListenerTest extends CayenneIshTestCase {


    private ICayenneService cayenneService

    @Before
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = InvoiceLifecycleListenerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/lifecycle/invoiceLifecycleTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)

        injector.getInstance(GroovyScriptService.class).initTriggers()

        super.setup()
    }

    @After
    void tearDown() {
        wipeTables()
    }
    /**
     * Test  <code>InvoiceLifecycleListener.createReplicationDataFor</code>
     */
    @Test
    void testCreateReplicationDataFor() throws Exception {
        PreferenceController pref = injector.getInstance(PreferenceController.class)
        pref.setEmailSMTPHost("test.test")
        pref.setEmailFromAddress("test@test.test")
        pref.setReplicationEnabled(true)

        ObjectContext context = cayenneService.getNewContext()
        Account account = SelectById.query(Account.class, 50).selectOne(context)
        Tax tax = SelectById.query(Tax.class, 3).selectOne(context)

        Student student1 = SelectById.query(Student.class, 1).selectOne(context)
        SelectById.query(Student.class, 2).selectOne(context)

        //create new invoice
        Invoice invoice = context.newObject(Invoice.class)
        invoice.setContact(student1.getContact())
        invoice.setDebtorsAccount(account)
        invoice.setSource(PaymentSource.SOURCE_WEB)

        InvoiceLine il1 = context.newObject(InvoiceLine.class)
        il1.setAccount(account)
        il1.setTax(tax)
        il1.setDiscountEachExTax(Money.ZERO)
        il1.setInvoice(invoice)
        il1.setPrepaidFeesAccount(account)
        il1.setPrepaidFeesRemaining(Money.ZERO)
        il1.setPriceEachExTax(Money.ONE)
        il1.setQuantity(BigDecimal.ONE)
        il1.setTaxEach(new Money("0.1"))
        il1.setTitle("il1")

        //commit it in replicating context
        context.commitChanges()

        // give script which runs in separate thread some time to queue emails
        Thread.sleep(5000)

        /**
         * delete all QueuedTransactions to be sure that we test only QueuedTransactions which are created
         * for Invoice which modified in not replicating context
         */
        deleteAllQueuedTransactions()

        context = cayenneService.getNewNonReplicatingContext()
        /**
         * Modify the invoice
         */
        invoice = context.localObject(invoice)
        invoice.setModifiedOn(new Date())
        /**
         * Commit the invoice in not replicating context
         */
        context.commitChanges()

        // give script which runs in separate thread some time to queue emails
        Thread.sleep(5000)

        SelectQuery<QueuedTransaction> query = SelectQuery.query(QueuedTransaction.class)
        query.addOrdering(new Ordering(QueuedTransaction.ID_PROPERTY, SortOrder.ASCENDING))
        List<QueuedTransaction> transactions = context.select(query)
        assertEquals("Only one transaction should be added.",1, transactions.size())
        assertEquals("The trasaction should contain only one queued record.", 1, transactions.get(0).getQueuedRecords().size())
        assertEquals("The queued record should be for Invoice", "Invoice", transactions.get(0).getQueuedRecords().get(0).getTableName())
        assertEquals("The queued record should be for the Invoice", invoice.getId(), transactions.get(0).getQueuedRecords().get(0).getForeignRecordId())

    }

    private void deleteAllQueuedTransactions()
    {
        ObjectContext context = cayenneService.getNewNonReplicatingContext()
        List records = context.select(SelectQuery.query(QueuedRecord.class))
        context.deleteObjects(records)

        records = context.select(SelectQuery.query(QueuedTransaction.class))
        context.deleteObjects(records)

        context.commitChanges()
    }
}
