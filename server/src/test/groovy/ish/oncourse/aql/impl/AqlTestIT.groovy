/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.aql.impl

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(readOnly = true, value = "ish/oncourse/aql/SyntheticPathTestDataSet.xml")
class AqlTestIT extends CayenneIshTestCase {

    private AqlService aqlService

    @BeforeAll
    void setup() throws Exception {
        aqlService = new AntlrAqlService()
    }

    @Test
    void testContactRootTags() {
        CompilationResult result = aqlService
                .compile("#ContactTag",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        Assertions.assertEquals(1, contacts.size())
        Assertions.assertEquals("1abc", contacts.get(0).uniqueCode)
    }

    @Test
    void testStudentCourseClassWithTag() {
        CompilationResult result = aqlService
                .compile("studentCourseClass #CourseChildTag",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        Assertions.assertEquals(1, contacts.size())
        Assertions.assertEquals("1abc", contacts.get(0).uniqueCode)
    }

    @Test
    void testTutorCourseClassWithTag() {
        CompilationResult result = aqlService
                .compile("tutorCourseClass #CourseChildTag",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        Assertions.assertEquals(1, contacts.size())
        Assertions.assertEquals("1abc", contacts.get(0).uniqueCode)
    }

    @Test
    void testMessagesIsEmpty() {
        CompilationResult result = aqlService
                .compile("messages is empty",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result.getCayenneExpression().isPresent())
        Assertions.assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .orderBy(Contact.UNIQUE_CODE.asc())
                .select(cayenneContext)
        Assertions.assertEquals(2, contacts.size())
        Assertions.assertEquals("2abc", contacts.get(0).uniqueCode)
        Assertions.assertEquals("2abcd", contacts.get(1).uniqueCode)
    }

    @Test
    void testStudentEnrollmentsIsEmpty() {
        CompilationResult result1 = aqlService
                .compile("studentEnrolments is empty",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result1.getCayenneExpression().isPresent())
        Assertions.assertTrue(result1.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result1.getCayenneExpression().get())
                .orderBy(Contact.UNIQUE_CODE.asc())
                .select(cayenneContext)
        Assertions.assertEquals(2, contacts.size())

        CompilationResult result2 = aqlService
                .compile("student.enrolments is empty",
                        Contact.class, cayenneContext)
        Assertions.assertTrue(result2.getCayenneExpression().isPresent())
        Assertions.assertTrue(result2.getErrors().isEmpty())

        List<Contact> contacts2 = ObjectSelect.query(Contact)
                .where(result2.getCayenneExpression().get())
                .orderBy(Contact.UNIQUE_CODE.asc())
                .select(cayenneContext)
        Assertions.assertEquals(contacts, contacts2)
    }
}
