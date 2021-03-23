package ish.oncourse.test.context

import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class CTutorRole {
    private ObjectContext objectContext
    TutorRole role

    private CTutorRole(){}

    CTutorRole build(){
        objectContext.commitChanges()
        this
    }

    static CTutorRole instance(ObjectContext context, Tutor tutor, CourseClass courseClass){
        CTutorRole builder = new CTutorRole()
        builder.objectContext = context

        builder.createDefaultRole(tutor, courseClass)
        builder
    }

    static CTutorRole instance(ObjectContext context, Contact contact, CourseClass courseClass){
        CTutorRole builder = new CTutorRole()
        builder.objectContext = context

        builder.createDefaultRole(contact, courseClass)
        builder
    }

    private CTutorRole createDefaultRole(Contact contact, CourseClass courseClass) {
        Tutor tutor = CTutor.instance(objectContext, contact).build().tutor
        createDefaultRole(tutor, courseClass)
        this
    }

    private CTutorRole createDefaultRole(Tutor tutor, CourseClass courseClass) {
        role = objectContext.newObject(TutorRole)
        role.tutor = tutor
        role.college = tutor.college
        role.courseClass = courseClass
        role.inPublicity = true
        objectContext.commitChanges()
        this
    }
}
