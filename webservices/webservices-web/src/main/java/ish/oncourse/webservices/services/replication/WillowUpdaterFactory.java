package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.webservices.UpdaterNotFoundException;
import ish.oncourse.webservices.soap.v4.auth.SessionToken;
import ish.oncourse.webservices.updaters.replication.CourseClassUpdater;
import ish.oncourse.webservices.updaters.replication.IWillowUpdater;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowUpdaterFactory {
	
	@Inject
	@Autowired
	private Request request;
	
	@Inject
	private ICayenneService cayenneService;

	@SuppressWarnings("rawtypes")
	public IWillowUpdater newReplicationUpdater() {
		
		Session session = request.getSession(true);
		SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
		
		ObjectContext ctx = cayenneService.newNonReplicatingContext();
		
		College college = (College) ctx.localObject(token.getCollege().getObjectId(), null);
		
		return new WillowUpdaterImpl(college);
	}

	@SuppressWarnings("rawtypes")
	private static class WillowUpdaterImpl implements IWillowUpdater {

		private Map<String, IWillowUpdater> updaterMap = new HashMap<String, IWillowUpdater>();

		private WillowUpdaterImpl(College college) {
			updaterMap.put(getClassName(CourseClass.class), new CourseClassUpdater(college, this));
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
}
