/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class ContactTest extends TestWithDatabase {

    @Test
    void testUpdateUniqueCode() {
        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName")
        contact.setLastName("lastName")

        Assertions.assertNotNull("Checking uniqueCode: ", contact.getUniqueCode())
    }

    @Test
    void testUpdateStudentTutorFlags() {
        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Assertions.assertFalse(contact.getIsStudent())
        Assertions.assertFalse(contact.getIsTutor())

        Tutor tutor = cayenneContext.newObject(Tutor.class)
        contact.setTutor(tutor)

        Assertions.assertNotNull(tutor.getContact())
        Assertions.assertFalse(contact.getIsStudent())
        Assertions.assertTrue(contact.getIsTutor())

        cayenneContext.commitChanges()

        Assertions.assertNotNull(contact.getTutor())
        Assertions.assertFalse(contact.getIsStudent())
        Assertions.assertTrue(contact.getIsTutor())

        contact.setIsStudent(true)
        contact.setIsTutor(false)

        Assertions.assertTrue(contact.getIsStudent())
        Assertions.assertFalse(contact.getIsTutor())

        cayenneContext.commitChanges()

        Assertions.assertFalse(contact.getIsStudent())
        Assertions.assertTrue(contact.getIsTutor())

        cayenneContext.deleteObjects(tutor)
        cayenneContext.commitChanges()

        Assertions.assertFalse(contact.getIsStudent())
        Assertions.assertFalse(contact.getIsTutor())

        Contact contact2 = cayenneContext.newObject(Contact.class)
        contact2.setFirstName("firstName2")
        contact2.setLastName("lastName1")

        Student student = cayenneContext.newObject(Student.class)
        contact2.setStudent(student)

        Assertions.assertNotNull(student.getContact())
        Assertions.assertTrue(contact2.getIsStudent())
        Assertions.assertFalse(contact2.getIsTutor())

        cayenneContext.commitChanges()

        Assertions.assertNotNull(contact2.getStudent())
        Assertions.assertTrue(contact2.getIsStudent())
        Assertions.assertFalse(contact2.getIsTutor())

        contact2.setIsStudent(false)
        contact2.setIsTutor(true)

        Assertions.assertFalse(contact2.getIsStudent())
        Assertions.assertTrue(contact2.getIsTutor())

        cayenneContext.commitChanges()

        Assertions.assertTrue(contact2.getIsStudent())
        Assertions.assertFalse(contact2.getIsTutor())

        cayenneContext.deleteObjects(student)
        cayenneContext.commitChanges()

        Assertions.assertFalse(contact2.getIsStudent())
        Assertions.assertFalse(contact2.getIsTutor())
    }
}
