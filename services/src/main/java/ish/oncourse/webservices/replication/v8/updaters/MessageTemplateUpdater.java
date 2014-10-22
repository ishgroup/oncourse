package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.MessageTemplate;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.MessageTemplateStub;

public class MessageTemplateUpdater extends AbstractWillowUpdater<MessageTemplateStub, MessageTemplate> {

	@Override
	protected void updateEntity(MessageTemplateStub stub, MessageTemplate entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setMessage(stub.getMessage());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setSubject(stub.getSubject());
	}
}
