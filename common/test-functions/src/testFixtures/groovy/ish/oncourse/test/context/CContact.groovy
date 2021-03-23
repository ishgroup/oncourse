package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class CContact {
    private Faker faker = DataContext.faker
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
        builder.contact.givenName = builder.faker.name().firstName()
        builder.contact.familyName = builder.faker.name().lastName()
        builder.contact.businessPhoneNumber = builder.faker.phoneNumber().cellPhone()
        builder.contact.mobilePhoneNumber = builder.faker.phoneNumber().cellPhone()
        builder.contact.faxNumber  = builder.faker.phoneNumber().cellPhone()
        builder.contact.suburb = builder.faker.address().streetSuffix()
        builder.contact.street = builder.faker.address().streetAddress()
        builder.contact.state = builder.faker.address().state()
        builder
    }
}
