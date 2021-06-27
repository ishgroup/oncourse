/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.unit

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.DataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId

@CompileStatic
class MockStudent extends Student {

    /**
     *
     */
    private static final long serialVersionUID = 1L
    private int id
    private Contact contact

    MockStudent(int id, ObjectContext objectContext) {
        this.id = id
        setObjectContext(objectContext)
    }

    @Override
    void setContact(Contact contact) {
        this.contact = contact
        if (contact.getStudent() == null || !contact.getStudent().equals(this)) {
            contact.setStudent(this)
        }
    }

    @Override
    Contact getContact() {
        return this.contact
    }

    @Override
    protected void setReverseRelationship(String relName, DataObject val) {
        if (CONTACT_PROPERTY.equals(relName)) {
            if (val != null && val instanceof MockContact) {
                MockContact c = (MockContact) val
                if (getContact() == null || !getContact().equals(c)) {
                    setContact(c)
                }
            }
        }
    }

    /**
     * @see org.apache.cayenne.CayenneDataObject#getObjectId()
     */
    @Override
    ObjectId getObjectId() {
        return new ObjectId("Student", "id", this.id)
    }

    static MockStudent newInstance(ObjectContext oc, int id, String firstName, String lastName) {
        MockStudent result = new MockStudent(id, oc)
        result.writeProperty("id", id)

        Contact contact = new MockContact(id, oc)
        contact.writeProperty("id", id)
        contact.setStudent(result)
        result.getContact().setFirstName(firstName)
        result.getContact().setLastName(lastName)
        result.setCreatedOn(new Date())
        result.setModifiedOn(new Date())

        return result
    }
}