/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services;

import ish.oncourse.cayenne.cache.ICacheEnabledService;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;

import javax.sql.DataSource;

/**
 * User: akoiro
 * Date: 14/12/17
 */
public class ModuleBinder {
	private BindWebSiteServices bindWebSiteServices = new BindWebSiteServices();
	private BindTapestryServices bindTapestryServices = new BindTapestryServices();

	private ServiceBuilder<ServerRuntime> serverRuntime = resources -> resources.getService(ServerRuntime.class);
	private ServiceBuilder<DataSource> dataSource = resources -> resources.getService(DataSource.class);
	private ServiceBuilder<ICacheEnabledService> cacheEnabledService = resources -> resources.getService(ICacheEnabledService.class);


	public void bind(ServiceBinder binder) {

		binder.bind(ServerRuntime.class, serverRuntime);

		binder.bind(DataSource.class, dataSource);


		BinderFunctions.bindEntityServices(binder);
		BinderFunctions.bindEnvServices(binder, "services", true);
		BinderFunctions.bindPaymentGatewayServices(binder);
		BinderFunctions.bindReferenceServices(binder);
		bindWebSiteServices.bind(binder);
		bindTapestryServices.bind(binder);

		binder.bind(ICacheEnabledService.class, cacheEnabledService);
	}

	public ModuleBinder serverRuntime(ServiceBuilder<ServerRuntime> serverRuntime) {
		this.serverRuntime = serverRuntime;
		return this;
	}

	public ModuleBinder dataSource(ServiceBuilder<DataSource> dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public ModuleBinder bindWebSiteServices(BindWebSiteServices bindWebSiteServices) {
		this.bindWebSiteServices = bindWebSiteServices;
		return this;
	}

	public ModuleBinder bindTapestryServices(BindTapestryServices bindTapestryServices) {
		this.bindTapestryServices = bindTapestryServices;
		return this;
	}


	public ModuleBinder cacheEnabledService(ServiceBuilder<ICacheEnabledService> cacheEnabledService) {
		this.cacheEnabledService = cacheEnabledService;
		return this;
	}

}
