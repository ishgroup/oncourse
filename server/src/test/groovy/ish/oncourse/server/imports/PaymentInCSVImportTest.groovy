package ish.oncourse.server.imports

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.db.SanityCheckService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/imports/paymentInCSVImportTestDataSet.xml")
class PaymentInCSVImportTest extends TestWithDatabase {

    @BeforeEach
    void population() throws Exception {
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)
        dataPopulation.run()

        SanityCheckService sanityCheckService = injector.getInstance(SanityCheckService.class)
        sanityCheckService.performCheck()
    }


    @Test
    void test() throws IOException {
        Assertions.assertEquals(0, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(PaymentInLine.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())


        ImportService importService = injector.getInstance(ImportService.class)

        ImportParameter parameter = new ImportParameter()
        parameter.setKeyCode("ish.onCourse.import.paymentIn.csv")

        Map<String, byte[]> data = new HashMap<>()
        data.put("importFile", IOUtils.toByteArray(ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/PaymentInCSVImportTest.csv")))

        parameter.setData(data)

        importService.performImport(parameter)


        Assertions.assertEquals(185, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(187, ObjectSelect.query(PaymentInLine.class).select(cayenneContext).size())
        Assertions.assertEquals(16, ObjectSelect.query(Banking.class).select(cayenneContext).size())
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
