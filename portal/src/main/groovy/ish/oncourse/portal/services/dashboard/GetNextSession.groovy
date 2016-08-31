package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.Contact
import ish.oncourse.model.Session
import ish.oncourse.portal.services.IPortalService

class GetNextSession {
	
	def Contact contact
	def IPortalService portalService
	def Session session
	
	def GetNextSession(Contact contact, IPortalService portalService) {
		this.portalService = portalService
		this.contact = contact
	}
	
	def Session get() {
		if (!contact) {
			return null
		} else if (!session) {
			List<Session> sessions = portalService.getContactSessionsFrom(new Date(), contact)
			if (!sessions.empty) {
				session = sessions.get(0)
			}
		}
		return session
	}
}
