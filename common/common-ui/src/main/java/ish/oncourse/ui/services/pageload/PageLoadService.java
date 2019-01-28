package ish.oncourse.ui.services.pageload;

import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.ICacheProvider;
import ish.oncourse.cache.caffeine.CaffeineFactory;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.WebTemplateChangeTracker;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.ui.services.pageload.template.CacheKey;
import ish.oncourse.ui.services.pageload.template.GetTemplateKey;
import ish.oncourse.ui.services.pageload.template.resource.GetSiteTemplateResource;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.internal.pageload.ComponentAssembler;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.parser.TemplateToken;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PageLoadService {
	public static final String SYS_PROPERTY_CACHE_TAPESTRY_SIZE = "cache.tapestry.size";

	private static final Logger logger = LogManager.getLogger();

	private final PageLoader pageLoader;
	private final TemplateParser templateParser;
	private final ComponentRequestSelectorAnalyzer selectorAnalyzer;
	private final IWebSiteVersionService webSiteVersionService;
	private final WebTemplateChangeTracker templateChangeTracker;
	private final GetSiteTemplateResource getSiteTemplateResource;
	private final GetTemplateKey getTemplateKey;

	private final ICacheProvider cacheProvider;
	private final ICacheFactory<MultiKey, Object> cacheFactory;
	private final Configuration<MultiKey, Object> cacheConfig;


	@Inject
	public PageLoadService(ICayenneService cayenneService,
						   IWebSiteService webSiteService,
						   IWebSiteVersionService webSiteVersionService,
						   IWebNodeService webNodeService, IResourceService resourceService,
						   ICacheProvider cacheProvider,
						   ComponentResourceLocator componentResourceLocator,
						   ComponentRequestSelectorAnalyzer selectorAnalyzer,
						   TemplateParser templateParser,
						   PageLoader pageLoader) {
		this.cacheProvider = cacheProvider;
		this.webSiteVersionService = webSiteVersionService;
		this.selectorAnalyzer = selectorAnalyzer;
		this.templateParser = templateParser;
		this.pageLoader = pageLoader;
		this.templateChangeTracker = new WebTemplateChangeTracker(cayenneService, webSiteService, webSiteVersionService);
		this.getSiteTemplateResource = new GetSiteTemplateResource(webNodeService, resourceService, componentResourceLocator);
		this.getTemplateKey = new GetTemplateKey(webNodeService);

		this.cacheFactory = cacheProvider.createFactory(MultiKey.class, Object.class);
		long cacheSize = new Long(System.getProperty(SYS_PROPERTY_CACHE_TAPESTRY_SIZE, "1000"));
		this.cacheConfig = CaffeineFactory.createDefaultConfig(MultiKey.class, Object.class, cacheSize);

	}

	public ComponentTemplate getTemplate(ComponentModel componentModel, ComponentResourceSelector selector) {
		MultiKey resourceKey = getTemplateKey.get(componentModel.getComponentClassName(), CacheKey.resources, selector);
		Resource resource = getResource(resourceKey, componentModel, selector);
		return getTemplate(getTemplateKey.get(componentModel.getComponentClassName(), CacheKey.templates, selector), resource);
	}


	private Resource getResource(MultiKey key, ComponentModel componentModel, ComponentResourceSelector selector) {
		return getCachedValue(key, () -> getSiteTemplateResource.get(componentModel, selector));
	}

	private ComponentTemplate getTemplate(MultiKey key, Resource resource) {
		return getCachedValue(key, () -> parse(resource));
	}


	/**
	 * Cache of parsed templates, keyed on resource.
	 */
	private ComponentTemplate parse(Resource resource) {
		ComponentTemplate result = missingTemplate;
		if (resource.exists()) {
			result = templateParser.parseTemplate(resource);
		} else {
			logger.debug("%s resource doesn't exist", resource);
		}
		return result;
	}

	public ComponentAssembler getAssembler(String className, ComponentResourceSelector selector, Supplier<ComponentAssembler> delegate) {
		MultiKey key = getTemplateKey.get(className, CacheKey.assemblers, selector);
		return getCachedValue(key, delegate);
	}


	/**
	 * Create cache if absent, use <code>appKey</code> as group name,
	 * appKey contains site key so we don't need to use host name in MultiKey
	 * appKey can been null for SiteNotFound page
	 */
	private <V> V getCachedValue(MultiKey key, Supplier<V> vSupplier) {
		String appKey = webSiteVersionService.getApplicationKey();
		try {
			if (appKey == null || appKey.startsWith(WebSiteVersionService.EDITOR_PREFIX))
				return vSupplier.get();
			Cache<MultiKey, Object> cache = cacheFactory.createIfAbsent(appKey, cacheConfig);
			V v = (V) cache.get(key);
			if (v == null) {
				v = vSupplier.get();
				cache.put(key, v);
			}
			return v;
		} catch (Exception e) {
			logger.error("Exception appeared during reading cached value. cacheGroup {}, key: {}", appKey, key);
			logger.catching(e);
			return vSupplier.get();
		}
	}

	public Page getPage(String canonicalPageName) {
		ComponentResourceSelector selector = selectorAnalyzer.buildSelectorForRequest();
		MultiKey key = getTemplateKey.get(canonicalPageName, CacheKey.pages, selector);
		return getCachedValue(key, () -> pageLoader.loadPage(canonicalPageName, selector));
	}

	/**
	 * We don't use this functionality.
	 */
	public Set<Page> getAllPages() {
		return Collections.EMPTY_SET;
	}

	public boolean containsChanges() {
		return templateChangeTracker.containsChanges();
	}

	public void clean() {
		cleanAllCaches();
		templateChangeTracker.resetTimestamp();
	}

	private void cleanAllCaches() {
		String cacheKey = webSiteVersionService.getApplicationKey();
		cacheProvider.getCacheManager().destroyCache(cacheKey);
	}

	static final ComponentTemplate missingTemplate = new ComponentTemplate() {

		public Map<String, Location> getComponentIds() {
			return Collections.emptyMap();
		}

		public Resource getResource() {
			return null;
		}

		public List<TemplateToken> getTokens() {
			return Collections.emptyList();
		}

		public boolean isMissing() {
			return true;
		}

		public List<TemplateToken> getExtensionPointTokens(String extensionPointId) {
			return null;
		}

		public boolean isExtension() {
			return false;
		}

		public boolean usesStrictMixinParameters() {
			return false;
		}

		public String toString() {
			return new ToStringBuilder(this).append("missingTemplate").toString();
		}
	};
}
