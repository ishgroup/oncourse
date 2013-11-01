package ish.oncourse.webservices.replication.v4.builders;

import org.apache.log4j.Logger;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.GenericReplicationStub;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends GenericReplicationStub> implements IWillowStubBuilder {
	protected static final Logger logger = Logger.getLogger(AbstractWillowStubBuilder.class);

	/**
	 * @see IWillowStubBuilder#convert(QueuedRecord)
	 */
	public GenericReplicationStub convert(final QueuedRecord queuedRecord, final SupportedVersions version) {
		GenericReplicationStub soapStub;
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
			soapStub = convert(entity, version);
			soapStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());
			break;
		case DELETE:
			soapStub = PortHelper.createDeleteStub(version);
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
	public GenericReplicationStub convert(final Queueable entity, final SupportedVersions version) {
		@SuppressWarnings("unchecked")
		V soapStub = createFullStub((T) entity);
		soapStub.setWillowId(entity.getId());
		soapStub.setAngelId(entity.getAngelId());
		soapStub.setEntityIdentifier(entity.getObjectId().getEntityName());
		soapStub.setCreated(entity.getCreated());
		return soapStub;
	}

	protected abstract V createFullStub(T entity);
}
