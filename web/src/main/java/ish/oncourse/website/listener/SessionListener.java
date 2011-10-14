package ish.oncourse.website.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionListener implements HttpSessionListener {
	private static final Logger LOGGER = Logger.getLogger(SessionListener.class);

	public void sessionCreated(HttpSessionEvent se) {
		// warn if the session is created in the sessionless app
		LOGGER.warn("Session is created in website-web app!", new Exception(se.getSource()
				.toString()));
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		// do nothing
	}

}
