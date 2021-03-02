package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.NodeSpecialType
import ish.math.Money
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.Application
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.model.TaggableTag
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.services.course.GetEnrollableClasses
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.willow.checkout.functions.BuildClassPrice
import ish.oncourse.willow.checkout.functions.GetCourse
import ish.oncourse.willow.model.web.*
import ish.oncourse.willow.service.CourseClassesApi
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SelectById

import java.time.ZoneOffset

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION
import static org.apache.cayenne.query.ObjectSelect.query

@CompileStatic
class CourseClassesApiServiceImpl implements CourseClassesApi {

    private CayenneService cayenneService
    private CollegeService collegeService


    @Inject
    CourseClassesApiServiceImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }

    List<CourseClass> getAvailableClasses(String courseId) {
        ObjectContext context = cayenneService.sharedContext()
        College college = context.localObject(collegeService.college)
        
        ish.oncourse.model.Course course = new GetCourse(context, college, courseId).get()
        return course.availableClasses.collect {toClassDTO(it)}
    }
    
    List<CourseClass> getCourseClasses(CourseClassesParams courseClassesParams) {
        ObjectContext context = cayenneService.sharedContext()
        College college = context.localObject(collegeService.college)

        List<CourseClass> result = []
        List<ish.oncourse.model.Discount> promotions = []
        Contact contact

        if (courseClassesParams.contact?.id) {
            contact = SelectById.query(Contact, courseClassesParams.contact.id)
                    .prefetch(Contact.STUDENT.joint())
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                    .cacheGroup(Contact.class.simpleName)
                    .selectOne(context)
        }

        if (courseClassesParams.promotions) {
            promotions = (ObjectSelect.query(ish.oncourse.model.Discount)
                    .where(ExpressionFactory.
                    inDbExp(ish.oncourse.model.Discount.ID_PK_COLUMN, courseClassesParams.promotions*.id)) & ish.oncourse.model.Discount.COLLEGE.eq(college))
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                    .cacheGroup(ish.oncourse.model.Discount.class.simpleName)
                    .select(context)
        }

        List<ish.oncourse.model.CourseClass> classes = (ObjectSelect.query(ish.oncourse.model.CourseClass)
                .where(ExpressionFactory.inDbExp(ish.oncourse.model.CourseClass.ID_PK_COLUMN, courseClassesParams.courseClassesIds)) & ish.oncourse.model.CourseClass.COLLEGE.eq(college))
                .prefetch(ish.oncourse.model.CourseClass.COURSE.joint())
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .cacheGroup(ish.oncourse.model.CourseClass.class.simpleName)
                .select(context)

        classes.each { c ->

            boolean allowByApplication = false
            Money overridenFee = null

            if (ENROLMENT_BY_APPLICATION == c.course.enrolmentType) {
                if (contact?.student) {
                    Application application = new FindOfferedApplication(c.course, contact.student, context, QueryCacheStrategy.LOCAL_CACHE).get()
                    if (application != null) {
                        overridenFee = application.feeOverride
                        allowByApplication = false
                    } else {
                        allowByApplication = true
                    }
                    
                } else {
                    overridenFee = null
                    allowByApplication = true
                }
            }

            result << toClassDTO(c, allowByApplication, overridenFee, promotions, contact)
        }
        result
    }

    @Override
    List<Course> getCourses(CoursesParams coursesParams) {
        ObjectContext context = cayenneService.sharedContext()
        College college = context.localObject(collegeService.college)

        List<Course> result = []
        coursesParams.coursesIds.each { id ->
            ish.oncourse.model.Course course = new GetCourse(context, college, id).get()
            result << new Course().with {
                it.id = course.id.toString()
                it.code = course.code
                it.name = course.name
                List<ish.oncourse.model.CourseClass> classes = GetEnrollableClasses.valueOf(course).get()
                it.hasCurrentClasses = !classes.empty
                it.hasMoreAvailablePlaces = classes.find { it.hasAvailableEnrolmentPlaces } != null
                it
            }
        }
        result
    }

    private String getSubject(ish.oncourse.model.Course course) {
        query(Tag)
                .where(Tag.TAGGABLE_TAGS.outer().dot(TaggableTag.TAGGABLE).outer().dot(Taggable.ENTITY_IDENTIFIER).eq(ish.oncourse.model.Course.simpleName))
                .and(Tag.TAGGABLE_TAGS.outer().dot(TaggableTag.TAGGABLE).outer().dot(Taggable.ENTITY_WILLOW_ID).eq(course.id))
                .select(cayenneService.newContext())
                .find { NodeSpecialType.SUBJECTS == it.root.specialType }?.name
    }
    
    private CourseClass toClassDTO(ish.oncourse.model.CourseClass c, Boolean allowByApplication = false, Money overridenFee = null, List<ish.oncourse.model.Discount> promotions = [], Contact contact = null ) {
        new CourseClass().with {
            it.id = c.id.toString()
            it.code = c.code
            it.course = new Course().with {
                it.id = c.course.id.toString()
                it.code = c.course.code
                it.name = c.course.name
                it
            }

            it.availableEnrolmentPlaces = c.availableEnrolmentPlaces

            if (c.room?.site?.isWebVisible) {
                it.room = new Room().with { r ->
                    r.name = c.room.name
                    r.site = new Site().with { s ->
                        s.name = c.room.site.name
                        s.street = c.room.site.street
                        s.suburb = c.room.site.suburb
                        s.postcode = c.room.site.postcode
                        s
                    }
                    r
                }
            }

            it.start = c.startDateTime?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            it.end = c.endDateTime?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            it.hasAvailablePlaces = c.hasAvailableEnrolmentPlaces && new CheckClassAge().college(collegeService.college).courseClass(c).check()
            it.isFinished = !c.cancelled && c.hasEnded()
            it.isCancelled = c.cancelled
            it.distantLearning = c.isDistantLearningCourse
            it.isAllowByApplication = allowByApplication
            it.isPaymentGatewayEnabled = true//new IsPaymentGatewayEnabled(college, c.objectContext).get()
            it.price =  new BuildClassPrice(c, allowByApplication, overridenFee, promotions, contact).build()
            it.timezone = c.timeZone
            it.subject = getSubject(c.course)
            it
        }
        
    }
}

