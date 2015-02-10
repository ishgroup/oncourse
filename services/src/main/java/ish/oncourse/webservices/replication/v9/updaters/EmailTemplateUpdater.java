/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.updaters;

import ish.oncourse.model.EmailTemplate;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.EmailTemplateStub;

public class EmailTemplateUpdater extends AbstractWillowUpdater<EmailTemplateStub, EmailTemplate> {
	@Override
	protected void updateEntity(EmailTemplateStub stub, EmailTemplate entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setEntity(stub.getEntity());
		entity.setName(stub.getName());
		entity.setSubject(stub.getSubject());
		entity.setBodyHtml(stub.getBodyHtml());
		entity.setBodyPlain(stub.getBodyPlain());
	}
}