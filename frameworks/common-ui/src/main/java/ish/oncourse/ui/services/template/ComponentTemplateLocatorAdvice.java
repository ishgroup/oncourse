package ish.oncourse.ui.services.template;

import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;

import java.util.Locale;

import org.apache.log4j.Logger;
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
public class ComponentTemplateLocatorAdvice implements MethodAdvice {

	private static final Logger LOGGER = Logger
			.getLogger(ComponentTemplateLocatorAdvice.class);

	private static final String PACKAGE = "ish.oncourse.ui";

	@Inject
	private transient IResourceService resourceService;

	public void advise(Invocation invocation) {
		ComponentModel model = (ComponentModel) invocation.getParameter(0);
		Locale locale = (Locale) invocation.getParameter(1);

		Resource resource = locateTemplateResource(model, locale);

		if (resource != null) {
			LOGGER.debug("Override Template invoked");
			invocation.overrideResult(resource);
		} else {
			LOGGER.debug("Original Template invoked");
			invocation.proceed();
		}
	}

	private Resource locateTemplateResource(ComponentModel model, Locale locale) {

		String componentName = model.getComponentClassName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Retrieve template for component class name : "
					+ componentName + ", if starts with : " + PACKAGE);
		}

		if (!componentName.startsWith(PACKAGE)) {
			return null;
		}

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

		LOGGER.debug("Template override: " + templateFile + " doesn't exist.");

		return null;
	}
}
