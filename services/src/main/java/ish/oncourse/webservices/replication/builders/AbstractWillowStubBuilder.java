package ish.oncourse.webservices.replication.builders;

import ish.common.types.EntityMapping;
import ish.oncourse.model.*;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends GenericReplicationStub> implements IWillowStubBuilder {
	protected static final Logger logger = LogManager.getLogger();

	/**
	 * @see IWillowStubBuilder#convert(ish.oncourse.model.QueuedRecord, ish.oncourse.webservices.util.SupportedVersions) 
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
			if (entity instanceof BinaryInfoRelation) {
				soapStub.setEntityIdentifier(EntityMapping.getAttachmentRelationIdentifer(((BinaryInfoRelation) entity).getEntityIdentifier()));
			} else if (entity instanceof ContactCustomField){
				String customFieldIdentifier = getCustomFieldIdentifier(version);
				soapStub.setEntityIdentifier(customFieldIdentifier);
			} else {
				soapStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());
			}
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
	 * @see IWillowStubBuilder#convert(ish.oncourse.model.Queueable, ish.oncourse.webservices.util.SupportedVersions) 
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
	
	
	private String getCustomFieldIdentifier(SupportedVersions version) {
		switch (version) {
			case V10:
			case V14:
				return CustomField.class.getSimpleName();
			case V15:
			case V16:
			default:
				return ContactCustomField.class.getSimpleName();
		}
	}
}
