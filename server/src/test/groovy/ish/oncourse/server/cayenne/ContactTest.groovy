/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.access.DataContext
import org.junit.jupiter.api.Test

import static org.junit.Assert.*

/**
 */
class ContactTest extends CayenneIshTestCase {

	@Test
    void testUpdateUniqueCode() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName")
        contact.setLastName("lastName")

        assertNotNull("Checking uniqueCode: ", contact.getUniqueCode())
    }

	@Test
    void testUpdateStudentTutorFlags() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        assertFalse("Checking isStudent: ", contact.getIsStudent())
        assertFalse("Checking isTutor: ", contact.getIsTutor())

        Tutor tutor = newContext.newObject(Tutor.class)
        contact.setTutor(tutor)

        assertNotNull(tutor.getContact())
        assertFalse("Checking isStudent: ", contact.getIsStudent())
        assertTrue("Checking isTutor: ", contact.getIsTutor())

        newContext.commitChanges()

        assertNotNull(contact.getTutor())
        assertFalse("Checking isStudent: ", contact.getIsStudent())
        assertTrue("Checking isTutor: ", contact.getIsTutor())

        contact.setIsStudent(true)
        contact.setIsTutor(false)

        assertTrue("Checking isStudent: ", contact.getIsStudent())
        assertFalse("Checking isTutor: ", contact.getIsTutor())

        newContext.commitChanges()

        assertFalse("Checking isStudent: ", contact.getIsStudent())
        assertTrue("Checking isTutor: ", contact.getIsTutor())

        newContext.deleteObjects(tutor)
        newContext.commitChanges()

        assertFalse("Checking isStudent: ", contact.getIsStudent())
        assertFalse("Checking isTutor: ", contact.getIsTutor())

        Contact contact2 = newContext.newObject(Contact.class)
        contact2.setFirstName("firstName2")
        contact2.setLastName("lastName1")

        Student student = newContext.newObject(Student.class)
        contact2.setStudent(student)

        assertNotNull(student.getContact())
        assertTrue("Checking isStudent: ", contact2.getIsStudent())
        assertFalse("Checking isTutor: ", contact2.getIsTutor())

        newContext.commitChanges()

        assertNotNull(contact2.getStudent())
        assertTrue("Checking isStudent: ", contact2.getIsStudent())
        assertFalse("Checking isTutor: ", contact2.getIsTutor())

        contact2.setIsStudent(false)
        contact2.setIsTutor(true)

        assertFalse("Checking isStudent: ", contact2.getIsStudent())
        assertTrue("Checking isTutor: ", contact2.getIsTutor())

        newContext.commitChanges()

        assertTrue("Checking isStudent: ", contact2.getIsStudent())
        assertFalse("Checking isTutor: ", contact2.getIsTutor())

        newContext.deleteObjects(student)
        newContext.commitChanges()

        assertFalse("Checking isStudent: ", contact2.getIsStudent())
        assertFalse("Checking isTutor: ", contact2.getIsTutor())
    }
}
