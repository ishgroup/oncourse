package org.apache.tapestry5.internal.pageload;

import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.ThreadCleanupListener;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

public class RequestPageCacheOverride implements RequestPageCache,
		ThreadCleanupListener {
	private final Logger logger;

	private final ComponentClassResolver resolver;

	private final PageSource pageSource;

	private final ThreadLocale threadLocale;

	private final Map<MultiKey, Page> cache = CollectionFactory.newMap();

	@Inject
	private Request request;

	public RequestPageCacheOverride(Logger logger,
			ComponentClassResolver resolver, PageSource pageSource,
			ThreadLocale threadLocale) {
		this.logger = logger;
		this.resolver = resolver;
		this.pageSource = pageSource;
		this.threadLocale = threadLocale;
	}

	public void threadDidCleanup() {
		for (Page page : cache.values()) {
			try {
				page.detached();
			} catch (Throwable t) {
				logger.error(String.format("Error detaching page %s: %s", page,
						InternalUtils.toMessage(t)), t);
			}
		}
	}

	public Page get(String pageName) {

		String canonical = resolver.canonicalizePageName(pageName);
		MultiKey key = new MultiKey(canonical, request.getServerName());

		Page page = cache.get(key);

		if (page == null) {
			Locale locale = threadLocale.getLocale();

			page = pageSource.getPage(canonical, locale);

			try {
				page.attached();
			} catch (Throwable t) {
				throw new RuntimeException(String.format(
						"Unable to attach page %s (%s): %s", canonical, locale,
						InternalUtils.toMessage(t)), t);
			}

			cache.put(key, page);
		}

		return page;
	}
}