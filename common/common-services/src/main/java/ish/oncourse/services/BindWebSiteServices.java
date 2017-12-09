/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services;

import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.alias.WebUrlAliasService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.content.WebContentService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.menu.WebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.node.WebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;

/**
 * User: akoiro
 * Date: 9/12/17
 */
public class BindWebSiteServices {
	private ServiceBinder binder;
	private Class<? extends IWebSiteService> webSiteServiceClass;
	private IWebSiteService webSiteService;
	private ServiceBuilder<IWebSiteService> webSiteServiceBuilder;

	public BindWebSiteServices binder(ServiceBinder binder) {
		this.binder = binder;
		return this;
	}

	public BindWebSiteServices webSiteService(Class<? extends IWebSiteService> webSiteServiceClass) {
		this.webSiteServiceClass = webSiteServiceClass;
		return this;
	}

	public BindWebSiteServices webSiteService(IWebSiteService webSiteService) {
		this.webSiteService = webSiteService;
		return this;
	}

	public BindWebSiteServices webSiteService(ServiceBuilder<IWebSiteService> webSiteServiceBuilder) {
		this.webSiteServiceBuilder = webSiteServiceBuilder;
		return this;
	}


	public void bind() {
		if (webSiteServiceClass != null) {
			binder.bind(IWebSiteService.class, webSiteServiceClass);
		} else if (this.webSiteService != null) {
			binder.bind(IWebSiteService.class, resources -> webSiteService);
		} else if (this.webSiteServiceBuilder != null) {
			binder.bind(IWebSiteService.class, this.webSiteServiceBuilder);
		}
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		binder.bind(IWebNodeTypeService.class, WebNodeTypeService.class);
		binder.bind(IWebUrlAliasService.class, WebUrlAliasService.class);
		binder.bind(IWebMenuService.class, WebMenuService.class);

	}


}
