/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.imports.avetmiss


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.Gender
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
class AvetmissStudentImportTest extends CayenneIshTestCase {

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        dataPopulation.run()
    }

    
    @Test
    void testImport() throws Exception {
        ImportService importService = injector.getInstance(ImportService.class)

        ImportParameter parameter = new ImportParameter()
        parameter.setKeyCode("ish.onCourse.import.avetmiss.student")

        Map<String, byte[]> data = new HashMap<>()
        data.put("avetmiss80", IOUtils.toByteArray(
                ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/export/avetmiss8/import/NAT00080.txt")))
        data.put("avetmiss85", IOUtils.toByteArray(
                ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/export/avetmiss8/import/NAT00085.txt")))

        parameter.setData(data)

        importService.performImport(parameter)

        ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()

        Assertions.assertEquals(13, ObjectSelect.query(Contact.class).select(context).size())

        Contact contact1 = ObjectSelect.query(Contact.class)
                .where(Contact.FIRST_NAME.eq("MOHAMODA").andExp(Contact.LAST_NAME.eq("AALAX"))).selectOne(context)

        Assertions.assertEquals("MOHAMODA", contact1.getFirstName())
        Assertions.assertEquals("ADOMAHOM", contact1.getMiddleName())
        Assertions.assertEquals("AALAX", contact1.getLastName())
        Assertions.assertTrue(contact1.getIsStudent())
        Assertions.assertEquals(LocalDate.of(1964, 7, 2), contact1.getBirthDate())
        Assertions.assertEquals(Gender.MALE, contact1.gender)
        Assertions.assertEquals("0289834839", contact1.getHomePhone())
        Assertions.assertEquals("049898923", contact1.getMobilePhone())
        Assertions.assertEquals("0289893567", contact1.getWorkPhone())
        Assertions.assertEquals("MOHAMODA@EXAMPLE.COM", contact1.getEmail())
        Assertions.assertEquals("2, 10, GULI ST", contact1.getStreet())
        Assertions.assertEquals("SULAXITO", contact1.getSuburb())
        Assertions.assertEquals(AvetmissStudentPriorEducation.MISC, contact1.getStudent().getPriorEducationCode())
        Assertions.assertEquals(new Integer(1976), contact1.getStudent().getYearSchoolCompleted())
    }
}
