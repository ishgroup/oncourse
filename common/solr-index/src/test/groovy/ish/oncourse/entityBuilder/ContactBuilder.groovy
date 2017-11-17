package ish.oncourse.entityBuilder

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class ContactBuilder {
    private ObjectContext objectContext
    private Contact contact

    private ContactBuilder (ObjectContext context){
        objectContext = context
    }

    ContactBuilder firstName(String firstName){
        contact.givenName = firstName
    }

    ContactBuilder middleName(String middleName){
        contact.middleName = middleName
    }

    ContactBuilder familyName(String familyName){
        contact.familyName = familyName
    }

    Contact build (){
        objectContext.commitChanges()
        contact
    }

    static ContactBuilder instance(ObjectContext context, College college, String uniqueCode){
        ContactBuilder builder = new ContactBuilder(context)
        builder.createDefaultContact(college, uniqueCode)
        builder
    }

    private ContactBuilder createDefaultContact(College college, String uniqueCode){
        contact = objectContext.newObject(Contact)
        contact.college = objectContext.localObject(college)
        contact.uniqueCode = uniqueCode
        contact.givenName = 'John'
        contact.familyName = 'Smith'
        this
    }
}
