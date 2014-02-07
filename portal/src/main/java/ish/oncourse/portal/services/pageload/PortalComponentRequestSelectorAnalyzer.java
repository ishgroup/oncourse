package ish.oncourse.portal.services.pageload;

import java.util.Locale;

import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
@Deprecated
public class PortalComponentRequestSelectorAnalyzer implements ComponentRequestSelectorAnalyzer {
	
	private final ThreadLocale threadLocale;
	
	private final IUserAgentDetector userAgentDetector;
	
	public PortalComponentRequestSelectorAnalyzer(ThreadLocale threadLocale, IUserAgentDetector clientService) {
		super();
		this.threadLocale = threadLocale;
		this.userAgentDetector = clientService;
	}

	public ComponentResourceSelector buildSelectorForRequest() {
		Locale locale = threadLocale.getLocale();
		UserAgent userAgent = userAgentDetector.getUserAgent();
		return new ComponentResourceSelector(locale).withAxis(UserAgent.class, userAgent);
	}
}
