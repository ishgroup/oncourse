package ish.oncourse.willow.cayenne

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.QueuedRecord
import ish.oncourse.model.QueuedTransaction
import ish.oncourse.willow.service.ApiTest
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.assertEquals

class QueueableLifecycleListenerTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/cayenne/QueueableLifecycleListenerTest.xml'
    }

    @Test
    void replicatedContextTest() {

        ObjectContext context = cayenneService.newContext()

        addContact(context)

        List<QueuedTransaction> transactions = ObjectSelect.query(QueuedTransaction).select(context)

        assertEquals(1, transactions.size())
        assertEquals(2, transactions[0].queuedRecords.size())
    }


    @Test
    void nonReplicatedContextTest() {

        ObjectContext context = cayenneService.newNonReplicatingContext()

        addContact(context)

        List<QueuedTransaction> transactions = ObjectSelect.query(QueuedTransaction).select(context)
        assertEquals(0, transactions.size())
        
        List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord).select(context)
        assertEquals(0, queuedRecords.size())
    }

    private addContact(ObjectContext context) {
        College college = SelectById.query(College, 1L).selectOne(context)
        Contact contact = context.newObject(Contact.class)
        contact.lastLoginTime = new Date()
        contact.setCollege(college)

        contact.givenName = 'John'
        contact.familyName = 'Smith'
        contact.emailAddress = 'email@addres.com'
        contact.createNewStudent()

        contact.isMarketingViaEmailAllowed = true
        contact.isMarketingViaPostAllowed = true
        contact.isMarketingViaSMSAllowed = true
        contact.objectContext.commitChanges()
    }

}
