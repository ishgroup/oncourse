package ish.oncourse.ui.pages;

import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class TimezoneHolder {
	private static final String TIMEZONE_NAME_PARAMETER = "timezoneName";
	private static final String STATUS_OK_RESPONSE = "{status: 'OK'}";
	private static final String JSON_RESPONSE_TYPE = "text/json";
	private static final String OFFSET_PARAMETER = "offset";
	private static final Logger logger = LogManager.getLogger();
	
	@Inject
	private Request request;
	
	@Inject
	private ICookiesService cookiesService;

	StreamResponse onActionFromSetupOffset() {
		for (String parameter : request.getParameterNames()) {
			String value = request.getParameter(parameter);
			switch (parameter) {
				case OFFSET_PARAMETER:
					cookiesService.writeCookieValue(CookiesService.CLIENT_TIMEZONE_OFFSET_IN_MINUTES, value);
					break;
				case TIMEZONE_NAME_PARAMETER:
					cookiesService.writeCookieValue(CookiesService.CLIENT_TIMEZONE_NAME, value);
					break;
				default:
					logger.error("Unexpected param {} with value {} for timezone holder passed", parameter, value);
					break;
			}
		}
		return new TextStreamResponse(JSON_RESPONSE_TYPE, STATUS_OK_RESPONSE);
	}
}
