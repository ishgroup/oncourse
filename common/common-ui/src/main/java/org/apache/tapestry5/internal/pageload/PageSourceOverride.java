package org.apache.tapestry5.internal.pageload;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.WebSiteService;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.InvalidationListener;
import org.apache.tapestry5.services.Request;

import java.util.Locale;
import java.util.Map;

public class PageSourceOverride implements PageSource, InvalidationListener {
	private final PageLoader pageLoader;
	private IWebNodeService webNodeService;

	private Request request;

	private final Map<MultiKey, Page> pageCache = CollectionFactory
			.newConcurrentMap();

	public PageSourceOverride(PageLoader pageLoader, IWebNodeService webNodeService, Request request) {
		this.pageLoader = pageLoader;
		this.webNodeService = webNodeService;
		this.request = request;
	}

	public synchronized void objectWasInvalidated() {
		pageCache.clear();
	}

	public Page getPage(String canonicalPageName, Locale locale) {

		String layout = webNodeService.getLayoutKey();
		WebSite site = (WebSite) request.getAttribute(WebSiteService.CURRENT_WEB_SITE);

		MultiKey key = new MultiKey(canonicalPageName,
				site != null ? site.getSiteKey() : request.getServerName(), layout);

		if (!pageCache.containsKey(key)) {
			// In rare race conditions, we may see the same page loaded multiple
			// times across
			// different threads. The last built one will "evict" the others
			// from the page cache,
			// and the earlier ones will be GCed.

			Page page = pageLoader.loadPage(canonicalPageName, locale);

			pageCache.put(key, page);
		}

		// From good authority (Brian Goetz), this is the best way to ensure
		// that the
		// loaded page, with all of its semi-mutable construction-time state, is
		// properly published.

		return pageCache.get(key);
	}

}
