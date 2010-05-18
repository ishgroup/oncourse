package ish.oncourse.ui.services.filter;

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

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
