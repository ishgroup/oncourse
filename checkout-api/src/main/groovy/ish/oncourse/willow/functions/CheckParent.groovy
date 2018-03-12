package ish.oncourse.willow.functions

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState

@CompileStatic
class CheckParent {


    private College college
    private ObjectContext context
    private Contact contact
    private List<Contact> parents = new ArrayList<>()
    private boolean parentRequired = false

    CheckParent(College college, ObjectContext context, Contact contact) {
        this.college = college
        this.context = context
        this.contact = contact
    }
    
    CheckParent perform() {
        parentRequired = new IsParentRequired(college, context, contact).get()
        
        if (contact.persistenceState != PersistenceState.NEW) {
            parents = new GetParents(college, context, contact).get()
        }
        
        return this
    }
    
    boolean getParentRequired() {
        return parentRequired
    }

    List<Contact> getParents() {
        return parents
    }
}
