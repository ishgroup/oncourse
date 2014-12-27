/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.ContactRelation;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.ContactRelationStub;

/**
 * @author akoyro
 */
public class ContactRelationStubBuilder extends AbstractWillowStubBuilder<ContactRelation, ContactRelationStub> {

	@Override
	protected ContactRelationStub createFullStub(final ContactRelation entity) {
		final ContactRelationStub stub = new ContactRelationStub();
		stub.setFromContactId(entity.getFromContact().getId());
		stub.setModified(entity.getModified());
		stub.setToContactId(entity.getToContact().getId());
		stub.setTypeId(entity.getRelationType().getId());
		return stub;
	}
}
