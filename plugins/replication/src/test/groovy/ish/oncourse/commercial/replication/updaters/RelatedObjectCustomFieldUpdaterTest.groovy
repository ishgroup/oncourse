/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.TestWithDatabase
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.ApplicationCustomField
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassCustomField
import ish.oncourse.server.cayenne.CourseCustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EnrolmentCustomField
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.cayenne.WaitingListCustomField
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v23.stubs.replication.CustomFieldStub
import static org.junit.Assert.assertNotNull
import org.junit.Test
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class RelatedObjectCustomFieldUpdaterTest extends TestWithDatabase {

    @Test
    void test() throws Exception {

        List<TestContainer> testCases = new ArrayList<>()
        testCases.add(createContactTestContainer())
        testCases.add(createCourseTestContainer())
        testCases.add(createCourseClassTestContainer())
        testCases.add(createEnrolmentTestContainer())
        testCases.add(createApplicationTestContainer())
        testCases.add(createWaitingListTestContainer())

        AngelUpdaterImpl updater = new AngelUpdaterImpl()

        testCases.each{testCase ->
            updater.updateEntityFromStub(testCase.stub, testCase.entity, testCase.callback)
        }
    }

    private class ContactTestRelationshipCallback extends TestRelationshipCallback {
        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = super.updateRelationShip(entityId, clazz)
            if (related == null && Contact.class.equals(clazz)) {
                Contact contact = mock(Contact.class)
                when(contact.getId()).thenReturn(2L)
                when(contact.getWillowId()).thenReturn(3L)
                related = contact
            }
            assertNotNull(related)
            return (M) related
        }
    }

    private TestContainer createContactTestContainer() {
        TestContainer container = new TestContainer()

        // prepare stub
        GenericReplicationStub stub = createCustomFieldStub("ContactCustomField",
                AbstractAngelUpdater.CONTACT_ENTITY_NAME, 1L, 2L, 10L)

        // prepare entity for update
        ContactCustomField entity = mock(ContactCustomField.class)

        // prepare relationship callback
        TestRelationshipCallback callback = new ContactTestRelationshipCallback()

        // add prepared data to test container
        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private class CourseTestRelationshipCallback extends TestRelationshipCallback {
        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = super.updateRelationShip(entityId, clazz)
            if (related == null && Course.class.equals(clazz)) {
                Course course = mock(Course.class)
                when(course.getId()).thenReturn(2L)
                when(course.getWillowId()).thenReturn(3L)
                related = course
            }
            assertNotNull(related)
            return (M) related
        }
    }

    private TestContainer createCourseTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("CourseCustomField",
                AbstractAngelUpdater.COURSE_ENTITY_NAME, 1L, 2L, 10L)

        CourseCustomField entity = mock(CourseCustomField.class)

        TestRelationshipCallback callback = new CourseTestRelationshipCallback()

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private class CourseClassTestRelationshipCallback extends TestRelationshipCallback {
        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = super.updateRelationShip(entityId, clazz)
            if (related == null && CourseClass.class.equals(clazz)) {
                CourseClass courseClass = mock(CourseClass.class)
                when(courseClass.getId()).thenReturn(2L)
                when(courseClass.getWillowId()).thenReturn(3L)
                related = courseClass
            }
            assertNotNull(related)
            return (M) related
        }
    }

    private TestContainer createCourseClassTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("CourseClassCustomField",
                AbstractAngelUpdater.COURSE_CLASS_ENTITY_NAME, 1L, 2L, 10L)

        CourseClassCustomField entity = mock(CourseClassCustomField.class)

        TestRelationshipCallback callback = new CourseClassTestRelationshipCallback()

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }


    private class EnrolmentTestRelationshipCallback extends TestRelationshipCallback {
        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = super.updateRelationShip(entityId, clazz)
            if (related == null && Enrolment.class.equals(clazz)) {
                Enrolment enrolment = mock(Enrolment.class)
                when(enrolment.getId()).thenReturn(2L)
                when(enrolment.getWillowId()).thenReturn(3L)
                related = enrolment
            }
            assertNotNull(related)
            return (M) related
        }
    }

    private TestContainer createEnrolmentTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("EnrolmentCustomField",
                AbstractAngelUpdater.ENROLMENT_ENTITY_NAME, 1L, 2L, 10L)

        EnrolmentCustomField entity = mock(EnrolmentCustomField.class)

        TestRelationshipCallback callback = new EnrolmentTestRelationshipCallback()

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private class ApplicationTestRelationshipCallback extends TestRelationshipCallback {
        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = super.updateRelationShip(entityId, clazz)
            if (related == null && Application.class.equals(clazz)) {
                Application application = mock(Application.class)
                when(application.getId()).thenReturn(2L)
                when(application.getWillowId()).thenReturn(3L)
                related = application
            }
            assertNotNull(related)
            return (M) related
        }
    }

    private TestContainer createApplicationTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("ApplicationCustomField",
                AbstractAngelUpdater.APPLICATION_ENTITY_NAME, 1L, 2L, 10L)

        ApplicationCustomField entity = mock(ApplicationCustomField.class)

        TestRelationshipCallback callback = new ApplicationTestRelationshipCallback()

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private class AeWaitingListTestRelationshipCallback extends TestRelationshipCallback {
        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = super.updateRelationShip(entityId, clazz)
            if (related == null && WaitingList.class.equals(clazz)) {
                WaitingList waitingList = mock(WaitingList.class)
                when(waitingList.getId()).thenReturn(2L)
                when(waitingList.getWillowId()).thenReturn(3L)
                related = waitingList
            }
            assertNotNull(related)
            return (M) related
        }
    }

    private TestContainer createWaitingListTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("WaitingListCustomField",
                AbstractAngelUpdater.WAITING_LIST_ENTITY_NAME, 1L, 2L, 10L)

        WaitingListCustomField entity = mock(WaitingListCustomField.class)

        TestRelationshipCallback callback = new AeWaitingListTestRelationshipCallback()

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private static GenericReplicationStub createCustomFieldStub(String entityIdentifier, String entityName, Long customFieldAngelId, Long customFieldWillowId, Long customFieldTypeId) {
        CustomFieldStub stub = mock(CustomFieldStub.class)
        when(stub.getEntityName()).thenReturn(entityName)
        when(stub.getEntityIdentifier()).thenReturn(entityIdentifier)
        when(stub.getAngelId()).thenReturn(customFieldAngelId)
        when(stub.getWillowId()).thenReturn(customFieldWillowId)
        when(stub.getCreated()).thenReturn(new Date())
        when(stub.getModified()).thenReturn(new Date())
        when(stub.getCustomFieldTypeId()).thenReturn(customFieldTypeId)
        return stub
    }

    private class TestContainer {
        GenericReplicationStub stub
        Queueable entity
        RelationShipCallback callback
    }

    private class TestRelationshipCallback implements RelationShipCallback {

        @Override
        <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
            Queueable related = null
            if (CustomFieldType.class.equals(clazz)) {
                CustomFieldType customFieldType = mock(CustomFieldType.class)
                when(customFieldType.getId()).thenReturn(10L)
                related = customFieldType
            }
            return (M) related
        }
    }
}
