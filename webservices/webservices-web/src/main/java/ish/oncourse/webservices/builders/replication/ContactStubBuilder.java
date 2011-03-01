package ish.oncourse.webservices.builders.replication;

import java.util.Map;

import ish.oncourse.model.Contact;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ContactStub;

public class ContactStubBuilder extends AbstractWillowStubBuilder<Contact, ContactStub> {
	
	public ContactStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}

	@Override
	protected ContactStub createFullStub(Contact entity) {
		return null;
	}
}
