package ish.oncourse.willow.portal.service.impl

import ish.oncourse.willow.portal.v1.model.ClassAttendanceItem
import ish.oncourse.willow.portal.v1.model.CourseClass
import ish.oncourse.willow.portal.v1.service.CourseClassApi

class CourseClassApiImpl implements CourseClassApi {
    @Override
    CourseClass getClass(String classId) {
        return null
    }

    @Override
    void markAttendance(ClassAttendanceItem item) {

    }
}
