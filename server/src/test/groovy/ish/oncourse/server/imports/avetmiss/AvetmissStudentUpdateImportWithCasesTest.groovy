package ish.oncourse.server.imports.avetmiss


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.imports.ImportParameter
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CustomFieldType
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
class AvetmissStudentUpdateImportWithCasesTest extends CayenneIshTestCase {
    public static final String ANGIE_CONTACT_FIRST_NAME = "ANGIE"
    ImportService importService
    ICayenneService cayenneService
    ObjectContext context
    ImportParameter parameter

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        DataPopulation dataPopulation = injector.getInstance(DataPopulation)
        dataPopulation.run()

        importService = injector.getInstance(ImportService)
        cayenneService = importService.cayenneService
        context = cayenneService.newContext
        parameter = new ImportParameter()
        parameter.setKeyCode("ish.onCourse.import.update.avetmiss.student")
    }


    @Test
    void updateNotStudent() {
        Contact notStudent = createAngieContact(context)
        notStudent.isStudent = false
        notStudent.student = null
        context.commitChanges()

        Assertions.assertFalse(notStudent.getIsStudent())
        Assertions.assertNull(notStudent.getStudent())

        processImport("studentNAT00080.txt", "studentNAT00085.txt")

        notStudent = ObjectSelect.query(Contact).where(Contact.FIRST_NAME.eq(ANGIE_CONTACT_FIRST_NAME)).selectOne(cayenneService.newContext)
        Assertions.assertTrue(notStudent.getIsStudent(), "contact was not a student, after import it is")
        Assertions.assertNotNull(notStudent.getStudent(), "contact was not a student, after import it is")
    }


    @Test
    void createStudentFromNat80Nat85ByImportUpdateScript() {
        Assertions.assertTrue(ObjectSelect.query(Contact).select(context).isEmpty(), "There are no contacts in DB before Import")

        processImport("createStudentNAT00080.txt", "createStudentNAT00085.txt")

        Assertions.assertEquals(2, ObjectSelect.query(Contact).select(cayenneService.newContext).size())
    }

    /**
     * Contact duplication case!
     */

    @Test
    void updateStudentWithoutBirthdate() {
        Contact withoutBirthdate = createAngieContact(context)
        withoutBirthdate.birthDate = null
        context.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(Contact).select(context).size(), "There are 1 contact in DB before Import")

        processImport("studentNAT00080.txt", "studentNAT00085.txt")

        Assertions.assertEquals(2, ObjectSelect.query(Contact).select(cayenneService.newContext).size())
        Assertions.assertEquals(2, ObjectSelect.query(Contact).where(Contact.FIRST_NAME.eq(ANGIE_CONTACT_FIRST_NAME)).select(context).size(), "NAT80 is self-contained and is searching for name, surname AND BIRTHDAY to define existing contact. If contact in DB have no BIRTHDAY, studentUpdateImport will create duplicate contact with BIRTHDAY")
    }

    /**
     * Contact duplication case!
     */

    @Test
    void updateStudentWithoutEmail() {
        Contact withoutEmail = createAngieContact(context)
        withoutEmail.email = null
        context.commitChanges()

        processImport("studentNAT00080.txt", "studentNAT00085.txt")

        Assertions.assertEquals(2, ObjectSelect.query(Contact).select(cayenneService.newContext).size())
        Assertions.assertEquals(2, ObjectSelect.query(Contact).where(Contact.FIRST_NAME.eq(ANGIE_CONTACT_FIRST_NAME)).select(context).size(), "NAT85 is self-contained and is searching for name, surname AND EMAIL to define existing contact. If contact in DB have no EMAIL, studentUpdateImport will create duplicate contact with EMAIL")
    }

    /**
     * we can change contact's name or/and surname
     * Condition: contact must be NEW (NOT exist in DB)
     */

    @Test
    void changeNameViaImportForNewContact() {
        Assertions.assertTrue(ObjectSelect.query(Contact).select(context).isEmpty())
        processImport("changeNameNAT00080.txt", "changeNameNAT00085.txt")

        List<Contact> contacts = ObjectSelect.query(Contact).select(context)
        Assertions.assertEquals(1, contacts.size())
        Assertions.assertNotEquals(ANGIE_CONTACT_FIRST_NAME, contacts.get(0).firstName)
        Assertions.assertEquals("Karol".toUpperCase(), contacts.get(0).firstName)
    }

    /**
     * Contact duplication case!
     * we CAN'T change contact's name or/and surname during update of existing contact
     */

    @Test
    void changeNameViaImportForExistingContact() {
        createAngieContact(context)
        context.commitChanges()

        processImport("changeNameNAT00080.txt", "changeNameNAT00085.txt")

        List<Contact> contacts = ObjectSelect.query(Contact).select(cayenneService.newContext)
        Assertions.assertEquals(2, contacts.size())
        Assertions.assertNotNull(contacts.findAll() { c -> c.firstName.equals(ANGIE_CONTACT_FIRST_NAME) })
        Assertions.assertNotNull(contacts.findAll() { c -> c.firstName.equalsIgnoreCase("Karol") })
    }

    /**
     * it was a case, where 2+ same CustomFieldTypes were created
     * case: nat80 contains first existing contact and 2+ new contacts, nat85 also contains 1+ new contacts
     */

    @Test
    void oneCustomFieldTypeCreated() {
        createAngieContact(context)
        context.commitChanges()

        Assertions.assertEquals(0, ObjectSelect.query(CustomFieldType).where(CustomFieldType.NAME.eq("clientId")).selectCount(context))

        processImport("twoNewContactsNAT00080.txt", "twoNewContactsNAT00085.txt")

        Assertions.assertEquals(1, ObjectSelect.query(CustomFieldType).where(CustomFieldType.NAME.eq("clientId")).selectCount(cayenneService.newContext))
    }

    private void processImport(String nat80Name, String nat85Name) {
        Map<String, byte[]> data = new HashMap<>()
        data.put("avetmiss80", IOUtils.toByteArray(
                ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/export/avetmiss8/import/" + nat80Name)))
        data.put("avetmiss85", IOUtils.toByteArray(
                ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/export/avetmiss8/import/" + nat85Name)))
        parameter.setData(data)

        importService.performImport(parameter)
    }

    private Contact createAngieContact(ObjectContext context) {
        Contact angieContact = context.newObject(Contact)
        angieContact.firstName = ANGIE_CONTACT_FIRST_NAME
        angieContact.lastName = "JONES"
        angieContact.email = "ANGELA@EXAMPLE.COM"
        angieContact.birthDate = LocalDate.of(1983, 3, 9)
        angieContact
    }
}
