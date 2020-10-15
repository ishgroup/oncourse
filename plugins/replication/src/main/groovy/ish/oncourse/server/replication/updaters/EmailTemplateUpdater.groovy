/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.webservices.v21.stubs.replication.EmailTemplateStub

class EmailTemplateUpdater extends AbstractAngelUpdater<EmailTemplateStub, EmailTemplate> {
	@Override
	protected void updateEntity(EmailTemplateStub stub, EmailTemplate entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setEntity(stub.getEntity())
		entity.setName(stub.getName())
		entity.setSubject(stub.getSubject())
		entity.setBodyHtml(stub.getBodyHtml())
		entity.setBodyPlain(stub.getBodyPlain())
	}
}
