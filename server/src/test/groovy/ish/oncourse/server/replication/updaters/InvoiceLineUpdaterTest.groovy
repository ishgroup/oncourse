package ish.oncourse.server.replication.updaters

import ish.CayenneIshTestCase
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.replication.handler.OutboundReplicationHandlerTest
import ish.oncourse.server.replication.services.TransactionGroupProcessorImpl
import ish.oncourse.webservices.v22.stubs.replication.InvoiceLineStub
import ish.oncourse.webservices.v22.stubs.replication.TransactionGroup
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test

class InvoiceLineUpdaterTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    @Before
    void setup() throws Exception {
        wipeTables()
        InputStream st = OutboundReplicationHandlerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/replication/updaters/InvoiceLineUpdaterTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)
        this.cayenneService = injector.getInstance(ICayenneService.class)
        super.setup()
    }

    /**
     * The test has been added for task #15147
     */
    @Test
    void testPrepaidFeesRemaining()
    {
        ObjectContext objectContext = cayenneService.getNewContext()
        InvoiceLineStub invoiceLineStub = new InvoiceLineStub()
        invoiceLineStub.setDescription("Description")
        invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO)
        invoiceLineStub.setPriceEachExTax(new BigDecimal(100))
        invoiceLineStub.setQuantity(new BigDecimal(1))
        invoiceLineStub.setTaxEach(BigDecimal.ZERO)
        invoiceLineStub.setTitle("Title")
        invoiceLineStub.setUnit("Unit")
        invoiceLineStub.setCreated(Calendar.getInstance().getTime())
        //invoiceLineStub.setSortOrder(0);

        testInvoiceLineWithoutEnrolment(objectContext, invoiceLineStub)

        invoiceLineStub.setEnrolmentId(1L)
        testInvoiceLineWithEnrolment(objectContext, invoiceLineStub)
    }

    private void testInvoiceLineWithoutEnrolment(ObjectContext objectContext, InvoiceLineStub invoiceLineStub) {
        InvoiceLineUpdater invoiceLineUpdater = new InvoiceLineUpdater()
        InvoiceLine invoiceLine = objectContext.newObject(InvoiceLine.class)
        //default value for tax is set in InvoiceLineLifecycleListener.postAdd
        invoiceLine.setTax(new Tax())

        assertEquals("Test getFinalPriceToPayExTax is  ZERO", Money.ZERO, invoiceLine.getFinalPriceToPayExTax())

        invoiceLineUpdater.updateEntity(invoiceLineStub, invoiceLine, new RelationShipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                return null
            }
        })

        assertNotSame("Test getFinalPriceToPayExTax is not ZERO", Money.ZERO, invoiceLine.getFinalPriceToPayExTax())
        assertEquals("Test getFinalPriceToPayExTax",invoiceLine.getPriceTotalExTax(),invoiceLine.getFinalPriceToPayExTax())
        //We should set prepaidFeesRemaining only if Invoice is related to Enrolment
        assertEquals("Test getPrepaidFeesRemaining is ZERO",Money.ZERO,invoiceLine.getPrepaidFeesRemaining())
    }

    private void testInvoiceLineWithEnrolment(ObjectContext objectContext, InvoiceLineStub invoiceLineStub) {
        InvoiceLineUpdater invoiceLineUpdater = new InvoiceLineUpdater()
        InvoiceLine invoiceLine = objectContext.newObject(InvoiceLine.class)
        //default value for tax is set in InvoiceLineLifecycleListener.postAdd
        invoiceLine.setTax(new Tax())

        assertEquals("Test getFinalPriceToPayExTax is  ZERO", Money.ZERO, invoiceLine.getFinalPriceToPayExTax())

        invoiceLineUpdater.updateEntity(invoiceLineStub, invoiceLine, new RelationShipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                return null
            }
        })

        assertNotSame("Test getFinalPriceToPayExTax is not ZERO", Money.ZERO, invoiceLine.getFinalPriceToPayExTax())
        assertEquals("Test getFinalPriceToPayExTax",invoiceLine.getPriceTotalExTax(),invoiceLine.getFinalPriceToPayExTax())
        //We should set prepaidFeesRemaining only if Invoice is related to Enrolment
        assertEquals("Test getPrepaidFeesRemaining is ZERO", invoiceLine.getPriceTotalExTax(),invoiceLine.getPrepaidFeesRemaining())
    }

    @Test
    void testInvoiceLineWithClass() {

        InvoiceLineStub invoiceLineStub = new InvoiceLineStub()
        invoiceLineStub.setEntityIdentifier("InvoiceLine")
        invoiceLineStub.setCreated(new Date())
        invoiceLineStub.setModified(new Date())
        invoiceLineStub.setAngelId(2L)
        invoiceLineStub.setWillowId(2L)
        invoiceLineStub.setDescription("Description")
        invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO)
        invoiceLineStub.setPriceEachExTax(new BigDecimal(10))
        invoiceLineStub.setQuantity(new BigDecimal(1))
        invoiceLineStub.setTaxEach(BigDecimal.ZERO)
        invoiceLineStub.setTitle("Title")
        invoiceLineStub.setUnit("Unit")
        invoiceLineStub.setCreated(Calendar.getInstance().getTime())
        invoiceLineStub.setCourseClassId(1L)
        invoiceLineStub.setInvoiceId(2L)

        TransactionGroup transactionGroup = new TransactionGroup()
        transactionGroup.getReplicationStub().add(invoiceLineStub)

        TransactionGroupProcessorImpl transactionGroupProcessor = new TransactionGroupProcessorImpl(cayenneService, new AngelUpdaterImpl())
        transactionGroupProcessor.processGroup(transactionGroup)

        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(cayenneService.getNewContext())

        assertEquals("Transactions must not been created", 0, transactions.size())

        InvoiceLine invoiceLine = SelectById.query(InvoiceLine.class, 2L).selectOne(cayenneService.getNewContext())
        assertEquals("PrepaidFeesRemaining should be \$10.00", new Money("10.00"), invoiceLine.getPrepaidFeesRemaining())

    }

}
