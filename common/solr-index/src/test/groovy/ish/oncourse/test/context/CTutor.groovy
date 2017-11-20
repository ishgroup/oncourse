package ish.oncourse.test.context

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

    static CTutor instance(ObjectContext context, Contact contact){
        CTutor builder = new CTutor()
        builder.objectContext = context

        builder.tutor = builder.objectContext.newObject(Tutor)
        builder.tutor.contact = contact
        builder.tutor.college = contact.college

        builder
    }
}
