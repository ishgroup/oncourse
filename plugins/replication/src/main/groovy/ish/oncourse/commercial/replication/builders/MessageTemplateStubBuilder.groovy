/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.MessageTemplate
import ish.oncourse.webservices.v21.stubs.replication.MessageTemplateStub

/**
 */
class MessageTemplateStubBuilder extends AbstractAngelStubBuilder<MessageTemplate, MessageTemplateStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected MessageTemplateStub createFullStub(MessageTemplate mt) {
		def stub = new MessageTemplateStub()
		stub.setCreated(mt.getCreatedOn())
		stub.setMessage(mt.getMessage())
		stub.setModified(mt.getModifiedOn())
		stub.setName(mt.getName())
		stub.setType(mt.getType().getDatabaseValue())
		stub.setSubject(mt.getSubject())
		return stub
	}
}
