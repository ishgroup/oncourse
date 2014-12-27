package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.MessageTemplate;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.MessageTemplateStub;

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
