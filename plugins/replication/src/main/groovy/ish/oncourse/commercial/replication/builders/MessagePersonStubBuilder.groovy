/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.webservices.v21.stubs.replication.MessagePersonStub

/**
 */
class MessagePersonStubBuilder extends AbstractAngelStubBuilder<MessagePerson, MessagePersonStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected MessagePersonStub createFullStub(MessagePerson mp) {
		def stub = new MessagePersonStub()
		if (mp.getContact() != null) {
			stub.setContactId(mp.getContact().getId())
		}
		stub.setCreated(mp.getCreatedOn())
		stub.setDestinationAddress(mp.getDestinationAddress())
		stub.setMessageId(mp.getMessage().getId())
		stub.setModified(mp.getModifiedOn())
		stub.setNumberOfAttempts(mp.getAttemptCount())
		stub.setResponse(mp.getResponse())
		stub.setStatus(mp.getStatus().getDatabaseValue())
		stub.setType(mp.getType().getDatabaseValue())
		return stub
	}
}
