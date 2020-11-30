package ish.oncourse.test.context

import ish.common.types.CourseEnrolmentType
import ish.oncourse.model.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

/**
 * Created by alex on 11/17/17.
 */
class CCourse {
    private ObjectContext objectContext
    Course course

    List<CCourseClass> classes = new LinkedList<>()

    CCourse load() {
        ObjectSelect.query(CourseClass).where(Course.COLLEGE.eq(course.college)).select(objectContext).forEach { cc ->
            classes.add(new CCourseClass().with {
                it.objectContext = this.objectContext
                it.courseClass = cc
                it
            }.load())
        }
        return this
    }

    CCourse relatedTo(CProduct product, EntityRelationType relationType) {
        EntityRelation relation = new EntityRelation()
        relation.setRelationType(relationType)
        relation.setCollege(this.course.college)
        relation.setFromEntityWillowId(this.course.id)
        relation.setFromEntityIdentifier("Product")
        relation.setToEntityWillowId(product.product.id)
        relation.setToEntityIdentifier("Course")
        relation.setCreated(new Date())
        relation.setModified(new Date())
        return this
    }

    CCourse relatedFrom(CCourse from, EntityRelationType relationType) {
        EntityRelation relation = new EntityRelation()
        relation.setRelationType(relationType)
        relation.setToEntityWillowId(this.course.id)
        relation.setToEntityIdentifier("Course")
        relation.setFromEntityWillowId(from.course.id)
        relation.setFromEntityIdentifier("Course")
        relation.setCollege(this.course.college)
        relation.setCreated(new Date())
        relation.setModified(new Date())
        return this
    }

    CCourse relatedTo(CCourse to, EntityRelationType relationType) {
        EntityRelation relation = new EntityRelation()
        relation.setRelationType(relationType)
        relation.setToEntityWillowId(to.course.id)
        relation.setToEntityIdentifier("Course")
        relation.setFromEntityWillowId(this.course.id)
        relation.setFromEntityIdentifier("Course")
        relation.setCollege(this.course.college)
        relation.setCreated(new Date())
        relation.setModified(new Date())
        return this
    }

    CCourseClass newCourseClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        classes.add(cClass)
        cClass
    }

    CCourseClass newCourseClassWithSessionsAndTutor(String code, Tutor tutor, Integer... sessionStartDatesFromNow) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        CTutorRole.instance(objectContext, tutor, cClass.courseClass)
        sessionStartDatesFromNow.each { s -> cClass.withSession(s) }

        classes.add(cClass)
        cClass
    }

    CCourseClass newCourseClassWithSessions(String code, Date... sessionStartDates) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        for (Date date : sessionStartDates) {
            cClass.withSession(date)
        }
        classes.add(cClass)
        cClass
    }

    CCourseClass newCourseClassWithSessions(String code, Integer... sessionStartDatesFromNow) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        sessionStartDatesFromNow.each { day -> cClass.withSession(new Date() + day) }
        classes.add(cClass)
        cClass
    }

    CCourseClass newCourseClassWithSessionsAndSite(String code, CSite site, Integer... sessionStartDatesFromNow) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        sessionStartDatesFromNow.each { day -> cClass.withSessionAndSite(new Date() + day, site) }
        classes.add(cClass)
        cClass
    }

    /**
     * Creates class with 2 sessions, started now and in the next day
     * @param code
     * @return
     */
    CCourseClass newCourseClassWithSessions(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        cClass.withSession(new Date())
        cClass.withSession(new Date() + 1)

        classes.add(cClass)
        cClass
    }

    CCourseClass newSelfPacedClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isDistantLearningCourse(true)

        classes.add(cClass)
        cClass
    }

    /**
     * Creates class with 2 sessions, started now and in the next day
     * both sessions have 'String timezone' timezone
     * @param code
     * @return
     */
    CCourseClass newCourseClassWithTimezonedSessions(String code, String timezone = "Australia/Sydney") {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        cClass.withTimeZonedSession(new Date(), timezone)
        cClass.withTimeZonedSession(new Date() + 1, timezone)

        classes.add(cClass)
        cClass
    }

    /**
     * Creates class with 2 sessions, started now and in the next day
     * both sessions have 'String timezone' timezone
     * @param code
     * @return
     */
    CCourseClass newCourseClassWithTimezonedSessions(String code, String timezone = "Australia/Sydney", Date... sessionStartDates) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        sessionStartDates.each { s -> cClass.withTimeZonedSession(s, timezone) }

        classes.add(cClass)
        cClass
    }

    CCourseClass getCourseClassBy(String code) {
        return classes.stream().filter({ (it.courseClass.code == code) }).findFirst().get()
    }

    static CCourse instance(ObjectContext context, College college, String name, String code) {
        CCourse cCourse = new CCourse()

        cCourse.objectContext = context
        cCourse.course = cCourse.objectContext.newObject(Course)
        cCourse.course.college = cCourse.objectContext.localObject(college)
        cCourse.course.name = name
        cCourse.course.code = code
        cCourse.course.detail = name + " details"
        cCourse.course.enrolmentType = CourseEnrolmentType.OPEN_FOR_ENROLMENT
        cCourse.course.isWebVisible = true
        cCourse
    }

    CCourse withClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        classes.add(cClass)
        this
    }

    /**
     * class duration between start and end is 10 days for default    
     */
    CCourse withClass(String code, Date classStartDate) {
        withClass(code, classStartDate, 10)
    }

    CCourse withClass(String code, Date classStartDate, int durationDays) {
        withClass(code, classStartDate, classStartDate + durationDays)
        this
    }

    CCourse withClassWithSiteLocation(String code, BigDecimal longitude, BigDecimal latitude, Integer... sessionStartDatesFromNow) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        sessionStartDatesFromNow.each { s -> cClass.withSessionWithSiteLocation(new Date() + s, longitude, latitude) }
        classes.add(cClass)
        this
    }


    CCourse withSelfPacedClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isDistantLearningCourse(true)

        classes.add(cClass)
        this
    }

    CCourse withSelfPacedClassWithSite(String code, CSite site) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
                .isDistantLearningCourse(true).withRoom(site.site)
        classes.add(cClass)
        this
    }

    CCourse withSelfPacedClassAndTutor(String code, Tutor... tutors) {
        CourseClass clazz = newSelfPacedClass(code).courseClass
        tutors.each { t -> CTutorRole.instance(objectContext, t, clazz) }
        this
    }

    CCourse withClass(String code, Date startDate, Date endDate) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).startDate(startDate).endDate(endDate)
        classes.add(cClass)
        this
    }

    CCourse withCancelledClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).cancelled(true)
        classes.add(cClass)
        this
    }

    CCourse withDistantClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isDistantLearningCourse(true)
        classes.add(cClass)
        this
    }

    CCourse withInactiveClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).active(false)
        classes.add(cClass)
        this
    }

    CCourse withEnrolDisabledClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).active(false).isWebVisible(false)
        classes.add(cClass)
        this
    }

    CCourse withWebInvisibleClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isWebVisible(false)
        classes.add(cClass)
        this
    }

    CCourse detail(String detail) {
        course.setDetail(detail)
        this
    }

    CCourse isWebVisible(boolean isVisible) {
        course.isWebVisible = isVisible
        this
    }

    CCourse build() {
        objectContext.commitChanges()
        this
    }

    void delete() {
        this.classes.each { it.delete() }
        this.classes.clear()
        objectContext.deleteObjects(this.course.getToCourses())
        objectContext.deleteObject(this.course)
        objectContext.commitChanges()
    }
}
