package org.apache.tapestry5.internal.pageload;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.WebTemplateChangeTracker;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.textile.TextileUtil;
import net.sf.ehcache.pool.sizeof.annotations.IgnoreSizeOf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.TapestryConstants;
import org.apache.tapestry5.internal.event.InvalidationEventHubImpl;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.parser.TemplateToken;
import org.apache.tapestry5.internal.services.ComponentTemplateSource;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.InvalidationEventHub;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.UpdateListener;
import org.apache.tapestry5.services.templates.ComponentTemplateLocator;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * Service implementation that manages a cache of parsed component templates.
 */
@IgnoreSizeOf
public final class ComponentTemplateSourceOverride extends InvalidationEventHubImpl implements ComponentTemplateSource, UpdateListener {

	private Request request;
	private static final Logger logger = LogManager.getLogger();
	private static final String PACKAGE = "ish.oncourse";

	private IResourceService resourceService;
	private IWebNodeService webNodeService;
	private IWebSiteVersionService webSiteVersionService;
	private final TemplateParser parser;
	private final ComponentTemplateLocator locator;
	private transient WebCacheService webCacheService;
	private final WebTemplateChangeTracker entityChangeTracker;
	
	private final ComponentTemplate missingTemplate = new ComponentTemplate() {

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
	};

	public ComponentTemplateSourceOverride(TemplateParser parser, @Primary ComponentTemplateLocator locator,
			Request request, IResourceService resourceService, IWebNodeService webNodeService,
			ICayenneService cayenneService, IWebSiteService webSiteService, 
			IWebSiteVersionService webSiteVersionService, WebCacheService webCacheService) {

		this.parser = parser;
		this.locator = locator;
		this.entityChangeTracker = new WebTemplateChangeTracker(cayenneService, webSiteService, webSiteVersionService);
		this.webSiteVersionService = webSiteVersionService;
		this.request = request;
		this.resourceService = resourceService;
		this.webNodeService = webNodeService;
		this.webCacheService = webCacheService;
	}

    /**
     * Resolves the component name to a localized {@link Resource} (using the
     * {@link ComponentTemplateLocator} chain of command service). The localized
     * resource is used as the key to a cache of {@link ComponentTemplate}s.
     * <p/>
     * If a template doesn't exist, then the missing ComponentTemplate is
     * returned.
     */
    @Override
    public ComponentTemplate getTemplate(final ComponentModel componentModel, final Locale locale) {
        String componentName = componentModel.getComponentClassName();

        //it reads templateFileName attribute to get user defined template.
        CustomTemplateDefinition ctd  = (CustomTemplateDefinition) request.getAttribute(TextileUtil.CUSTOM_TEMPLATE_DEFINITION);
		if (ctd != null && !componentName.endsWith(ctd.getTemplateClassName())) {
			ctd = null;
		}

		WebSiteLayout layout = webNodeService.getLayout();
        //we should use anouther key to cache Resource for component when user defines custom template
        MultiKey key = CustomTemplateDefinition.getMultiKeyBy(componentName, ctd, request.getServerName(), locale, layout != null ? layout.getLayoutKey() : null);

        String cacheKey = webSiteVersionService.getCacheKey();
     
		final CustomTemplateDefinition finalCtd = ctd;
		final Resource resource = webCacheService.getTemplateResource(cacheKey, key,
				new Callable<Resource>() {
					@Override
					public Resource call() throws Exception {
						return locateSiteTemplateResource(componentModel, finalCtd, locale);
					}
				});

		return webCacheService.getTemplate(cacheKey, resource,
				new Callable<ComponentTemplate>() {
					@Override
					public ComponentTemplate call() throws Exception {
						return parseTemplate(resource);
					}
				});
    }

	private ComponentTemplate parseTemplate(Resource r) {
		// In a race condition, we may parse the same template more than once.
		// This will likely add
		// the resource to the tracker multiple times. Not likely this will
		// cause a big issue.
		ComponentTemplate result = missingTemplate;

		if (r.exists()) {
			result = parser.parseTemplate(r);
		}

		return result;
	}

	private Resource locateTemplateResource(ComponentModel initialModel, Locale locale) {
		ComponentModel model = initialModel;
		while (model != null) {
			Resource localized = locator.locateTemplate(model, locale);

			if (localized != null) {
				return localized;
			}

			// Otherwise, this component doesn't have its own template ... lets
			// work up to its
			// base class and check there.

			model = model.getParentModel();
		}

		// This will be a Resource whose URL is null, which will be picked up
		// later and force the
		// return of the empty template.

		return initialModel.getBaseResource().withExtension(TapestryConstants.TEMPLATE_EXTENSION);
	}

	private Resource locateSiteTemplateResource(ComponentModel model, CustomTemplateDefinition ctd, Locale locale) {

		String componentName = model.getComponentClassName();

		logger.debug("Retrieve template for component class name: {}, if starts with: {}", componentName, PACKAGE);
		
		boolean isErrorPage = false;
		
		try {
			Class<?> componentClass = Class.forName(componentName);
			isErrorPage = ExceptionReporter.class.isAssignableFrom(componentClass);
		}
		catch (ClassNotFoundException e) {
			logger.warn("Component class not found.", e);
		}

		if (componentName.startsWith(PACKAGE) && !isErrorPage) {
			Resource templateBaseResource = model.getBaseResource().withExtension("tml");

			String templatePath = templateBaseResource.getPath();
            String templateFile = templatePath.substring(templatePath.lastIndexOf("/") + 1);
			WebSiteLayout layout = webNodeService.getLayout();
			
			if (layout != null) {

                Resource resource = null;
                if (ctd != null && model.getComponentClassName().endsWith(ctd.getTemplateClassName())) {
                    logger.debug("Try to load user defined template {} override for {}.", ctd.getTemplateFileName(), templateFile);
                    resource = resourceService.getDbTemplateResource(layout, ctd.getTemplateFileName());

					if (resource == null) {
						resource = new ClasspathResource(templatePath.replace(templateFile, ctd.getTemplateFileName()));
					}
                }

                if (resource == null || !resource.exists()) {
                    logger.debug("Try to load template override for: {}", templateFile);
                    resource = resourceService.getDbTemplateResource(layout, templateFile);
                }

				if (resource != null) {
					return resource;
				}

				logger.debug("Template override: {} doesn't exist.", templateFile);
			}
		}

		return locateTemplateResource(model, locale);
	}

	/**
	 * Checks to see if any parsed resource has changed. If so, then all
	 * internal caches are cleared, and an invalidation event is fired. This is
	 * brute force ... a more targeted dependency management strategy may come
	 * later.
	 */
	public void checkForUpdates() {
		String cacheKey = webSiteVersionService.getCacheKey();
		if (entityChangeTracker.containsChanges(cacheKey)) {
			entityChangeTracker.resetTimestamp(cacheKey);
			webCacheService.cleanTemplatesCache(cacheKey);
			fireInvalidationEvent();
		}
	}

	public InvalidationEventHub getInvalidationEventHub() {
		return this;
	}
}
