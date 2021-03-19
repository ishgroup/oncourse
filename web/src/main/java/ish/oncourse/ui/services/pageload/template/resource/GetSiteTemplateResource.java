/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template.resource;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.resource.IResourceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import static ish.oncourse.ui.services.pageload.PageLoadModule.PACKAGE;
import static org.apache.tapestry5.TapestryConstants.TEMPLATE_EXTENSION;

/**
 * User: akoiro
 * Date: 21/11/17
 */
public class GetSiteTemplateResource {
	private static final Logger logger = LogManager.getLogger();

	private final GetCustomTemplateResource getCustomTemplateResource;
	private final GetDefaultTemplateResource getDefaultTemplateResource;
	private final IWebNodeService webNodeService;
	private final IResourceService resourceService;

	public GetSiteTemplateResource(IWebNodeService webNodeService,
								   IResourceService resourceService,
								   ComponentResourceLocator componentResourceLocator) {
		this.getCustomTemplateResource = new GetCustomTemplateResource(webNodeService, resourceService);
		this.getDefaultTemplateResource = new GetDefaultTemplateResource(componentResourceLocator);
		this.webNodeService = webNodeService;
		this.resourceService = resourceService;
	}

	private boolean isErrorPage(String componentName) {
		try {
			Class<?> componentClass = Class.forName(componentName);
			return ExceptionReporter.class.isAssignableFrom(componentClass);
		} catch (ClassNotFoundException e) {
			logger.warn("Component class not found.", e);
			return false;
		}
	}

	private boolean isSiteTemplate(String componentName) {
		return !isErrorPage(componentName) && componentName.startsWith(PACKAGE);
	}


	public Resource get(ComponentModel componentModel, ComponentResourceSelector selector) {
		String componentName = componentModel.getComponentClassName();
		WebSiteLayout layout = webNodeService.getLayout();
		logger.debug("Retrieve template for component class name: {}, if starts with: {}", componentName, PACKAGE);

		if (!isSiteTemplate(componentName))
			return getDefaultTemplateResource.get(componentModel, selector);
		if (layout == null)
			return getDefaultTemplateResource.get(componentModel, selector);

		Resource resource = getCustomTemplateResource.get(componentModel, selector);
		if (resource != null)
			return resource;

		Resource templateBaseResource = componentModel.getBaseResource().withExtension(TEMPLATE_EXTENSION);
		String templatePath = templateBaseResource.getPath();
		String templateFile = templatePath.substring(templatePath.lastIndexOf("/") + 1);

		logger.debug("Try to load template override for: {}", templateFile);
		resource = resourceService.getDbTemplateResource(layout, templateFile);

		if (resource != null) {
			return resource;
		}

		logger.debug("Template override: {} doesn't exist.", templateFile);
		return getDefaultTemplateResource.get(componentModel, selector);
	}
}
