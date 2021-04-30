package ish.oncourse.aql.impl

import ish.CayenneIshTestCase
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class SyntheticPathTest extends CayenneIshTestCase {

    private DataContext cayenneContext
    private AqlService aqlService

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/aql/SyntheticPathTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)
        cayenneContext = injector.getInstance(ICayenneService.class).getNewReadonlyContext()
        aqlService = new AntlrAqlService()
    }

    @Test
    void testClassEnrolmentCountPath() {

        CompilationResult result = aqlService.compile("courseClass.enrolmentCount == 0", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        List<?> select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(1, select.size())

        result = aqlService.compile("courseClass.enrolmentCount > 0", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(2, select.size())

        result = aqlService.compile("courseClass.enrolmentCount >= 0", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(3, select.size())

        result = aqlService.compile("courseClass.enrolmentCount == 1", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(1, select.size())

        result = aqlService.compile("courseClass.enrolmentCount == 2", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(1, select.size())

    }

    @Test
    void testClassIsMaximumEnrolmentsTest() {

        CompilationResult result = aqlService.compile("courseClass.isMaxEnrolments is true", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        List<?> select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(1, select.size())


        result = aqlService.compile("courseClass.isMaxEnrolments is false", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(2, select.size())

        result = aqlService.compile("courseClass.isMinEnrolments is true", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(2, select.size())


        result = aqlService.compile("courseClass.isMinEnrolments is false", Session.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(1, select.size())

    }

    @Test
    void testAccountTransaction() {
        CompilationResult result = aqlService.compile("accountTransactions.id in (1,2)", PaymentIn.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
        List<?> select = ObjectSelect.query(PaymentIn.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assert.assertEquals(2, select.size())
    }

    @Test
    void testRedeemableBy() {
        testProductItemSyntheticAttr("redeemableBy.id = 1")

        testProductItemSyntheticAttr("redeemableBy.firstName = fname1")
    }

    @Test
    void testPurchasedBy() {
        testProductItemSyntheticAttr("purchasedBy.id = 1")

        testProductItemSyntheticAttr("purchasedBy.firstName = fname1")
    }

    private void testProductItemSyntheticAttr(String exp) {
        CompilationResult result = aqlService
                .compile(exp, ProductItem.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<ProductItem> productItemList = ObjectSelect.query(ProductItem)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        assertEquals(1, productItemList.size())
        assertEquals(1, productItemList.get(0).id)
    }

    @Test
    void testInvoiceCourseClasses() {
        CompilationResult result = aqlService
                .compile("courseClasses.id = 1", Invoice.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<Invoice> invoices = ObjectSelect.query(Invoice)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        assertEquals(1, invoices.size())
        assertEquals(1, invoices.get(0).id)
    }

    @Test
    void testTutorCourseClassAttribute() {
        CompilationResult result = aqlService
                .compile("tutorCourseClass.isCancelled != true",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testStudentCourseClassAttribute() {
        CompilationResult result = aqlService
                .compile("studentCourseClass.isCancelled != true",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testStudentEnrolmentsAttribute() {
        CompilationResult result = aqlService
                .compile("studentEnrolments.status == SUCCESS",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testSyntheticAttributeWithDate() {
        CompilationResult result = aqlService
                .compile("studentCourseClass.endDateTime >= yesterday",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testAccountTransactionContact() {
        CompilationResult result = aqlService
                .compile("contact.id = 1", AccountTransaction.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<AccountTransaction> accountTransactionList = ObjectSelect.query(AccountTransaction)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)

        assertEquals(1, accountTransactionList.size())
    }

    @Test
    void testAllRelatedContacts() {
        CompilationResult result = aqlService
                .compile("allRelatedContacts.id = 2", Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)

        assertEquals(2, contacts.size())
    }
}
