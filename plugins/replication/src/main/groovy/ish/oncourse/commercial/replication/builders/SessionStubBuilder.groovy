/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Session
import ish.oncourse.webservices.v23.stubs.replication.SessionStub

/**
 */
class SessionStubBuilder extends AbstractAngelStubBuilder<Session, SessionStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected SessionStub createFullStub(Session entity) {
		def stub = new SessionStub()
		stub.setCourseClassId(entity.getCourseClass().getId())
		stub.setEndDate(entity.getEndDatetime())
		stub.setPayAdjustment(entity.getPayAdjustment())
		stub.setRoomId(entity.getRoom() != null ? entity.getRoom().getId() : null)
		stub.setStartDate(entity.getStartDatetime())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setPublicNotes(entity.getPublicNotes())
		stub.setPrivateNotes(entity.getPrivateNotes())
		return stub
	}
}
