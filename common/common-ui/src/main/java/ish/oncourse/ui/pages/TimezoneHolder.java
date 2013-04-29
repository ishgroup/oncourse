package ish.oncourse.ui.pages;

import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesService;

import org.apache.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class TimezoneHolder {
	private static final String TIMEZONE_NAME_PARAMETER = "timezoneName";
	private static final String STATUS_OK_RESPONSE = "{status: 'OK'}";
	private static final String JSON_RESPONSE_TYPE = "text/json";
	private static final String OFFSET_PARAMETER = "offset";
	private static final Logger LOGGER = Logger.getLogger(TimezoneHolder.class);
	
	@Inject
	private Request request;
	
	@Inject
	private ICookiesService cookiesService;

	StreamResponse onActionFromSetupOffset() {
		for (String parameter : request.getParameterNames()) {
			String value = request.getParameter(parameter);
			if (OFFSET_PARAMETER.equals(parameter)) {
				cookiesService.writeCookieValue(CookiesService.CLIENT_TIMEZONE_OFFSET_IN_MINUTES, value);
			} else if (TIMEZONE_NAME_PARAMETER.equals(parameter)) {
				cookiesService.writeCookieValue(CookiesService.CLIENT_TIMEZONE_NAME, value);
			} else {
				LOGGER.error(String.format("Unexpected param %s with value %s for timezone holder passed", parameter, value));
			}
		}
		return new TextStreamResponse(JSON_RESPONSE_TYPE, STATUS_OK_RESPONSE);
	}
}
