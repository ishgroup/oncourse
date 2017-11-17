package ish.oncourse.entityBuilder

import ish.oncourse.model.Contact
import ish.oncourse.model.Tutor
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class TutorBuilder {
    private ObjectContext objectContext
    private Tutor tutor

    private TutorBuilder (ObjectContext context){
        objectContext = context
    }

    Tutor build(){
        objectContext.commitChanges()
        tutor
    }

    static TutorBuilder instance(ObjectContext context, Contact contact){
        TutorBuilder builder = new TutorBuilder(context)
        builder.createDefaultTutor(contact)
        builder
    }

    private createDefaultTutor(Contact contact){
        tutor = objectContext.newObject(Tutor)
        tutor.contact = contact
        tutor.college = contact.college
        this
    }
}
