package ish.oncourse.cms.services.state;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class SessionStoreService implements ISessionStoreService {

	@Inject
	private Request request;

	public ObjectContext sessionContext() {
		Session session = request.getSession(false);
		return (ObjectContext) session.getAttribute(SESSION_CONTEXT);
	}
}
