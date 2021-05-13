package ish.oncourse.aql.impl

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(readOnly = true, value = "ish/oncourse/aql/SyntheticPathTestDataSet.xml")
class SyntheticPathTest extends TestWithDatabase {

    private AqlService aqlService

    @BeforeEach
    void setup() throws Exception {
        aqlService = new AntlrAqlService()
    }

    @Test
    void testClassEnrolmentCountPath() {

        CompilationResult result = aqlService.compile("courseClass.enrolmentCount == 0", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        List<?> select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(1, select.size())

        result = aqlService.compile("courseClass.enrolmentCount > 0", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(2, select.size())

        result = aqlService.compile("courseClass.enrolmentCount >= 0", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(3, select.size())

        result = aqlService.compile("courseClass.enrolmentCount == 1", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(1, select.size())

        result = aqlService.compile("courseClass.enrolmentCount == 2", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(1, select.size())

    }

    @Test
    void testClassIsMaximumEnrolmentsTest() {

        CompilationResult result = aqlService.compile("courseClass.isMaxEnrolments is true", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        List<?> select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(1, select.size())


        result = aqlService.compile("courseClass.isMaxEnrolments is false", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(2, select.size())

        result = aqlService.compile("courseClass.isMinEnrolments is true", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(2, select.size())


        result = aqlService.compile("courseClass.isMinEnrolments is false", Session.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        select = ObjectSelect.query(Session.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(1, select.size())

    }

    @Test
    void testAccountTransaction() {
        CompilationResult result = aqlService.compile("accountTransactions.id in (1,2)", PaymentIn.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
        List<?> select = ObjectSelect.query(PaymentIn.class).where(result.getCayenneExpression().get()).select(cayenneContext)
        Assertions.assertEquals(2, select.size())
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
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<ProductItem> productItemList = ObjectSelect.query(ProductItem)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        Assertions.assertEquals(1, productItemList.size())
        Assertions.assertEquals(1, productItemList.get(0).id)
    }

    @Test
    void testInvoiceCourseClasses() {
        CompilationResult result = aqlService
                .compile("courseClasses.id = 1", Invoice.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Invoice> invoices = ObjectSelect.query(Invoice)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        Assertions.assertEquals(1, invoices.size())
        Assertions.assertEquals(1, invoices.get(0).id)
    }

    @Test
    void testTutorCourseClassAttribute() {
        CompilationResult result = aqlService
                .compile("tutorCourseClass.isCancelled != true",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testStudentCourseClassAttribute() {
        CompilationResult result = aqlService
                .compile("studentCourseClass.isCancelled != true",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testStudentEnrolmentsAttribute() {
        CompilationResult result = aqlService
                .compile("studentEnrolments.status == SUCCESS",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testSyntheticAttributeWithDate() {
        CompilationResult result = aqlService
                .compile("studentCourseClass.endDateTime >= yesterday",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())
    }

    @Test
    void testAccountTransactionContact() {
        CompilationResult result = aqlService
                .compile("contact.id = 1", AccountTransaction.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<AccountTransaction> accountTransactionList = ObjectSelect.query(AccountTransaction)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)

        Assertions.assertEquals(1, accountTransactionList.size())
    }

    @Test
    void testAllRelatedContacts() {
        CompilationResult result = aqlService
                .compile("allRelatedContacts.id = 2", Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)

        Assertions.assertEquals(2, contacts.size())
    }
}
