package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.common.types.EnrolmentStatus
import ish.oncourse.model.Contact

class UsiRequired {
    private Contact contact

    UsiRequired(Contact contact) {
        this.contact = contact
    }

    boolean get() {
        if (contact.student && !contact.student.usi) {
            def now = new Date()
            return !contact.student.enrolments
                    .findAll { e ->
                e.courseClass.course.isVETCourse &&
                        EnrolmentStatus.SUCCESS == e.status &&
                        use(TimeCategory) {
                            (e.courseClass.endDate ?
                                    (e.courseClass.endDate + 1.year).after(now) :
                                    (e.createdOn + 1.year).after(now)
                            )
                        }
            }.empty
        } else {
            return false
        }

    }
}
