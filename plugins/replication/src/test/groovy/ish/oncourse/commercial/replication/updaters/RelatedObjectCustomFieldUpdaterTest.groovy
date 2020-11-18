package ish.oncourse.commercial.replication.updaters

import ish.CayenneIshTestCase
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v22.stubs.replication.CustomFieldStub
import org.junit.Test

import static org.junit.Assert.assertNotNull

class RelatedObjectCustomFieldUpdaterTest extends CayenneIshTestCase {

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

    private TestContainer createContactTestContainer() {
        TestContainer container = new TestContainer()

        // prepare stub
        GenericReplicationStub stub = createCustomFieldStub("ContactCustomField",
                AbstractAngelUpdater.CONTACT_ENTITY_NAME, 1L, 2L, 10L)

        // prepare entity for update
        ContactCustomField entity = org.mockito.Mockito.mock(ContactCustomField.class)

        // prepare relationship callback
        TestRelationshipCallback callback = new TestRelationshipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                Queueable related = super.updateRelationShip(entityId, clazz)
                if (related == null && Contact.class.equals(clazz)) {
                    Contact contact = org.mockito.Mockito.mock(Contact.class)
                    org.mockito.Mockito.when(contact.getId()).thenReturn(2L)
                    org.mockito.Mockito.when(contact.getWillowId()).thenReturn(3L)
                    related = contact
                }
                assertNotNull(related)
                return (M) related
            }
        }

        // add prepared data to test container
        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private TestContainer createCourseTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("CourseCustomField",
                AbstractAngelUpdater.COURSE_ENTITY_NAME, 1L, 2L, 10L)

        CourseCustomField entity = org.mockito.Mockito.mock(CourseCustomField.class)

        TestRelationshipCallback callback = new TestRelationshipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                Queueable related = super.updateRelationShip(entityId, clazz)
                if (related == null && Course.class.equals(clazz)) {
                    Course course = org.mockito.Mockito.mock(Course.class)
                    org.mockito.Mockito.when(course.getId()).thenReturn(2L)
                    org.mockito.Mockito.when(course.getWillowId()).thenReturn(3L)
                    related = course
                }
                assertNotNull(related)
                return (M) related
            }
        }

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private TestContainer createCourseClassTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("CourseClassCustomField",
                AbstractAngelUpdater.COURSE_CLASS_ENTITY_NAME, 1L, 2L, 10L)

        CourseClassCustomField entity = org.mockito.Mockito.mock(CourseClassCustomField.class)

        TestRelationshipCallback callback = new TestRelationshipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                Queueable related = super.updateRelationShip(entityId, clazz)
                if (related == null && CourseClass.class.equals(clazz)) {
                    CourseClass courseClass = org.mockito.Mockito.mock(CourseClass.class)
                    org.mockito.Mockito.when(courseClass.getId()).thenReturn(2L)
                    org.mockito.Mockito.when(courseClass.getWillowId()).thenReturn(3L)
                    related = courseClass
                }
                assertNotNull(related)
                return (M) related
            }
        }

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private TestContainer createEnrolmentTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("EnrolmentCustomField",
                AbstractAngelUpdater.ENROLMENT_ENTITY_NAME, 1L, 2L, 10L)

        EnrolmentCustomField entity = org.mockito.Mockito.mock(EnrolmentCustomField.class)

        TestRelationshipCallback callback = new TestRelationshipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                Queueable related = super.updateRelationShip(entityId, clazz)
                if (related == null && Enrolment.class.equals(clazz)) {
                    Enrolment enrolment = org.mockito.Mockito.mock(Enrolment.class)
                    org.mockito.Mockito.when(enrolment.getId()).thenReturn(2L)
                    org.mockito.Mockito.when(enrolment.getWillowId()).thenReturn(3L)
                    related = enrolment
                }
                assertNotNull(related)
                return (M) related
            }
        }

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private TestContainer createApplicationTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("ApplicationCustomField",
                AbstractAngelUpdater.APPLICATION_ENTITY_NAME, 1L, 2L, 10L)

        ApplicationCustomField entity = org.mockito.Mockito.mock(ApplicationCustomField.class)

        TestRelationshipCallback callback = new TestRelationshipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                Queueable related = super.updateRelationShip(entityId, clazz)
                if (related == null && Application.class.equals(clazz)) {
                    Application application = org.mockito.Mockito.mock(Application.class)
                    org.mockito.Mockito.when(application.getId()).thenReturn(2L)
                    org.mockito.Mockito.when(application.getWillowId()).thenReturn(3L)
                    related = application
                }
                assertNotNull(related)
                return (M) related
            }
        }

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private TestContainer createWaitingListTestContainer() {
        TestContainer container = new TestContainer()

        GenericReplicationStub stub = createCustomFieldStub("WaitingListCustomField",
                AbstractAngelUpdater.WAITING_LIST_ENTITY_NAME, 1L, 2L, 10L)

        WaitingListCustomField entity = org.mockito.Mockito.mock(WaitingListCustomField.class)

        TestRelationshipCallback callback = new TestRelationshipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                Queueable related = super.updateRelationShip(entityId, clazz)
                if (related == null && WaitingList.class.equals(clazz)) {
                    WaitingList waitingList = org.mockito.Mockito.mock(WaitingList.class)
                    org.mockito.Mockito.when(waitingList.getId()).thenReturn(2L)
                    org.mockito.Mockito.when(waitingList.getWillowId()).thenReturn(3L)
                    related = waitingList
                }
                assertNotNull(related)
                return (M) related
            }
        }

        container.entity = entity
        container.stub = stub
        container.callback = callback

        return container
    }

    private static GenericReplicationStub createCustomFieldStub(String entityIdentifier, String entityName, Long customFieldAngelId, Long customFieldWillowId, Long customFieldTypeId) {
        CustomFieldStub stub = org.mockito.Mockito.mock(CustomFieldStub.class)
        org.mockito.Mockito.when(stub.getEntityName()).thenReturn(entityName)
        org.mockito.Mockito.when(stub.getEntityIdentifier()).thenReturn(entityIdentifier)
        org.mockito.Mockito.when(stub.getAngelId()).thenReturn(customFieldAngelId)
        org.mockito.Mockito.when(stub.getWillowId()).thenReturn(customFieldWillowId)
        org.mockito.Mockito.when(stub.getCreated()).thenReturn(new Date())
        org.mockito.Mockito.when(stub.getModified()).thenReturn(new Date())
        org.mockito.Mockito.when(stub.getCustomFieldTypeId()).thenReturn(customFieldTypeId)
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
                CustomFieldType customFieldType = org.mockito.Mockito.mock(CustomFieldType.class)
                org.mockito.Mockito.when(customFieldType.getId()).thenReturn(10L)
                related = customFieldType
            }
            return (M) related
        }
    }
}