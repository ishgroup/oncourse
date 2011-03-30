package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.services.ICollegeRequestService;
import ish.oncourse.webservices.updaters.replication.AbstractWillowUpdater;
import ish.oncourse.webservices.updaters.replication.AttendanceUpdater;
import ish.oncourse.webservices.updaters.replication.BinaryInfoRelationUpdater;
import ish.oncourse.webservices.updaters.replication.CertificateOutcomeUpdater;
import ish.oncourse.webservices.updaters.replication.CertificateUpdater;
import ish.oncourse.webservices.updaters.replication.CourseClassUpdater;
import ish.oncourse.webservices.updaters.replication.CourseModuleUpdater;
import ish.oncourse.webservices.updaters.replication.CourseUpdater;
import ish.oncourse.webservices.updaters.replication.DiscountConcessionTypeUpdater;
import ish.oncourse.webservices.updaters.replication.DiscountUpdater;
import ish.oncourse.webservices.updaters.replication.IWillowUpdater;
import ish.oncourse.webservices.updaters.replication.PreferenceUpdater;
import ish.oncourse.webservices.updaters.replication.SessionCourseClassTutorUpdater;
import ish.oncourse.webservices.updaters.replication.SessionTutorUpdater;
import ish.oncourse.webservices.updaters.replication.TagUpdater;
import ish.oncourse.webservices.updaters.replication.TaggableTagUpdater;
import ish.oncourse.webservices.updaters.replication.TutorRoleUpdater;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowUpdaterFactory {

	@Inject
	@Autowired
	private ICollegeRequestService collegeRequestService;

	@SuppressWarnings("rawtypes")
	public IWillowUpdater newReplicationUpdater(ObjectContext ctx, TransactionGroup group) {
		return new WillowUpdaterImpl(ctx, group);
	}

	@SuppressWarnings("rawtypes")
	private class WillowUpdaterImpl implements IWillowUpdater<ReplicationStub> {

		private Map<String, IWillowUpdater> updaters = new HashMap<String, IWillowUpdater>();

		private WillowUpdaterImpl(ObjectContext objectContext, TransactionGroup group) {
			Map<String, AbstractWillowUpdater> updaterMap = new HashMap<String, AbstractWillowUpdater>();
			
			updaterMap.put(getClassName(Attendance.class), new AttendanceUpdater());
			updaterMap.put(getClassName(BinaryInfoRelation.class), new BinaryInfoRelationUpdater());
			updaterMap.put(getClassName(Course.class), new CourseUpdater());
			updaterMap.put(getClassName(CourseClass.class), new CourseClassUpdater());
			updaterMap.put(getClassName(CourseModule.class), new CourseModuleUpdater());
			updaterMap.put(getClassName(Certificate.class), new CertificateUpdater());
			updaterMap.put(getClassName(CertificateOutcome.class), new CertificateOutcomeUpdater());
			updaterMap.put(getClassName(Discount.class), new DiscountUpdater());
			updaterMap.put(getClassName(DiscountConcessionType.class), new DiscountConcessionTypeUpdater());
			updaterMap.put(getClassName(Preference.class), new PreferenceUpdater());
			updaterMap.put(getClassName(SessionTutor.class), new SessionTutorUpdater());
			updaterMap.put(getClassName(Tag.class), new TagUpdater());
			updaterMap.put(getClassName(TaggableTag.class), new TaggableTagUpdater());
			updaterMap.put(getClassName(TutorRole.class), new TutorRoleUpdater());
			
			for (Map.Entry<String, AbstractWillowUpdater> up : updaterMap.entrySet()) {
				up.getValue().setCollege(collegeRequestService.getRequestingCollege());
				up.getValue().setGroup(group);
				up.getValue().setNext(this);
				up.getValue().setObjectContext(objectContext);
			}
			
			updaters.putAll(updaterMap);
			

			// special cases
			updaters.put("SessionCourseClassTutor", new SessionCourseClassTutorUpdater(objectContext, collegeRequestService.getRequestingCollege(), group, this));
		}

		@SuppressWarnings("unchecked")
		@Override
		public Queueable updateRecord(ReplicationStub stub, List<ReplicatedRecord> result) {
			String key = stub.getEntityIdentifier();

			IWillowUpdater updater = updaters.get(key);

			if (updater == null) {
				throw new UpdaterNotFoundException("Builder not found during record conversion", key);
			}

			return updater.updateRecord(stub, result);
		}

	}

	private static String getClassName(Class<?> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}
}
