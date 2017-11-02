package org.apache.tapestry5.internal.pageload;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteService;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.services.Request;

import java.util.Locale;
import java.util.concurrent.Callable;

public class PageSourceOverride implements PageSource {
	private final PageLoader pageLoader;
	private IWebNodeService webNodeService;
	private IWebSiteVersionService webSiteVersionService;
	private transient WebCacheService webCacheService;

	private Request request;

	public PageSourceOverride(PageLoader pageLoader, IWebNodeService webNodeService, Request request, IWebSiteVersionService webSiteVersionService, WebCacheService webCacheService) {
		this.webSiteVersionService = webSiteVersionService;
		this.pageLoader = pageLoader;
		this.webNodeService = webNodeService;
		this.request = request;
		this.webCacheService = webCacheService;
	}

	public Page getPage(final String canonicalPageName, final Locale locale) {

		WebSiteLayout layout = webNodeService.getLayout();
		WebSite site = (WebSite) request.getAttribute(WebSiteService.CURRENT_WEB_SITE);

		MultiKey key = new MultiKey(canonicalPageName,
				site != null ? site.getSiteKey() : request.getServerName(), layout != null ? layout.getLayoutKey() : null);
		String siteKey = webSiteVersionService.getCacheKey();
		return webCacheService.getPage(siteKey, key, new Callable<Page>() {
			@Override
			public Page call() throws Exception {
				return pageLoader.loadPage(canonicalPageName, locale);
			}
		});
	}

}
