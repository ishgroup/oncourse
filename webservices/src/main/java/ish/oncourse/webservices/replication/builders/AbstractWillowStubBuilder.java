package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends ReplicationStub>
		implements IWillowStubBuilder {

	public ReplicationStub convert(QueuedRecord queuedRecord) {

		ReplicationStub soapStub = null;

		switch (queuedRecord.getAction()) {
		case CREATE:
		case UPDATE:
			@SuppressWarnings("unchecked")
			T entity = (T) queuedRecord.getLinkedRecord();
			soapStub = createFullStub(entity);
			break;
		case DELETE:
			soapStub = new DeletedStub();
			break;
		default:
			throw new IllegalArgumentException(
					"QueuedRecord with null action is not allowed.");
		}

		soapStub.setWillowId(queuedRecord.getEntityWillowId());
		soapStub.setAngelId(queuedRecord.getAngelId());
		soapStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());

		return soapStub;
	}

	protected abstract V createFullStub(T entity);
}
