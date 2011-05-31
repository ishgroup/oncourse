package ish.oncourse.webservices.listeners;

import ish.oncourse.webservices.soap.v4.auth.SessionToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class CollegeSessions implements HttpSessionListener, HttpSessionAttributeListener {

	private static final Logger logger = Logger.getLogger(CollegeSessions.class);

	private static Map<Long, HttpSession> SESSION_MAP = new ConcurrentHashMap<Long, HttpSession>();

	@Override
	public void sessionCreated(HttpSessionEvent se) {

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		SessionToken token = (SessionToken) se.getSession().getAttribute(SessionToken.SESSION_TOKEN_KEY);
		if (token != null) {
			logger.info(String.format("Terminating session for college:%s, with communicationKey:%s.", token.getCollege().getName(),
					token.getCommunicationKey()));
			SESSION_MAP.remove(token.getCommunicationKey());
		}
	}

	@Override
	public void attributeAdded(HttpSessionBindingEvent se) {
		Object attr = se.getValue();

		if (attr instanceof SessionToken) {
			SessionToken token = (SessionToken) attr;
			logger.info(String.format("Creating session for college: %s, with communicationKey:%s.", token.getCollege().getName(),
					token.getCommunicationKey()));
			SESSION_MAP.put(token.getCommunicationKey(), se.getSession());
		}

	}

	public static HttpSession getCollegeSession(Long communicationKey) {
		return SESSION_MAP.get(communicationKey);
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent se) {

	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent se) {

	}
}
