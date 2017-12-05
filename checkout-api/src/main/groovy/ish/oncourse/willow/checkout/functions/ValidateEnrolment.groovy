package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.model.Student
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.Preferences
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import java.time.temporal.ChronoUnit

@CompileStatic
class ValidateEnrolment extends Validate<Enrolment>{
    
    ValidateEnrolment(ObjectContext context, College college) {
        super(context, college)
    }

    @Override
    ValidateEnrolment validate(Enrolment enrolment) {

        validate(new GetCourseClass(context, college, enrolment.classId).get(), new GetContact(context, college, enrolment.contactId).get().student)
    }

    ValidateEnrolment validate(CourseClass courseClass, Student student) {

        if (courseClass.cancelled) {
            errors << "Unfortunately ${getClassName(courseClass)} class  was canceled. Please return to website and select another class or add yourself to the waiting list.".toString()
            this
        }

        if (!courseClass.isWebVisible || !courseClass.isActive) {
            errors << "Unfortunately class ${getClassName(courseClass)} is not available for enrolling. Please return to website and select another class or add yourself to the waiting list.".toString()
            this
        }
        
        if (!ish.oncourse.model.Enrolment.STUDENT.eq(student).filterObjects(courseClass.validEnrolments).isEmpty()){
            errors << "$student.contact.fullName is already enrolled in this class. Please select another class from <a href=\"/course/$courseClass.course.code\"> this course</a>.".toString()
            this
        }
        
        if (!courseClass.hasAvailableEnrolmentPlaces){
            errors << "Unfortunately you just missed out. The class ${getClassName(courseClass)} was removed from your shopping basket since the last place has now been filled. Please select another class from this course or join the waiting list. <a href=\"/course/$courseClass.course.code\">[ Show course ]</a>".toString()
            this
        }

        String  age = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE, context).value
        String type = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE, context).value
        if (!new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(age, type)).check()) {
            errors << "Unfortunately you just missed out. The class ${getClassName(courseClass)} was removed from your shopping basket since enrolments are now closed for that class. Please select another class from this course or join the waiting list. <a href=\"/course/$courseClass.course.code\">[ Show course ]</a>".toString()
            this
        }
      
        validateDateOfBirth(courseClass, student)

        this
    }



    private validateDateOfBirth(CourseClass courseClass, Student student) {
        Date dateOfBirth = student.contact.dateOfBirth
        if (dateOfBirth != null)
        {
            Integer minEnrolmentAge = courseClass.minStudentAge
            Integer maxEnrolmentAge = courseClass.maxStudentAge

            String value = new GetPreference(college, Preferences.ENROLMENT_MIN_AGE, context).value
            Integer globalMinEnrolmentAge = value != null && StringUtils.isNumeric(value) ? Integer.valueOf(value) : 0

            Integer age = ChronoUnit.YEARS.between(dateOfBirth.toInstant(), new Date().toInstant()).toInteger()

            if (minEnrolmentAge != null || maxEnrolmentAge != null) {
                if ((minEnrolmentAge != null && age < minEnrolmentAge)) {
                    errors << "The minimum age for this class is $minEnrolmentAge. $student.contact.fullName is too young to enrol.".toString()
                } else if ((maxEnrolmentAge != null && age > maxEnrolmentAge)) {
                    errors << "The maximum age for this class is $maxEnrolmentAge. $student.contact.fullName is too old to enrol. If you intended to enrol your child, please click \"add another student\" below.".toString()
                }
            } else if (globalMinEnrolmentAge != null && age < globalMinEnrolmentAge) {
                errors << "The minimum age for this class is $globalMinEnrolmentAge. $student.contact.fullName is too young to enrol.".toString()
            }
        }
        this
    }

    private String getClassName(CourseClass courseClass) {
        "$courseClass.course.name ($courseClass.course.code-$courseClass.code)"
    }

}
