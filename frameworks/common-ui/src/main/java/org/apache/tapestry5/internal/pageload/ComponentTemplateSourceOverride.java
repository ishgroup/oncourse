package org.apache.tapestry5.internal.pageload;

import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.template.T5FileResource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
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
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.URLChangeTracker;
import org.apache.tapestry5.ioc.services.ClasspathURLConverter;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.InvalidationEventHub;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.UpdateListener;
import org.apache.tapestry5.services.templates.ComponentTemplateLocator;


/**
 * Service implementation that manages a cache of parsed component templates.
 */
public final class ComponentTemplateSourceOverride extends InvalidationEventHubImpl implements ComponentTemplateSource,
		UpdateListener {

	private Request request;
	private static final Logger LOGGER = Logger.getLogger(ComponentTemplateSourceOverride.class);
	private static final String PACKAGE = "ish.oncourse";
	private IResourceService resourceService;
	private final TemplateParser parser;
	private final ComponentTemplateLocator locator;
	private final URLChangeTracker tracker;
	/**
	 * Caches from a key (combining component name and locale) to a resource.
	 * Often, many different keys will point to the same resource (i.e.,
	 * "foo:en_US", "foo:en_UK", and "foo:en" may all be parsed from the same
	 * "foo.tml" resource). The resource may end up being null, meaning the
	 * template does not exist in any locale.
	 */
	private final Map<MultiKey, Resource> templateResources = CollectionFactory.newConcurrentMap();
	/**
	 * Cache of parsed templates, keyed on resource.
	 */
	private final Map<Resource, ComponentTemplate> templates = CollectionFactory.newConcurrentMap();
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

		public List<TemplateToken> getExtensionPointTokens(
				String extensionPointId) {
			return null;
		}

		public boolean isExtension() {
			return false;
		}
	};

	public ComponentTemplateSourceOverride(TemplateParser parser,
			@Primary ComponentTemplateLocator templateLocator,
			ClasspathURLConverter classpathURLConverter,
			Request request,
			IResourceService resourceService) {

		this(parser, templateLocator, new URLChangeTracker(
				classpathURLConverter));

		this.request = request;
		this.resourceService = resourceService;
	}

	ComponentTemplateSourceOverride(TemplateParser parser,
			ComponentTemplateLocator locator, URLChangeTracker tracker) {
		this.parser = parser;
		this.locator = locator;
		this.tracker = tracker;
	}

	/**
	 * Resolves the component name to a localized {@link Resource} (using the
	 * {@link ComponentTemplateLocator} chain of command service). The localized
	 * resource is used as the key to a cache of {@link ComponentTemplate}s.
	 * <p/>
	 * If a template doesn't exist, then the missing ComponentTemplate is
	 * returned.
	 */
	public ComponentTemplate getTemplate(ComponentModel componentModel,
			Locale locale) {

		String componentName = componentModel.getComponentClassName();

		MultiKey key = new MultiKey(componentName, locale,
				request.getServerName());

		// First cache is key to resource.

		Resource resource = templateResources.get(key);

		if (resource == null) {
			if (componentName.startsWith(PACKAGE)) {
				resource = locateSiteTemplateResource(componentModel, locale);
			}

			if (resource == null) {
				resource = locateTemplateResource(componentModel, locale);
			}

			if (resource != null) {
				templateResources.put(key, resource);
			}
		}

		// If we haven't yet parsed the template into the cache, do so now.

		ComponentTemplate result = templates.get(resource);

		if (result == null) {
			result = parseTemplate(resource);
			templates.put(resource, result);
		}

		return result;
	}

	private ComponentTemplate parseTemplate(Resource r) {
		// In a race condition, we may parse the same template more than once.
		// This will likely add
		// the resource to the tracker multiple times. Not likely this will
		// cause a big issue.
		ComponentTemplate result = missingTemplate;

		if (r.exists()) {
			tracker.add(r.toURL());
			result = parser.parseTemplate(r);
		}

		return result;
	}

	private Resource locateTemplateResource(ComponentModel initialModel,
			Locale locale) {
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

		return initialModel.getBaseResource().withExtension(
				TapestryConstants.TEMPLATE_EXTENSION);
	}

	private Resource locateSiteTemplateResource(ComponentModel model,
			Locale locale) {

		String componentName = model.getComponentClassName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Retrieve template for component class name : "
					+ componentName + ", if starts with : " + PACKAGE);
		}

		Resource templateBaseResource = model.getBaseResource().withExtension(
				"tml");

		String templatePath = templateBaseResource.getPath();
		String templateFile = templatePath.substring(templatePath.lastIndexOf("/") + 1);

		PrivateResource resource = resourceService.getTemplateResource(null,
				templateFile);

		LOGGER.debug("Try to load template override for: " + templateFile);

		if ((resource != null) && resource.exists()) {
			LOGGER.debug("Template override: " + templateFile + " found.");
			return new T5FileResource(resource.getFile());
		}

		LOGGER.debug("Template override: " + templateFile + " doesn't exist.");

		return null;
	}

	/**
	 * Checks to see if any parsed resource has changed. If so, then all
	 * internal caches are cleared, and an invalidation event is fired. This is
	 * brute force ... a more targeted dependency management strategy may come
	 * later.
	 */
	public void checkForUpdates() {
		if (tracker.containsChanges()) {
			tracker.clear();
			templateResources.clear();
			templates.clear();
			fireInvalidationEvent();
		}
	}

	public InvalidationEventHub getInvalidationEventHub() {
		return this;
	}
}
