package ish.util

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.MessageStatus
import ish.common.types.MessageType
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.scripting.api.MessageReceived
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup()
class MessageUtilTest extends TestWithDatabase {

    @AfterEach
    void teardown() {
        wipeTables()
    }

    @Test
    void testGenerateCreatorKey() {
        Contact contact = cayenneContext.newObject(Contact.class)
        Contact contact1 = cayenneContext.newObject(Contact.class)
        fillContact(contact, contact1)
        Country country = cayenneContext.newObject(Country.class)
        fillCountry(country)

        //check creatorKey generate without any entity
        Assertions.assertEquals('numb123!@#$%^&*()+=-0', MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0'))

        //check creatorKey generate with not committed entities. Runtime exception expected
        try {
            Assertions.assertEquals('numb123!@#$%^&*()+=-0', MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact, contact1))
            Assertions.fail()
        } catch (RuntimeException e) {
        }

        //check creatorKey generate with 1 entity
        cayenneContext.commitChanges()
        Assertions.assertEquals('numb123!@#$%^&*()+=-0_ dbontact_' + contact.getId(), MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact))

        //check creatorKey generate with several instances of entity
        Assertions.assertEquals(String.format('numb1230_Contact_%s_Contact_%s', contact.getId(), contact1.getId()), MessageUtils.generateCreatorKey("numb1230", contact, contact1))

        //check creatorKey generate with different entities
        Assertions.assertEquals(String.format('numb1230_Contact_%s_Country_%s_Contact_%s',
                contact.getId(), country.getObjectId().getIdSnapshot().get("id"), contact1.getId()),
                MessageUtils.generateCreatorKey("numb1230", contact, country, contact1))

        //check creatorKey generate with deleted entities. Runtime exception expected
        cayenneContext.deleteObject(contact)
        try {
            Assertions.assertNotNull(MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact))
            Assertions.fail()
        } catch (RuntimeException e) {
        }

        //check creatorKey generate with deleted and committed entity. Runtime exception expected
        cayenneContext.commitChanges()
        try {
            Assertions.assertNotNull(MessageUtils.generateCreatorKey('numb123!@#$%^&*()+=-0', contact, contact1))
            Assertions.fail()
        } catch (RuntimeException e) {
        }
    }

    @Test
    void testGetLastMessageByKey() {
        //no messages
        Assertions.assertFalse(MessageReceived.valueOf(cayenneContext, "noMessages").isPresent())
        Assertions.assertFalse(MessageReceived.valueOf(cayenneContext, "someKey").isPresent())

        //no messages with key
        //no committed messages
        Message message = cayenneContext.newObject(Message.class)
        message.setCreatorKey("someKey")
        fillMessage(message)
        cayenneContext.commitChanges()

        Assertions.assertFalse(MessageReceived.valueOf(cayenneContext, "notExistedKey").isPresent())
        //check message returned
        Assertions.assertTrue(MessageReceived.valueOf(cayenneContext, "someKey").isPresent())

        Message lastMessage = cayenneContext.newObject(Message.class)
        lastMessage.setCreatorKey("someKey")
        fillMessage(lastMessage)
        cayenneContext.commitChanges()

        Assertions.assertTrue(MessageReceived.valueOf(cayenneContext, "someKey").isPresent())
    }

    @Test
    void testGetLastMessageByKeyForContact() {
        Contact contact = cayenneContext.newObject(Contact.class)
        Contact contact1 = cayenneContext.newObject(Contact.class)
        fillContact(contact, contact1)
        cayenneContext.commitChanges()

        //no messages

        Assertions.assertFalse(MessageReceived.valueOf(cayenneContext, "noMessages", contact).isPresent())

        //contact1 has message, but not contact
        //expected: lastMessageByKey for contact will return null, for contact1 - return message
        MessagePerson mPerson = cayenneContext.newObject(MessagePerson.class)
        fillMessagePerson(mPerson)
        Message message = cayenneContext.newObject(Message.class)
        message.setCreatorKey("someKey")
        fillMessage(message)
        message.addToMessagePersons(mPerson)
        contact1.addToMessages(mPerson)
        cayenneContext.commitChanges()
        Assertions.assertFalse(MessageReceived.valueOf(cayenneContext, "someKey", contact).isPresent())
        Assertions.assertTrue(MessageReceived.valueOf(cayenneContext, "someKey", contact1).isPresent())

        mPerson = cayenneContext.newObject(MessagePerson.class)
        fillMessagePerson(mPerson)
        Message lastMessage = cayenneContext.newObject(Message.class)
        lastMessage.setCreatorKey("someKey")
        fillMessage(lastMessage)
        lastMessage.addToMessagePersons(mPerson)
        contact1.addToMessages(mPerson)
        cayenneContext.commitChanges()

        Assertions.assertTrue(MessageReceived.valueOf(cayenneContext, "someKey", contact1).isPresent())
    }

    private void fillMessage(Message... messages) {
        for (Message message : messages) {
            message.setEmailSubject("someSubject")
            message.setPostDescription("somePostDesc")
            message.setSmsText("someSmsText")
            message.setEmailBody("someBody")
            message.setEmailHtmlBody("someBody")
        }
    }

    private void fillContact(Contact... contacts) {
        for (Contact contact : contacts) {
            contact.setFirstName("FirstName")
            contact.setLastName("Last Name")
        }
    }

    private void fillCountry(Country... countries) {
        for (Country country : countries) {
            country.setName("Belarus")
        }
    }

    private void fillMessagePerson(MessagePerson... mPersons) {
        for (MessagePerson mPerson : mPersons) {
            mPerson.setStatus(MessageStatus.SENT)
            mPerson.setType(MessageType.EMAIL)
            mPerson.setAttemptCount(1)
            mPerson.setDestinationAddress("someDestinationAddress")
        }
    }
}
