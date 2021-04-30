package ish.oncourse.server.imports.avetmiss

import ish.CayenneIshTestCase
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.junit.Assert
import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by akoiro on 24/03/2016.
 */
class AvetmissStudentUpdateImportTest extends CayenneIshTestCase {
    @Before
    void setup() throws Exception {
        wipeTables()
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        dataPopulation.run()
    }

    @Test
    void  test() throws IOException {
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
        assertEquals(13, ObjectSelect.query(Contact.class).select(context).size())

        Contact contact1 = ObjectSelect.query(Contact.class)
                .where(Contact.FIRST_NAME.eq("MOHAMODA").andExp(Contact.LAST_NAME.eq("AALAX"))).selectOne(context)
        Assert.assertNotNull(contact1)
    }
}
