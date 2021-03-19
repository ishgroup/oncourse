/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.tapestry.IWillowComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: akoiro
 * Date: 7/4/18
 */
public class WillowComponentRequestSelectorAnalyzer implements IWillowComponentRequestSelectorAnalyzer {
	private final ThreadLocale threadLocale;
	private final IWebSiteVersionService webSiteVersionService;


	@Inject
	public WillowComponentRequestSelectorAnalyzer(ThreadLocale threadLocale, IWebSiteVersionService webSiteVersionService) {
		this.threadLocale = threadLocale;
		this.webSiteVersionService = webSiteVersionService;
	}

	@Override
	public ComponentResourceSelector buildSelectorForRequest() {
		return buildSelectorForRequest(Collections.EMPTY_MAP);
	}

	@Override
	public ComponentResourceSelector buildSelectorForRequest(Map<String, Object> textileParams) {
		Map<String, Object> axis = new HashMap<>(textileParams);
		ComponentResourceSelector selector = new ComponentResourceSelector(threadLocale.getLocale());
		String appKey = webSiteVersionService.getApplicationKey();
		if (appKey != null) axis.put("appKey", appKey);
		return selector.withAxis(Map.class, axis);

	}
}
