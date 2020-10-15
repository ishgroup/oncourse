/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.webservices.v21.stubs.replication.EmailTemplateStub

class EmailTemplateStubBuilder extends AbstractAngelStubBuilder<EmailTemplate, EmailTemplateStub> {
	@Override
	protected EmailTemplateStub createFullStub(EmailTemplate entity) {
		def stub = new EmailTemplateStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setEntity(entity.getEntity())
		stub.setName(entity.getName())
		stub.setSubject(entity.getSubject())
		stub.setBodyHtml(entity.getBodyHtml())
		stub.setBodyPlain(entity.getBodyPlain())
		return stub
	}
}
