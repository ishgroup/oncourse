package ish.oncourse.ui.services.pageload;

import ish.oncourse.cayenne.cache.JCacheDefaultConfigurationFactory;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.WebTemplateChangeTracker;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
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
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PageLoadService {
	private static final Logger logger = LogManager.getLogger();

	private final PageLoader pageLoader;
	private final TemplateParser templateParser;
	private final ComponentRequestSelectorAnalyzer selectorAnalyzer;

	private final CacheManager cacheManager;
	private final IWebSiteVersionService webSiteVersionService;
	private final WebTemplateChangeTracker templateChangeTracker;
	private final GetSiteTemplateResource getSiteTemplateResource;
	private final GetTemplateKey getTemplateKey;
	private final JCacheDefaultConfigurationFactory.GetOrCreateCache getOrCreateCache;


	@Inject
	public PageLoadService(ICayenneService cayenneService,
						   IWebSiteService webSiteService,
						   IWebSiteVersionService webSiteVersionService,
						   IWebNodeService webNodeService, IResourceService resourceService,
						   CacheManager cacheManager,
						   ComponentResourceLocator componentResourceLocator,
						   ComponentRequestSelectorAnalyzer selectorAnalyzer,
						   Request request, TemplateParser templateParser,
						   PageLoader pageLoader) {
		this.cacheManager = cacheManager;
		this.webSiteVersionService = webSiteVersionService;
		this.selectorAnalyzer = selectorAnalyzer;
		this.templateParser = templateParser;
		this.pageLoader = pageLoader;
		this.templateChangeTracker = new WebTemplateChangeTracker(cayenneService, webSiteService, webSiteVersionService);
		this.getSiteTemplateResource = new GetSiteTemplateResource(webNodeService, resourceService, componentResourceLocator);
		this.getTemplateKey = new GetTemplateKey(webNodeService, request);
		this.getOrCreateCache = new JCacheDefaultConfigurationFactory.GetOrCreateCache(cacheManager);
	}

	public ComponentTemplate getTemplate(ComponentModel componentModel, ComponentResourceSelector selector) {
		MultiKey key = getTemplateKey.get(componentModel.getComponentClassName(), selector);
		Resource resource = getResource(key, componentModel, selector);
		return getTemplate(key, resource);
	}


	private Resource getResource(MultiKey key, ComponentModel componentModel, ComponentResourceSelector selector) {
		return getCachedValue(CacheKey.resources, key, Resource.class,
				() -> getSiteTemplateResource.get(componentModel, selector));
	}

	private ComponentTemplate getTemplate(MultiKey key, Resource resource) {
		return getCachedValue(CacheKey.templates, key,
				ComponentTemplate.class,
				() -> parse(resource));
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
		MultiKey key = getTemplateKey.get(className, selector);
		return getCachedValue(CacheKey.assemblers, key,
				ComponentAssembler.class, delegate);
	}


	private <V> V getCachedValue(CacheKey cacheKey, MultiKey key, Class<V> valueClass, Supplier<V> get) {
		try {
			String appKey = webSiteVersionService.getApplicationKey();
			if (appKey.startsWith(WebSiteVersionService.EDITOR_PREFIX))
				return get.get();
			Cache<MultiKey, V> cache = getOrCreateCache.getOrCreate(cacheKey.getCacheName(appKey), MultiKey.class, valueClass);
			V v = cache.get(key);
			if (v == null) {
				v = get.get();
				cache.put(key, v);
			}
			return v;
		} catch (Exception e) {
			logger.error("Exception appeared during reading cached value. cacheGroup {}, key: {}", cacheKey.name(), key);
			logger.catching(e);
			return get.get();
		}
	}

	public Page getPage(String canonicalPageName) {
		ComponentResourceSelector selector = selectorAnalyzer.buildSelectorForRequest();
		MultiKey key = getTemplateKey.get(canonicalPageName, selector);
		return getCachedValue(CacheKey.pages, key,
				Page.class,
				() -> pageLoader.loadPage(canonicalPageName, selector));
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
		for (CacheKey key : CacheKey.values()) {
			cacheManager.destroyCache(key.getCacheName(cacheKey));
		}
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

	enum CacheKey {
		resources,
		templates,
		assemblers,
		pages;

		public String getCacheName(String applicationKey) {
			return String.format("%s_%s", this.name(), applicationKey);
		}
	}

}
