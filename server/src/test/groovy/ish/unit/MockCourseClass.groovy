/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.unit

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import org.apache.cayenne.DataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId

@CompileStatic
class MockCourseClass extends CourseClass {

    /**
     *
     */
    private static final long serialVersionUID = 1L
    private int id

    MockCourseClass(int id, ObjectContext objectContext) {
        this.id = id
        setObjectContext(objectContext)
    }

    @Override
    protected void setReverseRelationship(String relName, DataObject val) {
        // must ignore now
    }

    /**
     * @see org.apache.cayenne.CayenneDataObject#getObjectId()
     */
    @Override
    ObjectId getObjectId() {
        return new ObjectId("CourseClass", "id", this.id)
    }

    static MockCourseClass newInstance(ObjectContext oc, int id, String code, int maxPlaces, Course course) {
        MockCourseClass courseClass = new MockCourseClass(id, oc)
        // ---- translate fields
        // common fields
        courseClass.writeProperty("id", id)
        courseClass.setCreatedOn(new Date())
        courseClass.setModifiedOn(new Date())
        courseClass.setMaximumPlaces(maxPlaces)
        courseClass.setCode(code)
        courseClass.setCourse(course)
        return courseClass
    }
}
