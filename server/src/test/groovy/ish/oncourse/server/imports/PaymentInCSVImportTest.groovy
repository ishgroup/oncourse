package ish.oncourse.server.imports


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.SessionTest
import ish.oncourse.server.db.SanityCheckService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class PaymentInCSVImportTest extends CayenneIshTestCase {


    @BeforeEach
    void setup() throws Exception {
        wipeTables()

        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/imports/paymentInCSVImportTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        executeDatabaseOperation(rDataSet)


        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)
        dataPopulation.run()

        SanityCheckService sanityCheckService = injector.getInstance(SanityCheckService.class)
        sanityCheckService.performCheck()
    }


    @Test
    void test() throws IOException {
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewContext()

        Assertions.assertEquals(0, ObjectSelect.query(PaymentIn.class).select(context).size())
        Assertions.assertEquals(0, ObjectSelect.query(PaymentInLine.class).select(context).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())


        ImportService importService = injector.getInstance(ImportService.class)

        ImportParameter parameter = new ImportParameter()
        parameter.setKeyCode("ish.onCourse.import.paymentIn.csv")

        Map<String, byte[]> data = new HashMap<>()
        data.put("importFile", IOUtils.toByteArray(ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/PaymentInCSVImportTest.csv")))

        parameter.setData(data)

        importService.performImport(parameter)


        Assertions.assertEquals(185, ObjectSelect.query(PaymentIn.class).select(context).size())
        Assertions.assertEquals(187, ObjectSelect.query(PaymentInLine.class).select(context).size())
        Assertions.assertEquals(16, ObjectSelect.query(Banking.class).select(context).size())
    }

    private static final String[] EXPECTED_HEADERS = [
            "payment.invoice",
            "payment.dateBanked",
            "payment.amount",
            "paymentIn.paymentMethod.name",
            "payment.chequeBank",
            "payment.chequeBranch",
            "payment.chequeDrawer"
    ]

    //the attached example contains '\uFEFF' which blocks right

    @Test
    void testHeaderValues() throws IOException {
        InputStream inputStream = ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/PaymentInCSVImportTest.csv")
        byte[] byteArray = IOUtils.toByteArray(inputStream)
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray)
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream)
        CsvParser csvParser = new CsvParser(inputStreamReader)

        String[] header = (String[]) csvParser.getHeader()

        Assertions.assertEquals(7, header.length)

        for (int i = 0; i < 7; i++) {
            Assertions.assertEquals(EXPECTED_HEADERS[i], header[i])
        }
    }
}
