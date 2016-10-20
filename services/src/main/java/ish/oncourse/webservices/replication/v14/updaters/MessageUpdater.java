package ish.oncourse.webservices.replication.v14.updaters;

import ish.oncourse.model.Message;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v14.stubs.replication.MessageStub;

public class MessageUpdater extends AbstractWillowUpdater<MessageStub, Message> {

	@Override
	protected void updateEntity(MessageStub stub, Message entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setEmailBody(stub.getEmailBody());
		entity.setEmailSubject(stub.getEmailSubject());
		entity.setModified(stub.getModified());
		entity.setSmsText(stub.getSmsText());
	}
}
