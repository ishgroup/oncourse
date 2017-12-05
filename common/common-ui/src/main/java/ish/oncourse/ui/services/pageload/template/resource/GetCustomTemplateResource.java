/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template.resource;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.Request;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetCustomTemplateResource {
	private static final Logger logger = LogManager.getLogger();

	private final IWebNodeService webNodeService;
	private final IResourceService resourceService;
	private final Request request;


	public GetCustomTemplateResource(IWebNodeService webNodeService, IResourceService resourceService, Request request) {
		this.webNodeService = webNodeService;
		this.resourceService = resourceService;
		this.request = request;
	}

	public Resource get(ComponentModel componentModel) {
		Resource resource = null;
		String componentName = componentModel.getComponentClassName();
		CustomTemplateDefinition ctd = (CustomTemplateDefinition) request.getAttribute(TextileUtil.CUSTOM_TEMPLATE_DEFINITION);
		if (ctd != null && !componentModel.getComponentClassName().endsWith(ctd.getTemplateClassName())) {
			ctd = null;
		}
		if (ctd != null && componentModel.getComponentClassName().endsWith(ctd.getTemplateClassName())) {
			logger.debug("Try to load user defined template {} override for {}.", ctd.getTemplateFileName(), componentName);
			resource = resourceService.getDbTemplateResource(webNodeService.getLayout(), ctd.getTemplateFileName());

			if (resource == null) {
				String templatePath = componentModel.getBaseResource().getPath();
				String templateFile = templatePath.substring(templatePath.lastIndexOf("/") + 1);
				resource = new ClasspathResource(templatePath.replace(templateFile, ctd.getTemplateFileName()));
			}
		}
		return resource;

	}
}
