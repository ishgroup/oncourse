package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.MessageTemplate;
import ish.oncourse.webservices.v4.stubs.replication.MessageTemplateStub;

public class MessageTemplateUpdater extends AbstractWillowUpdater<MessageTemplateStub, MessageTemplate> {

	/* (non-Javadoc)
	 * @see ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater#updateEntity(ish.oncourse.webservices.v4.stubs.replication.ReplicationStub, ish.oncourse.model.Queueable, ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	protected void updateEntity(MessageTemplateStub stub,
			MessageTemplate entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setMessage(stub.getMessage());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setSubject(stub.getSubject());
	}
}
