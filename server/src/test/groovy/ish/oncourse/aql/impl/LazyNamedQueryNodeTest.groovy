package ish.oncourse.aql.impl

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.DatabaseUnitException
import org.dbunit.database.QueryDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.mysql.MySqlConnection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.sql.Connection
import java.sql.SQLException

@CompileStatic
class LazyNamedQueryNodeTest extends CayenneIshTestCase {

    private DataContext cayenneContext
    private AqlService aqlService

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/aql/NamedQueryTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)
        cayenneContext = injector.getInstance(ICayenneService.class).getNewReadonlyContext()
        aqlService = new AntlrAqlService()
    }

    private static Expression exp(CompilationResult res) {
        return res.getCayenneExpression().orElseThrow { new RuntimeException(res.getErrors().toString()) }
    }

    private static void assertResult(CompilationResult res) {
        Assertions.assertTrue(res.getCayenneExpression().isPresent())
        Assertions.assertTrue(res.getErrors().isEmpty())
    }

    /**
     * Export partial data set from real db
     * @throws DatabaseUnitException* @throws SQLException* @throws IOException
     */
    void exportPartialDataSet() throws DatabaseUnitException, SQLException, IOException {
        Connection connection = ServerRuntime.builder().addConfig("cayenne/cayenne-Angel.xml").build().getDataSource().getConnection()
        QueryDataSet partialDataSet = new QueryDataSet(new MySqlConnection(connection, "angel_onroad"))
        partialDataSet.addTable("Banking", "SELECT * FROM Banking WHERE id=466")
        partialDataSet.addTable("AccountTransaction", "SELECT * FROM AccountTransaction WHERE foreignRecordId IN (7332, 7348) AND tableName = 'P'")
        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial1.xml"))
    }

    @Test
    void testBankingAttributeIn() {
        //keep here for local test with real db
        //ServerRuntime runtime = ServerRuntime.builder().addConfig("cayenne/cayenne-Angel.xml").build();
        //ObjectContext cayenneContext = runtime.newContext();
        //aqlService = new AntlrAqlService();

        CompilationResult result = aqlService.compile("banking[465,466]", AccountTransaction.class, cayenneContext)
        assertResult(result)
        List<?> select = ObjectSelect.query(AccountTransaction.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(4, select.size())
    }

    @Test
    void testBankingAttributeSubAttribute() {
        CompilationResult result = aqlService.compile("banking.type in (MANUAL, GATEWAY)", AccountTransaction.class, cayenneContext)
        assertResult(result)
        List<?> select = ObjectSelect.query(AccountTransaction.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(4, select.size())
    }

    @Test
    void testBankingAttributeParentPath() {
        CompilationResult result = aqlService.compile("transactions.banking.type in (AUTO_MCVISA, AUTO_AMEX)", Account.class, cayenneContext)
        assertResult(result)
        List<?> select = ObjectSelect.query(Account.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(0, select.size())
    }

    @Test
    void testSessionTutorPostfixPath() {
        CompilationResult result = aqlService.compile("tutor.contact.firstName == 'John'", Session.class, cayenneContext)
        assertResult(result)
        List<?> select = ObjectSelect.query(Session.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(0, select.size())
    }

    @Test
    void testSessionTutorPrefixPath() {
        CompilationResult result = aqlService.compile("session.tutor.contact.firstName == 'John'", Attendance.class, cayenneContext)
        assertResult(result)
        List<?> select = ObjectSelect.query(Attendance.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(0, select.size())
    }


    @Test
    void testTeggableSyntax() {
        CompilationResult result = aqlService.compile("#Art1", CourseClass.class, cayenneContext)
        assertResult(result)
        List<?> select = ObjectSelect.query(CourseClass.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(2, select.size())

        result = aqlService.compile("#Art2", CourseClass.class, cayenneContext)
        assertResult(result)
        select = ObjectSelect.query(CourseClass.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(0, select.size())


        result = aqlService.compile("#Art13", CourseClass.class, cayenneContext)
        assertResult(result)
        select = ObjectSelect.query(CourseClass.class).where(exp(result)).select(cayenneContext)
        Assertions.assertEquals(1, select.size())
    }

    @Test
    void testBetweenExp() {
        CompilationResult result = aqlService
                .compile("dateDue in 27/03/2012 .. 29/03/2012", Invoice.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Invoice> invoices = ObjectSelect.query(Invoice)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        Assertions.assertEquals(1, invoices.size())
        Assertions.assertEquals(1, invoices.get(0).id)
    }

}
