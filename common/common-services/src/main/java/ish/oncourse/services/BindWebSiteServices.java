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
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.services.site.WebSiteVersionService;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;

/**
 * User: akoiro
 * Date: 9/12/17
 */
public class BindWebSiteServices {
	private Class<? extends IWebSiteService> webSiteServiceClass = WebSiteService.class;
	private Class<? extends IWebSiteVersionService> webSiteVersionServiceClass = WebSiteVersionService.class;

	private IWebSiteService webSiteService;
	private ServiceBuilder<IWebSiteService> webSiteServiceBuilder;


	public BindWebSiteServices webSiteService(Class<? extends IWebSiteService> webSiteServiceClass) {
		this.webSiteServiceClass = webSiteServiceClass;
		return this;
	}

	public BindWebSiteServices webSiteVersionService(Class<? extends IWebSiteVersionService> aClass) {
		this.webSiteVersionServiceClass = aClass;
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


	public void bind(ServiceBinder binder) {
		if (this.webSiteServiceBuilder != null) {
			binder.bind(IWebSiteService.class, this.webSiteServiceBuilder);
		} else if (this.webSiteService != null) {
			binder.bind(IWebSiteService.class, resources -> webSiteService);
		} else {
			binder.bind(IWebSiteService.class, webSiteServiceClass);
		}
		binder.bind(IWebSiteVersionService.class, webSiteVersionServiceClass);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		binder.bind(IWebNodeTypeService.class, WebNodeTypeService.class);
		binder.bind(IWebUrlAliasService.class, WebUrlAliasService.class);
		binder.bind(IWebMenuService.class, WebMenuService.class);

	}


}
