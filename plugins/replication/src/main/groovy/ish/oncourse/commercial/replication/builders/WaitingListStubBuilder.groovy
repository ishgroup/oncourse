/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.webservices.v23.stubs.replication.WaitingListStub

/**
 */
class WaitingListStubBuilder extends AbstractAngelStubBuilder<WaitingList, WaitingListStub> {
	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected WaitingListStub createFullStub(WaitingList w) {
		def stub = new WaitingListStub()
		stub.setCourseId(w.getCourse().getId())
		stub.setCreated(w.getCreatedOn())
		stub.setDetail(w.getStudentNotes())
		stub.setModified(w.getModifiedOn())
		stub.setStudentCount(w.getStudentCount())
		stub.setStudentId(w.getStudent().getId())
		return stub
	}
}
