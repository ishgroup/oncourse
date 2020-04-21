package ish.oncourse.webservices

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.site.IWebSiteService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.tapestry5.ioc.annotations.Inject

class CheckoutVerificationService implements ICheckoutVerificationService {

    @Inject
    private IWebSiteService siteService

    @Inject
    private ICayenneService cayenneService


    @Override
    List<CheckoutValidationResult> verify(Map<Long, List<Long>> enrolmentsMap) {
        ObjectContext context = cayenneService.newContext()
        College college = siteService.currentCollege
        List<CheckoutValidationResult> result = []
        enrolmentsMap.each { courseClassId, contacts ->
            CourseClass courseClass = ObjectSelect.query(CourseClass)
                    .where(CourseClass.ANGEL_ID.eq(courseClassId))
                    .and(CourseClass.COLLEGE.eq(college))
                    .selectOne(context)
            if (courseClass) {
                String classLabel = "$courseClass.course.code ($courseClass.uniqueIdentifier)"
                List<Enrolment> validEnrolments = courseClass.validEnrolments
                int availPlaces = courseClass.getMaximumPlaces() - validEnrolments.size()
                if (availPlaces < contacts.size()) {
                    contacts.each { contactId ->
                        result << new CheckoutValidationResult(
                                courseClassId: courseClassId,
                                contactId: contactId,
                                error: "No places available for class $classLabel")
                    }
                } else {
                    contacts.each { contactId ->
                        Contact contact = ObjectSelect.query(Contact)
                                .where(Contact.ANGEL_ID.eq(courseClassId))
                                .and(Contact.COLLEGE.eq(college))
                                .selectOne(context)
                        if (contact && contact.student && validEnrolments.any {it.student.id == contact.student.id }) {
                            result << new CheckoutValidationResult(
                                    courseClassId: courseClassId,
                                    contactId: contactId,
                                    error: "Student $contact.fullName already has an active enrolment for class $classLabel and he/she cannot be enrolled twice. ")
                        }
                    }
                }
            }

        }
        return result
    }
}
