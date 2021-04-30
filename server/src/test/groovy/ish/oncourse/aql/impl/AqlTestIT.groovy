/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.aql.impl

import ish.CayenneIshTestCase
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.SessionTest
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class AqlTestIT extends CayenneIshTestCase {

    private DataContext cayenneContext
    private AqlService aqlService

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/aql/SyntheticPathTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)
        cayenneContext = injector.getInstance(ICayenneService.class).getNewReadonlyContext()
        aqlService = new AntlrAqlService()
    }

    @Test
    void testContactRootTags() {
        CompilationResult result = aqlService
                .compile("#ContactTag",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        assertEquals(1, contacts.size())
        assertEquals("1abc", contacts.get(0).uniqueCode)
    }

    @Test
    void testStudentCourseClassWithTag() {
        CompilationResult result = aqlService
                .compile("studentCourseClass #CourseChildTag",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        assertEquals(1, contacts.size())
        assertEquals("1abc", contacts.get(0).uniqueCode)
    }

    @Test
    void testTutorCourseClassWithTag() {
        CompilationResult result = aqlService
                .compile("tutorCourseClass #CourseChildTag",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .select(cayenneContext)
        assertEquals(1, contacts.size())
        assertEquals("1abc", contacts.get(0).uniqueCode)
    }

    @Test
    void testMessagesIsEmpty() {
        CompilationResult result = aqlService
                .compile("messages is empty",
                        Contact.class, cayenneContext)
        assertTrue(result.getCayenneExpression().isPresent())
        assertTrue(result.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result.getCayenneExpression().get())
                .orderBy(Contact.UNIQUE_CODE.asc())
                .select(cayenneContext)
        assertEquals(2, contacts.size())
        assertEquals("2abc", contacts.get(0).uniqueCode)
        assertEquals("2abcd", contacts.get(1).uniqueCode)
    }

    @Test
    void testStudentEnrollmentsIsEmpty() {
        CompilationResult result1 = aqlService
                .compile("studentEnrolments is empty",
                        Contact.class, cayenneContext)
        assertTrue(result1.getCayenneExpression().isPresent())
        assertTrue(result1.getErrors().isEmpty())

        List<Contact> contacts = ObjectSelect.query(Contact)
                .where(result1.getCayenneExpression().get())
                .orderBy(Contact.UNIQUE_CODE.asc())
                .select(cayenneContext)
        assertEquals(2, contacts.size())

        CompilationResult result2 = aqlService
                .compile("student.enrolments is empty",
                        Contact.class, cayenneContext)
        assertTrue(result2.getCayenneExpression().isPresent())
        assertTrue(result2.getErrors().isEmpty())

        List<Contact> contacts2 = ObjectSelect.query(Contact)
                .where(result2.getCayenneExpression().get())
                .orderBy(Contact.UNIQUE_CODE.asc())
                .select(cayenneContext)
        assertEquals(contacts, contacts2)
    }
}
