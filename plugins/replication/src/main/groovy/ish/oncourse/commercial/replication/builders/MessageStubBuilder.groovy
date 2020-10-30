/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Message
import ish.oncourse.webservices.v22.stubs.replication.MessageStub

/**
 */
class MessageStubBuilder extends AbstractAngelStubBuilder<Message, MessageStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected MessageStub createFullStub(Message m) {
		def stub = new MessageStub()
		stub.setCreated(m.getCreatedOn())
		stub.setEmailBody(m.getEmailBody())
		stub.setEmailSubject(m.getEmailSubject())
		stub.setModified(m.getModifiedOn())
		stub.setSmsText(m.getSmsText())
		return stub
	}
}
