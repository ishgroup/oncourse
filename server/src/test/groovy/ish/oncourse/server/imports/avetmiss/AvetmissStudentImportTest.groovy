/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.Gender
import ish.common.types.UsiStatus
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.imports.ImportService
import ish.oncourse.server.upgrades.DataPopulation
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
				ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/avetmiss8/NAT00080.txt")))
        data.put("avetmiss85", IOUtils.toByteArray(
				ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/imports/avetmiss8/NAT00085.txt")))

        parameter.setData(data)
        importService.performImport(parameter)

        Assertions.assertEquals(13, ObjectSelect.query(Contact.class).select(cayenneContext).size())

        Contact contact1 = ObjectSelect.query(Contact.class)
				.where(Contact.FIRST_NAME.eq("MOHAMODA").andExp(Contact.LAST_NAME.eq("AALAX"))).selectOne(cayenneContext)

        Assertions.assertEquals("MOHAMODA", contact1.getFirstName())
        Assertions.assertEquals("ADOMAHOM", contact1.getMiddleName())
        Assertions.assertEquals("AALAX", contact1.getLastName())
        Assertions.assertTrue(contact1.getIsStudent())
        Assertions.assertEquals(LocalDate.of(1964, 7, 2), contact1.getBirthDate())
        Assertions.assertEquals(Gender.MALE, contact1.gender)
        Assertions.assertEquals("aBcd12345Z", contact1.student.usi)
        Assertions.assertEquals(UsiStatus.DEFAULT_NOT_SUPPLIED, contact1.student.usiStatus)
        Assertions.assertEquals("0289834839", contact1.getHomePhone())
        Assertions.assertEquals("049898923", contact1.getMobilePhone())
        Assertions.assertEquals("0289893567", contact1.getWorkPhone())
        Assertions.assertEquals("MOHAMODA@EXAMPLE.COM", contact1.getEmail())
        Assertions.assertEquals("2, 10, GULI ST", contact1.getStreet())
        Assertions.assertEquals("SULAXITO", contact1.getSuburb())
        Assertions.assertEquals(AvetmissStudentPriorEducation.MISC, contact1.getStudent().getPriorEducationCode())

        List<Contact> withoutSuburb = ObjectSelect.query(Contact.class)
                .where(Contact.SUBURB.isNull()).select(cayenneContext)
        Assertions.assertEquals(1, withoutSuburb.size())

        List<Contact> internationalUsiStatus = ObjectSelect.query(Contact.class)
                .where(Contact.STUDENT.dot(Student.USI_STATUS).eq(UsiStatus.INTERNATIONAL)).select(cayenneContext)
        Assertions.assertEquals(1, internationalUsiStatus.size())

        List<Contact> exemptionUsiStatus = ObjectSelect.query(Contact.class)
                .where(Contact.STUDENT.dot(Student.USI_STATUS).eq(UsiStatus.EXEMPTION)).select(cayenneContext)
        Assertions.assertEquals(1, exemptionUsiStatus.size())

        List<Contact> withEmptyEmail = ObjectSelect.query(Contact.class)
                .where(Contact.EMAIL.isNull()).select(cayenneContext)
        Assertions.assertEquals(3, withEmptyEmail.size())
    }
}
