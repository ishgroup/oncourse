/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.EmailTemplate;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v9.stubs.replication.EmailTemplateStub;

public class EmailTemplateStubBuilred extends AbstractWillowStubBuilder<EmailTemplate, EmailTemplateStub> {
	
	@Override
	protected EmailTemplateStub createFullStub(EmailTemplate entity) {
		EmailTemplateStub stub = new EmailTemplateStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setEntity(entity.getEntity());
		stub.setName(entity.getName());
		stub.setSubject(entity.getSubject());
		stub.setBodyHtml(entity.getBodyHtml());
		stub.setBodyPlain(entity.getBodyPlain());
		return stub;
	}
}
