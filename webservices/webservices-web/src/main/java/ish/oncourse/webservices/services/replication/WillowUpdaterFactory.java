package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.UpdaterNotFoundException;
import ish.oncourse.webservices.updaters.replication.AttendanceUpdater;
import ish.oncourse.webservices.updaters.replication.CertificateOutcomeUpdater;
import ish.oncourse.webservices.updaters.replication.CertificateUpdater;
import ish.oncourse.webservices.updaters.replication.CourseClassUpdater;
import ish.oncourse.webservices.updaters.replication.CourseModuleUpdater;
import ish.oncourse.webservices.updaters.replication.CourseUpdater;
import ish.oncourse.webservices.updaters.replication.DiscountConcessionTypeUpdater;
import ish.oncourse.webservices.updaters.replication.DiscountUpdater;
import ish.oncourse.webservices.updaters.replication.IWillowUpdater;
import ish.oncourse.webservices.updaters.replication.SessionTutorUpdater;
import ish.oncourse.webservices.updaters.replication.TutorRoleUpdater;
import ish.oncourse.webservices.util.SoapUtil;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowUpdaterFactory {
	
	@Inject
	@Autowired
	private Request request;
	
	@Inject
	@Autowired
	private IWillowQueueService queueService;

	@SuppressWarnings("rawtypes")
	public IWillowUpdater newReplicationUpdater() {
		College college = (College) request.getAttribute(SoapUtil.REQUESTING_COLLEGE);
		return new WillowUpdaterImpl(college);
	}

	@SuppressWarnings("rawtypes")
	private class WillowUpdaterImpl implements IWillowUpdater {

		private Map<String, IWillowUpdater> updaterMap = new HashMap<String, IWillowUpdater>();

		private WillowUpdaterImpl(College college) {
			updaterMap.put(getClassName(Attendance.class), new AttendanceUpdater(college, queueService, this));
			updaterMap.put(getClassName(Course.class), new CourseUpdater(college, queueService, this));
			updaterMap.put(getClassName(CourseClass.class), new CourseClassUpdater(college, queueService, this));
			updaterMap.put(getClassName(CourseModule.class), new CourseModuleUpdater(college, queueService, this));
			updaterMap.put(getClassName(Certificate.class), new CertificateUpdater(college, queueService, this));
			updaterMap.put(getClassName(CertificateOutcome.class), new CertificateOutcomeUpdater(college, queueService, this));
			updaterMap.put(getClassName(Discount.class), new DiscountUpdater(college, queueService, this));
			updaterMap.put(getClassName(DiscountConcessionType.class), new DiscountConcessionTypeUpdater(college, queueService, this));
			updaterMap.put(getClassName(SessionTutor.class), new SessionTutorUpdater(college, queueService, this));
			updaterMap.put(getClassName(TutorRole.class), new TutorRoleUpdater(college, queueService, this));
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<HollowStub> updateRecord(ReplicationStub stub) {
			String key = stub.getEntityIdentifier();

			IWillowUpdater updater = updaterMap.get(key);

			if (updater == null) {
				throw new UpdaterNotFoundException("Builder not found during record conversion", key);
			}

			return updater.updateRecord(stub);
		}

	}

	private static String getClassName(Class<?> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void setQueueService(IWillowQueueService queueService) {
		this.queueService = queueService;
	}
}
