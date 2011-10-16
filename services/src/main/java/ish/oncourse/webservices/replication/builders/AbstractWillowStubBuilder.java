package ish.oncourse.webservices.replication.builders;

import org.apache.log4j.Logger;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends ReplicationStub> implements
		IWillowStubBuilder {

	private static final Logger logger = Logger.getLogger(AbstractWillowStubBuilder.class);

	/**
	 * @see IWillowStubBuilder#convert(QueuedRecord)
	 */
	public ReplicationStub convert(QueuedRecord queuedRecord) {

		ReplicationStub soapStub = null;

		switch (queuedRecord.getAction()) {
		case CREATE:
		case UPDATE:
			@SuppressWarnings("unchecked")
			T entity = (T) queuedRecord.getLinkedRecord();
			if (entity == null) {
				String errorMessage = "There is no record with id " + queuedRecord.getEntityWillowId() + " in table "
						+ queuedRecord.getEntityIdentifier();
				logger.warn(errorMessage);
				queuedRecord.setErrorMessage(errorMessage);
				queuedRecord.getObjectContext().commitChanges();
				return null;
			}
			soapStub = convert(entity);
			soapStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());
			break;
		case DELETE:
			soapStub = new DeletedStub();
			soapStub.setWillowId(queuedRecord.getEntityWillowId());
			soapStub.setAngelId(queuedRecord.getAngelId());
			soapStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());
			break;
		default:
			String errorMessage = "QueuedRecord with null action is not allowed.";
			queuedRecord.setErrorMessage(errorMessage);
			queuedRecord.getObjectContext().commitChanges();
			throw new IllegalArgumentException(errorMessage);
		}

		return soapStub;
	}

	/**
	 * @see IWillowStubBuilder#convert(Queueable)
	 */
	@Override
	public ReplicationStub convert(Queueable entity) {
		@SuppressWarnings("unchecked")
		V soapStub = createFullStub((T) entity);

		soapStub.setWillowId(entity.getId());
		soapStub.setAngelId(entity.getAngelId());
		soapStub.setEntityIdentifier(entity.getObjectId().getEntityName());

		return soapStub;
	}

	protected abstract V createFullStub(T entity);
}
