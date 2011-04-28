package ish.oncourse.webservices.replication.updaters;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.WaitingList;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.Map;

public class WillowUpdaterImpl implements IWillowUpdater {

	private Map<String, IWillowUpdater> updaterMap = new HashMap<String, IWillowUpdater>();

	public WillowUpdaterImpl() {
		updaterMap.put(getEntityName(Attendance.class), new AttendanceUpdater());
		updaterMap.put(getEntityName(BinaryInfoRelation.class), new BinaryInfoRelationUpdater());
		updaterMap.put(getEntityName(Contact.class), new ContactUpdater());
		updaterMap.put(getEntityName(Course.class), new CourseUpdater());
		updaterMap.put(getEntityName(CourseClass.class), new CourseClassUpdater());
		updaterMap.put(getEntityName(CourseModule.class), new CourseModuleUpdater());
		updaterMap.put(getEntityName(Certificate.class), new CertificateUpdater());
		updaterMap.put(getEntityName(CertificateOutcome.class), new CertificateOutcomeUpdater());
		updaterMap.put(getEntityName(Discount.class), new DiscountUpdater());
		updaterMap.put(getEntityName(DiscountConcessionType.class), new DiscountConcessionTypeUpdater());
		updaterMap.put(getEntityName(Preference.class), new PreferenceUpdater());
		updaterMap.put("SessionCourseClassTutor", new SessionTutorUpdater());
		updaterMap.put(getEntityName(Student.class), new StudentUpdater());
		updaterMap.put(getEntityName(Tag.class), new TagUpdater());
		updaterMap.put(getEntityName(TaggableTag.class), new TaggableTagUpdater());
		updaterMap.put(getEntityName(Tutor.class), new TutorUpdater());
		updaterMap.put("CourseClassTutor", new TutorRoleUpdater());
		updaterMap.put(getEntityName(WaitingList.class), new WaitingListUpdater());
		updaterMap.put(getEntityName(Site.class), new SiteUpdater());
		updaterMap.put(getEntityName(Room.class), new RoomUpdater());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.webservices.replication.updaters.IWillowUpdater#
	 * updateEntityFromStub
	 * (ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 * ish.oncourse.model.Queueable,
	 * ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	public void updateEntityFromStub(ReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		String key = stub.getEntityIdentifier();
		IWillowUpdater updater = updaterMap.get(key);

		if (updater == null) {
			throw new UpdaterNotFoundException(String.format("Updater not found for entity with key:%s", key), key);
		}

		updater.updateEntityFromStub(stub, entity, callback);
	}
}
