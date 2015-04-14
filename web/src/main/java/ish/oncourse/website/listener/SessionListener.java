package ish.oncourse.website.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
	private static final Logger logger = LogManager.getLogger();

	public void sessionCreated(HttpSessionEvent se) {
		// warn if the session is created in the sessionless app
		logger.warn("Session is created in website-web app!", new Exception(se.getSource()
				.toString()));
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		// do nothing
	}

}
