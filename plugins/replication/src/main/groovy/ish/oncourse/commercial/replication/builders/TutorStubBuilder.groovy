/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v22.stubs.replication.TutorStub

/**
 */
class TutorStubBuilder extends AbstractAngelStubBuilder<Tutor, TutorStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected TutorStub createFullStub(Tutor t) {
		def stub = new TutorStub()
		if (t.getContact() != null) {
			stub.setContactId(t.getContact().getId())
		}
		stub.setCreated(t.getCreatedOn())
		stub.setModified(t.getModifiedOn())
		stub.setResumeTextile(t.getResume())
		stub.setStartDate(t.getDateStarted())
		stub.setFinishDate(t.getDateFinished())
		return stub
	}
}
