package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.Contact
import ish.oncourse.model.Session
import ish.oncourse.portal.services.IPortalService

class GetNextSession {
	
	def Contact contact
	def IPortalService portalService
	
	def GetNextSession(Contact contact, IPortalService portalService) {
		this.portalService = portalService
		this.contact = contact
	}
	
	def Session get() {
		if (!contact) {
			return null
		} else {
			List<Session> sessions = portalService.getContactSessionsFrom(new Date(), contact)
			if (!sessions.empty) {
				return sessions.get(0)
			}
			return null
		}
	}
}
