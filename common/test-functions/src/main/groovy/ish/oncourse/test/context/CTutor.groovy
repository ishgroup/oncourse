package ish.oncourse.test.context

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Tutor
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class CTutor {
    private ObjectContext objectContext
    Tutor tutor

    private CTutor(){}

    CTutor build(){
        objectContext.commitChanges()
        this
    }

    static CTutor instance(ObjectContext context, College college, String contactUniqueCode){
        Contact contact = CContact.instance(context, college, contactUniqueCode).contact
        CTutor builder = new CTutor()
        builder.createTutor(context, contact)
    }

    static CTutor instance(ObjectContext context, Contact contact){
        CTutor builder = new CTutor()
        builder.createTutor(context, contact)
    }

    private CTutor createTutor(ObjectContext context, Contact contact){

        objectContext = context

        tutor = objectContext.newObject(Tutor)
        tutor.contact = contact
        tutor.college = contact.college
        objectContext.commitChanges()
        this
    }

    CTutor angelId(long angelId) {
        tutor.angelId = angelId
        this
    }
}
