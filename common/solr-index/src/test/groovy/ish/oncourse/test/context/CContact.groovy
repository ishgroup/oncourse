package ish.oncourse.test.context

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class CContact {
    private ObjectContext objectContext
    Contact contact

    private CContact(){}

    CContact firstName(String firstName){
        contact.givenName = firstName
        this
    }

    CContact middleName(String middleName){
        contact.middleName = middleName
        this
    }

    CContact familyName(String familyName){
        contact.familyName = familyName
        this
    }

    CContact build (){
        objectContext.commitChanges()
        this
    }

    static CContact instance(ObjectContext context, College college, String uniqueCode){
        CContact builder = new CContact()
        builder.objectContext = context

        builder.contact = builder.objectContext.newObject(Contact)
        builder.contact.college = builder.objectContext.localObject(college)
        builder.contact.uniqueCode = uniqueCode
        builder.contact.givenName = 'John'
        builder.contact.familyName = 'Smith'

        builder
    }
}
