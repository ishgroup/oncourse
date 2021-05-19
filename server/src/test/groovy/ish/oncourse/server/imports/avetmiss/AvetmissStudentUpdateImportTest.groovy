package ish.oncourse.server.imports.avetmiss


import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup
class AvetmissStudentUpdateImportTest extends TestWithDatabase {

    @BeforeEach
    void populateData() throws Exception {
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)
        dataPopulation.run()
    }


    @Test
    void test() throws IOException {
        ImportService importService = injector.getInstance(ImportService.class)

        ImportParameter parameter = new ImportParameter()
        parameter.setKeyCode("ish.onCourse.import.update.avetmiss.student")

        Map<String, byte[]> data = new HashMap<>()
        data.put("avetmiss80", IOUtils.toByteArray(
                ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/avetmiss8/NAT00080.txt")))
        data.put("avetmiss85", IOUtils.toByteArray(
                ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/avetmiss8/NAT00085.txt")))

        parameter.setData(data)

        importService.performImport(parameter)

        ObjectContext context = importService.getCayenneService().getNewContext()
        Assertions.assertEquals(13, ObjectSelect.query(Contact.class).select(context).size())

        Contact contact1 = ObjectSelect.query(Contact.class)
                .where(Contact.FIRST_NAME.eq("MOHAMODA").andExp(Contact.LAST_NAME.eq("AALAX"))).selectOne(context)
        Assertions.assertNotNull(contact1)
    }
}
