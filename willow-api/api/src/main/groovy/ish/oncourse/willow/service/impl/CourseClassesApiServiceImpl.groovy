package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.Application
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
import ish.oncourse.services.preference.Preferences
import ish.oncourse.willow.checkout.functions.BuildClassPrice
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.model.web.*
import ish.oncourse.willow.service.CourseClassesApi
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SelectById

import java.time.ZoneOffset

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION

@CompileStatic
class CourseClassesApiServiceImpl implements CourseClassesApi {

    private CayenneService cayenneService
    private CollegeService collegeService


    @Inject
    CourseClassesApiServiceImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }
    
    List<CourseClass> getCourseClasses(CourseClassesParams courseClassesParams) {

        College college = collegeService.college
        ObjectContext context = cayenneService.newContext()
        List<CourseClass> result = []
        List<ish.oncourse.model.Discount> promotions = []
        Contact contact

        if (courseClassesParams.contact?.id) {
            contact = SelectById.query(Contact, courseClassesParams.contact.id)
                    .prefetch(Contact.STUDENT.joint())
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                    .cacheGroup(Contact.class.simpleName)
                    .selectOne(context)
        }

        if (courseClassesParams.promotions) {
            promotions = (ObjectSelect.query(ish.oncourse.model.Discount)
                    .where(ExpressionFactory.
                    inDbExp(ish.oncourse.model.Discount.ID_PK_COLUMN, courseClassesParams.promotions*.id)) & ish.oncourse.model.Discount.COLLEGE.eq(college))
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                    .cacheGroup(ish.oncourse.model.Discount.class.simpleName)
                    .select(context)
        }

        (ObjectSelect.query(ish.oncourse.model.CourseClass)
                .where(ExpressionFactory.inDbExp(ish.oncourse.model.CourseClass.ID_PK_COLUMN, courseClassesParams.courseClassesIds)) & ish.oncourse.model.CourseClass.COLLEGE.eq(college))
                .prefetch(ish.oncourse.model.CourseClass.COLLEGE.joint())
                .prefetch(ish.oncourse.model.CourseClass.COURSE.joint())
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroup(ish.oncourse.model.CourseClass.class.simpleName)
                .select(cayenneService.newContext())
                .each { c ->

                    boolean allowByApplication = false
                    Money overridenFee = null
            
                    if (ENROLMENT_BY_APPLICATION == c.course.enrolmentType) {
                        if (contact?.student) {
                            Application application = new FindOfferedApplication(c.course, contact.student, context).get()
                            if (application != null) {
                                overridenFee = application.feeOverride
                                allowByApplication = false
                            }
                        } else {
                            overridenFee = null
                            allowByApplication = true
                        }
                    }
            
                    result << new CourseClass().with {
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
                        it.hasAvailablePlaces = hasAvailablePlaces(c)
                        it.isFinished = !c.cancelled && c.hasEnded()
                        it.isCancelled = c.cancelled
                        it.distantLearning = c.isDistantLearningCourse
                        it.isAllowByApplication = allowByApplication
                        it.isPaymentGatewayEnabled = new IsPaymentGatewayEnabled(c.college, c.objectContext).get()
                        it.price =  new BuildClassPrice(c, allowByApplication, overridenFee, promotions).build()
                        it
                    }
        }
        result
    }
    
    private static boolean hasAvailablePlaces(ish.oncourse.model.CourseClass courseClass) {
        String  age = new GetPreference(courseClass.college, Preferences.STOP_WEB_ENROLMENTS_AGE, courseClass.objectContext).getValue()
        String type = new GetPreference(courseClass.college, Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE, courseClass.objectContext).getValue()
        return courseClass.isHasAvailableEnrolmentPlaces() && new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(age, type)).check()
    }
}

