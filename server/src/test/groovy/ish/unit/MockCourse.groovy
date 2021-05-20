/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.unit

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Course
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId

@CompileStatic
class MockCourse extends Course {

    /**
     *
     */
    private static final long serialVersionUID = 1L
    private int id

    MockCourse(int id, ObjectContext objectContext) {
        this.id = id
        setObjectContext(objectContext)
    }

    /**
     * @see org.apache.cayenne.CayenneDataObject#getObjectId()
     */
    @Override
    ObjectId getObjectId() {
        return new ObjectId("Course", "id", this.id)
    }

    static MockCourse newInstance(ObjectContext oc, int id, String name, String code) {
        MockCourse course = new MockCourse(id, oc)

        // ---- translate fields
        // common fields
        course.writeProperty("id", id)
        course.setCreatedOn(new Date())
        course.setModifiedOn(new Date())
        course.setName(name)
        course.setCode(code)

        return course
    }
}
