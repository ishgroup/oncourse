package ish.oncourse.ui.services.filter;

import org.apache.tapestry5.services.*;
import org.slf4j.Logger;

import java.io.IOException;

public class LogFilter implements RequestFilter {

	private Logger log;
	private RequestGlobals requestGlobals;

	public LogFilter(Logger log, RequestGlobals requestGlobals) {
		this.log = log;
		this.requestGlobals = requestGlobals;
	}

	public boolean service(Request request, Response response,
			RequestHandler handler) throws IOException {
		long startTime = System.currentTimeMillis();

		try {

			StringBuffer url = requestGlobals.getHTTPServletRequest()
					.getRequestURL();
			log.info(String.format("Request started: %s", url));

			return handler.service(request, response);
		} finally {
			long elapsed = System.currentTimeMillis() - startTime;
			log.info(String.format("Request finished in %d ms", elapsed));
		}
	}

}
