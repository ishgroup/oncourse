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
import org.apache.cayenne.Persistent

import javax.annotation.Nonnull

@CompileStatic
class MockContact extends Contact {

    private static final long serialVersionUID = 1L
    private int id
    private Student student

    MockContact(int id, ObjectContext objectContext) {
        this.id = id
        setObjectContext(objectContext)
    }

    @Override
    void setStudent(Student student) {
        this.student = student
        if (student.getContact() == null || !student.getContact().equals(this)) {
            student.setContact(this)
        }

        setIsStudent(student != null)
    }

    @Nonnull
    @Override
    Student getStudent() {
        return this.student
    }

    /**
     * @see org.apache.cayenne.CayenneDataObject#getObjectId()
     */
    @Override
    ObjectId getObjectId() {
        return new ObjectId("Contact", "id", this.id)
    }

    @Override
    protected void setReverseRelationship(String relName, DataObject val) {
        // do nothing
    }

    @Override
    protected void willConnect(String relationshipName, Persistent object) {
        // do nothing
    }

}
