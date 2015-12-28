package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

public abstract class AbstractWillowUpdater<V extends GenericReplicationStub, T extends Queueable> implements IWillowUpdater {
	public static final String CONTACT_ENTITY_NAME = Contact.class.getSimpleName();
	public static final String COURSE_ENTITY_NAME = Course.class.getSimpleName();
	public static final String COURSE_CLASS_ENTITY_NAME = CourseClass.class.getSimpleName();
	public static final String CERTIFICATE_ENTITY_NAME = Certificate.class.getSimpleName();
	public static final String ENROLMENT_ENTITY_NAME = Enrolment.class.getSimpleName();
	public static final String INVOICE_ENTITY_NAME = Invoice.class.getSimpleName();
	public static final String ROOM_ENTITY_NAME = Room.class.getSimpleName();
	public static final String SESSION_ENTITY_NAME = Session.class.getSimpleName();
	public static final String SITE_ENTITY_NAME = Site.class.getSimpleName();
	public static final String STUDENT_ENTITY_NAME = Student.class.getSimpleName();
	public static final String TUTOR_ENTITY_NAME = Tutor.class.getSimpleName();
	public static final String TAG_ENTITY_NAME = Tag.class.getSimpleName();
	public static final String APPLICATION_ENTITY_NAME = Application.class.getSimpleName();
	public static final String PRIOR_LEARNING_ENTITY_NAME = PriorLearning.class.getSimpleName();

	private ICayenneService cayenneService;

	public void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		updateEntity((V) stub, (T) entity, callback);
	}

	protected abstract void updateEntity(V stub, T entity, RelationShipCallback callback);
	
	public void setCayenneService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	};

	protected void deleteConcurrentPreferences(String preferenceName, College college) {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		Preference preference = ObjectSelect.query(Preference.class).where(Preference.NAME.eq(preferenceName)).and(Preference.COLLEGE.eq(college)).selectOne(context);
		if (preference != null) {
			context.deleteObject(preference);
			context.commitChanges();
		}		
	}
}
