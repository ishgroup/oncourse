package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetCourseClass extends Get<CourseClass> {
    final static  Logger logger = LoggerFactory.getLogger(GetContact.class)

    GetCourseClass(ObjectContext context, College college, String id) {
        super(context, college, id)
    }

    CourseClass get() {
        if (!StringUtils.trimToNull(id)) {
            logger.error("classId required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'classId required')).build())
        }

        CourseClass courseClass = (ObjectSelect.query(CourseClass).where(ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, id )) & CourseClass.COLLEGE.eq(college)).selectOne(context)
        if (!courseClass) {
            logger.error("Course Class is not exist, course class id: $id, collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Course Class is not exist')).build())
        }
        courseClass
    }
}
