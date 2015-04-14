package ish.oncourse.util.payment;

import ish.oncourse.model.College;
import org.apache.tapestry5.internal.services.SessionImpl;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.services.SessionPersistedObjectAnalyzer;

import javax.servlet.http.HttpSession;

public class MockSession extends SessionImpl implements Session {
	private Long collegeAttribute;
	
	public MockSession() {
		this(null, null);
	}

	@SuppressWarnings("rawtypes")
	public MockSession(HttpSession session, SessionPersistedObjectAnalyzer analyzer) {
		super(session, analyzer);
		collegeAttribute = null;
	}

	@Override
	public Object getAttribute(String name) {
		if (College.REQUESTING_COLLEGE_ATTRIBUTE.equals(name)) {
			return collegeAttribute;
		} else {
			return super.getAttribute(name);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (College.REQUESTING_COLLEGE_ATTRIBUTE.equals(name)) {
			collegeAttribute = (Long) value;
		} else {
			super.setAttribute(name, value);
		}
	}
}
