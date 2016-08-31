package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.portal.services.IPortalService
import ish.oncourse.services.courseclass.CourseClassFilter

class GetClassToApproval {
	
	def Contact contact
	def IPortalService portalService
	def CourseClass courseClass
	
	
	def GetClassToApproval(Contact contact, IPortalService portalService) {
		this.contact = contact
		this.portalService = portalService
	}
	
	def CourseClass get() {
		if (!contact || !contact.tutor) {
			return null
		} else if (!courseClass) {
			List<CourseClass> unapprovalClasses = portalService.getTutorCourseClasses(contact, CourseClassFilter.UNCONFIRMED);
			if (!unapprovalClasses.isEmpty()) {
				courseClass = CourseClass.START_DATE.asc().orderedList(unapprovalClasses).get(0);
			}
		}
		return courseClass
		
	}
	
}
