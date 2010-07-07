package ish.oncourse.ui.services.template;

import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cache.CachedObjectProvider;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.ioc.Invocation;
import org.apache.tapestry5.ioc.MethodAdvice;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.ComponentModel;

/**
 * Template source advisor for custom template location resolution.
 * 
 * @author Various
 */
public class PerSiteComponentTemplateSourceAdvice implements MethodAdvice {

	private static final Logger LOGGER = Logger
			.getLogger(PerSiteComponentTemplateSourceAdvice.class);

	private static final String PACKAGE = "ish.oncourse.ui";

	@Inject
	private transient ICacheService cacheService;

	@Inject
	private transient TemplateParser templateParser;

	@Inject
	private transient IWebSiteService webSiteService;

	@Inject
	private transient IResourceService resourceService;

	public void advise(Invocation invocation) {
		ComponentModel model = (ComponentModel) invocation.getParameter(0);
		Locale locale = (Locale) invocation.getParameter(1);

		ComponentTemplate template = overriddenTemplate(model, locale);

		if (template != null) {
			LOGGER.debug("Override Template invoked");
			invocation.overrideResult(template);
		} else {
			LOGGER.debug("Original Template invoked");
			invocation.proceed();
		}
	}

	private ComponentTemplate overriddenTemplate(
			final ComponentModel componentModel, final Locale locale) {

		String componentName = componentModel.getComponentClassName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Retrieve template for component class name : "
					+ componentName + ", if starts with : " + PACKAGE);
		}

		if (!componentName.startsWith(PACKAGE)) {
			return null;
		}

		String key = createTemplateKey(componentName);

		return cacheService.get(key,
				new CachedObjectProvider<ComponentTemplate>() {
					public ComponentTemplate create() {
						return createOverriddenTemplate(componentModel, locale);
					}
				}, CacheGroup.TEMPLATE_OVERRIDE);
	}

	private ComponentTemplate createOverriddenTemplate(
			final ComponentModel componentModel, final Locale locale) {

		Resource templateResource = locateTemplateResource(componentModel,
				locale);

		if (templateResource != null) {
			return templateParser.parseTemplate(templateResource);
		}

		return null;
	}

	private Resource locateTemplateResource(ComponentModel model, Locale locale) {

		Resource templateBaseResource = model.getBaseResource().withExtension(
				"tml");

		String templatePath = templateBaseResource.getPath();
		String templateFile = templatePath.substring(templatePath
				.lastIndexOf("/") + 1);

		PrivateResource resource = resourceService.getTemplateResource("",
				templateFile);

		LOGGER.debug("Try to load template override for: " + templateFile);

		if (resource.exists()) {
			return new T5FileResource(resource.getFile());
		}

		LOGGER.debug("Template override: " + templateFile
				+ " doesn't exist.");

		return null;
	}

	private String createTemplateKey(String componentName) {
		return PerSiteComponentTemplateSourceAdvice.class.getSimpleName() + ":"
				+ webSiteService.getCurrentWebSite().getSiteIdentifier() + "@"
				+ componentName;
	}
}
