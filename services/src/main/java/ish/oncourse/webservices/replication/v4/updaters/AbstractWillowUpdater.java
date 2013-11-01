package ish.oncourse.webservices.replication.v4.updaters;

import ish.oncourse.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ish.oncourse.webservices.util.GenericReplicationStub;

public abstract class AbstractWillowUpdater<V extends GenericReplicationStub, T extends Queueable> implements IWillowUpdater {
	protected static final Logger LOG = Logger.getLogger(AbstractWillowUpdater.class);
	protected static final String CONTACT_ENTITY_NAME = Contact.class.getSimpleName();
	protected static final String COURSE_ENTITY_NAME = Course.class.getSimpleName();
	protected static final String COURSE_CLASS_ENTITY_NAME = CourseClass.class.getSimpleName();
	protected static final String CERTIFICATE_ENTITY_NAME = Certificate.class.getSimpleName();
	protected static final String ENROLMENT_ENTITY_NAME = Enrolment.class.getSimpleName();
	protected static final String INVOICE_ENTITY_NAME = Invoice.class.getSimpleName();
	protected static final String ROOM_ENTITY_NAME = Room.class.getSimpleName();
	protected static final String SESSION_ENTITY_NAME = Session.class.getSimpleName();
	protected static final String SITE_ENTITY_NAME = Site.class.getSimpleName();
	protected static final String STUDENT_ENTITY_NAME = Student.class.getSimpleName();
	protected static final String TUTOR_ENTITY_NAME = Tutor.class.getSimpleName();
	protected static final String TAG_ENTITY_NAME = Tag.class.getSimpleName();

	/**
	 * @see ish.oncourse.server.replication.updater.IAngelUpdater#updateEntityFromStub(ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable,
	 *      ish.oncourse.server.replication.updater.RelationShipCallback)
	 */
	@SuppressWarnings("unchecked")
	public void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		//entity.setCreated(stub.getCreated());
		//entity.setModified(stub.getModified());
		updateEntity((V) stub, (T) entity, callback);
	}
	
	protected final String getCurrentCollegeAngelVersion(Queueable entity) {
		return entity.getCollege() != null && StringUtils.trimToNull(entity.getCollege().getAngelVersion()) != null ? 
			entity.getCollege().getAngelVersion() : College.UNDEFINED_ANGEL_VERSION;
	}

	protected abstract void updateEntity(V stub, T entity, RelationShipCallback callback);
}
