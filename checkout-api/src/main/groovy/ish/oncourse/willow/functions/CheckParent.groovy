package ish.oncourse.willow.functions

import ish.oncourse.model.College
import ish.oncourse.willow.model.web.Contact
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState
class CheckParent {


    private College college
    private ObjectContext context
    private ish.oncourse.model.Contact contact

    CheckParent(College college, ObjectContext context, ish.oncourse.model.Contact contact) {
        this.college = college
        this.context = context
        this.contact = contact
    }
    
    private boolean parentRequired = false
    private Contact parent = null

    CheckParent perform() {
        parentRequired = new IsParentRequired(college, context, contact).get()
        
        if (parentRequired && contact.persistenceState != PersistenceState.NEW) {
            parent = new GetParent(college, context, contact).get()
        }
        
        return this
    }


    boolean getParentRequired() {
        return parentRequired
    }

    Contact getParent() {
        return parent
    }
}
