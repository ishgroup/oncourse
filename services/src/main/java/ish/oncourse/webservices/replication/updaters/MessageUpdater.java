package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Message;
import ish.oncourse.webservices.v4.stubs.replication.MessageStub;

public class MessageUpdater extends AbstractWillowUpdater<MessageStub, Message> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater#
	 * updateEntity
	 * (ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 * ish.oncourse.model.Queueable,
	 * ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	protected void updateEntity(MessageStub stub, Message entity,
			RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setEmailBody(stub.getEmailBody());
		entity.setEmailSubject(stub.getEmailSubject());
		entity.setModified(stub.getModified());
		entity.setSmsText(stub.getSmsText());
	}
}
