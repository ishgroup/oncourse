package ish.oncourse.solr.functions.course

import ish.oncourse.solr.model.SContact
import ish.oncourse.solr.model.SCourse
import org.junit.Assert
import org.junit.Test

/**
 * User: akoiro
 * Date: 4/11/17
 */
class SCourseFunctionsTest {
    @Test
    void test_addContacts() {

        use(ContactFunctions) {
            List<SContact> contacts = [
                    ContactFunctionsTest.contact(),
                    ContactFunctionsTest.contact(),
                    ContactFunctionsTest.contact(),
                    ContactFunctionsTest.contact()
            ].collect { c -> c.getSContact() }
            SCourse sCourse = new SCourse()

            use(SCourseFunctions) {
                sCourse = sCourse.addContacts(contacts)
                Assert.assertEquals(contacts.tutorId, sCourse.tutorId)
                Assert.assertEquals(contacts.name, sCourse.tutor)
            }
        }
    }
}
