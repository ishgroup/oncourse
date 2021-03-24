package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetCourse extends Get<Course>{
    final static  Logger logger = LoggerFactory.getLogger(GetCourse.class)

    GetCourse(ObjectContext context, College college, String id) {
        super(context, college, id)
    }

    @Override
    Course get() {
        if (!StringUtils.trimToNull(id)) {
            logger.error("course Id required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'course id required')).build())
        }
        Course course = ((ObjectSelect.query(Course)
                .where(ExpressionFactory.matchDbExp(Course.ID_PK_COLUMN, id)) & Course.COLLEGE.eq(college)) & Course.IS_WEB_VISIBLE.eq(Boolean.TRUE))
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .cacheGroup(Course.class.simpleName)
                .selectOne(context)
        if (!course) {
            logger.error("Course is not exist, id:$id collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Course is not exist')).build())
        }
        course    
    }
}
