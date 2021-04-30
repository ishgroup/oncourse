package ish.util

import ish.CayenneIshTestCase
import ish.common.types.MessageStatus
import ish.common.types.MessageType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.scripting.api.MessageReceived
import org.apache.cayenne.access.DataContext
import org.junit.After
import org.junit.jupiter.api.Test

import static org.junit.Assert.*

class MessageUtilTest extends CayenneIshTestCase {

    @After
    void teardown() {
        wipeTables()
    }

    @Test
    void testGenerateCreatorKey(){
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Contact contact = context.newObject(Contact.class)
        Contact contact1 = context.newObject(Contact.class)
        fillContact(contact, contact1)
        Country country = context.newObject(Country.class)
        fillCountry(country)

        //check creatorKey generate without any entity
        assertEquals('numb123!@#$%^&*()+=-0', MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0'))

        //check creatorKey generate with not committed entities. Runtime exception expected
        try {
            assertEquals('numb123!@#$%^&*()+=-0', MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact, contact1))
            fail()
        } catch (RuntimeException e){}

        //check creatorKey generate with 1 entity
        context.commitChanges()
        assertEquals('numb123!@#$%^&*()+=-0_Contact_' + contact.getId(), MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact))

        //check creatorKey generate with several instances of entity
        assertEquals(String.format('numb1230_Contact_%s_Contact_%s', contact.getId(), contact1.getId()), MessageUtils.generateCreatorKey("numb1230", contact, contact1))

        //check creatorKey generate with different entities
        assertEquals(String.format('numb1230_Contact_%s_Country_%s_Contact_%s',
                contact.getId(), country.getObjectId().getIdSnapshot().get("id"), contact1.getId()),
                MessageUtils.generateCreatorKey("numb1230", contact, country, contact1))

        //check creatorKey generate with deleted entities. Runtime exception expected
        context.deleteObject(contact)
        try {
            assertNotNull(MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact))
            fail()
        } catch (RuntimeException e){}

        //check creatorKey generate with deleted and committed entity. Runtime exception expected
        context.commitChanges()
        try {
            assertNotNull(MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact, contact1))
            fail()
        } catch (RuntimeException e){}
    }

    @Test
    void testGetLastMessageByKey(){
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        //no messages
        assertFalse(MessageReceived.valueOf(context, "noMessages").isPresent())
        assertFalse(MessageReceived.valueOf(context, "someKey").isPresent())

        //no messages with key
        //no committed messages
        Message message = context.newObject(Message.class)
        message.setCreatorKey("someKey")
        fillMessage(message)
        context.commitChanges()

        assertFalse(MessageReceived.valueOf(context, "notExistedKey").isPresent())
        //check message returned
        assertTrue(MessageReceived.valueOf(context, "someKey").isPresent())

        Message lastMessage = context.newObject(Message.class)
        lastMessage.setCreatorKey("someKey")
        fillMessage(lastMessage)
        context.commitChanges()

        assertTrue(MessageReceived.valueOf(context, "someKey").isPresent())
    }

    @Test
    void testGetLastMessageByKeyForContact(){
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Contact contact = context.newObject(Contact.class)
        Contact contact1 = context.newObject(Contact.class)
        fillContact(contact, contact1)
        context.commitChanges()

        //no messages

        assertFalse(MessageReceived.valueOf(context, "noMessages", contact).isPresent())

        //contact1 has message, but not contact
        //expected: lastMessageByKey for contact will return null, for contact1 - return message
        MessagePerson mPerson = context.newObject(MessagePerson.class)
        fillMessagePerson(mPerson)
        Message message = context.newObject(Message.class)
        message.setCreatorKey("someKey")
        fillMessage(message)
        message.addToMessagePersons(mPerson)
        contact1.addToMessages(mPerson)
        context.commitChanges()
        assertFalse(MessageReceived.valueOf(context, "someKey", contact).isPresent())
        assertTrue(MessageReceived.valueOf(context, "someKey", contact1).isPresent())

        mPerson = context.newObject(MessagePerson.class)
        fillMessagePerson(mPerson)
        Message lastMessage = context.newObject(Message.class)
        lastMessage.setCreatorKey("someKey")
        fillMessage(lastMessage)
        lastMessage.addToMessagePersons(mPerson)
        contact1.addToMessages(mPerson)
        context.commitChanges()

        assertTrue(MessageReceived.valueOf(context, "someKey", contact1).isPresent())
    }

    private void fillMessage(Message... messages){
        for (Message message : messages) {
            message.setEmailSubject("someSubject")
            message.setPostDescription("somePostDesc")
            message.setSmsText("someSmsText")
            message.setEmailBody("someBody")
            message.setEmailHtmlBody("someBody")
        }
    }

    private void fillContact(Contact... contacts){
        for (Contact contact : contacts){
            contact.setFirstName("FirstName")
            contact.setLastName("Last Name")
        }
    }

    private void fillCountry(Country... countries) {
        for (Country country : countries){
            country.setName("Belarus")
        }
    }

    private void fillMessagePerson(MessagePerson... mPersons){
        for (MessagePerson mPerson : mPersons){
            mPerson.setStatus(MessageStatus.SENT)
            mPerson.setType(MessageType.EMAIL)
            mPerson.setAttemptCount(1)
            mPerson.setDestinationAddress("someDestinationAddress")
        }
    }
}
