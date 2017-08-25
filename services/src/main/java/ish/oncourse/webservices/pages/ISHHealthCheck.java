/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.pages;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.util.TextStreamResponse;

import static ish.oncourse.configuration.Configuration.API_VERSION;

public class ISHHealthCheck {
	private static final Logger logger = LogManager.getLogger();

	StreamResponse onActivate() {
		return new TextStreamResponse("text/plain", System.getProperty(API_VERSION));
	}
}
