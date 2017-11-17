package ish.oncourse.entityBuilder

import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class TutorRoleBuilder {
    private ObjectContext objectContext
    private TutorRole role

    private TutorRoleBuilder (ObjectContext context){
        objectContext = context
    }

    TutorRole build(){
        objectContext.commitChanges()
        role
    }

    static TutorRoleBuilder instance(ObjectContext context, Tutor tutor, CourseClass courseClass){
        TutorRoleBuilder builder = new TutorRoleBuilder(context)
        builder.createDefaultRole(tutor, courseClass)
        builder
    }

    static TutorRoleBuilder instance(ObjectContext context, Contact contact, CourseClass courseClass){
        TutorRoleBuilder builder = new TutorRoleBuilder(context)
        builder.createDefaultRole(contact, courseClass)
        builder
    }

    private TutorRoleBuilder createDefaultRole(Tutor tutor, CourseClass courseClass) {
        role = objectContext.newObject(TutorRole)
        role.tutor = tutor
        role.college = tutor.college
        role.courseClass = courseClass
        role.inPublicity = true
        this
    }

    private TutorRoleBuilder createDefaultRole(Contact contact, CourseClass courseClass) {
        Tutor tutor = TutorBuilder.instance(objectContext, contact).build()
        role = objectContext.newObject(TutorRole)
        role.tutor = tutor
        role.college = courseClass.college
        role.courseClass = courseClass
        role.inPublicity = true
        this
    }
}
