package ish.oncourse.cms.services.state;

import org.apache.cayenne.ObjectContext;

public interface ISessionStoreService  {
	
	String SESSION_CONTEXT = "session_context";
	
	/**
	 * A read-write context bound to http session.
	 * @return
	 */
	ObjectContext sessionContext();
}
