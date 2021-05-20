/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.unit

import groovy.transform.CompileStatic
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.DataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.apache.cayenne.Persistent

@CompileStatic
class MockEnrolment extends Enrolment {

    /**
     *
     */
    private static final long serialVersionUID = 1L
    private int id

    MockEnrolment(int id, ObjectContext objectContext) {
        this.id = id
        setObjectContext(objectContext)
    }

    @Override
    protected void setReverseRelationship(String relName, DataObject val) {
        // must ignore now
    }

    @Override
    protected void willConnect(String relationshipName, Persistent object) {
        // must ignore now
    }

    /**
     * @see org.apache.cayenne.CayenneDataObject#getObjectId()
     */
    @Override
    ObjectId getObjectId() {
        return new ObjectId("Enrolment", "id", this.id)
    }

    static MockEnrolment newInstance(ObjectContext oc, int id, CourseClass aClass, Student aStudent) {
        MockEnrolment enrolment = new MockEnrolment(id, oc)

        // ---- translate fields
        // common fields
        enrolment.writeProperty("id", id)
        enrolment.setCreatedOn(new Date())
        enrolment.setModifiedOn(new Date())

        // specific
        enrolment.setStudent(aStudent)
        enrolment.setCourseClass(aClass)
        enrolment.setStatus(EnrolmentStatus.NEW)

        // System.out.println("created enrolment "+ToStringBuilder.reflectionToString(enrolment));
        return enrolment
    }
}