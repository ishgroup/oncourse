/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import com.google.inject.*;
import io.bootique.ConfigModule;
import io.bootique.cayenne.CayenneModule;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.jetty.MappedServlet;
import io.bootique.tapestry.di.InjectorModuleDef;
import ish.oncourse.cayenne.WillowCayenneModuleBuilder;
import ish.oncourse.configuration.ISHHealthCheckServlet;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.tapestry.WillowModuleDef;
import ish.oncourse.tapestry.WillowTapestryFilter;
import ish.oncourse.tapestry.WillowTapestryFilterBuilder;
import ish.oncourse.util.log.LogAppInfo;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tapestry5.internal.spring.SpringModuleDef;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class ServicesModule extends ConfigModule {
	public static final String APP_PACKAGE = "ish.oncourse.webservices";

	public static final String REINDEEX_PATH = "/willow/solr/";

	private static final String URL_PATTERN = "/*";

	public static final String DATA_SOURCE_NAME = "willow";

	private static final String TAPESTRY_APP_NAME = "app";

	private static final String CXF_APP_NAME = "cxf";
	private static final String CXF_PARAM_HIDE_SERVICE_LIST_PAGE = "hide-service-list-page";

	private static final TypeLiteral<MappedFilter<WillowTapestryFilter>> TAPESTRY_FILTER =
			new TypeLiteral<MappedFilter<WillowTapestryFilter>>() {
			};

	private static final TypeLiteral<MappedServlet<CXFServlet>> CXF_SERVLET =
			new TypeLiteral<MappedServlet<CXFServlet>>() {
			};


	@Singleton
	@Provides
	MappedFilter<WillowTapestryFilter> createTapestryFilter(Injector injector) {
		LogAppInfo info = new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(LogAppInfo.DATA_SOURSE_NAME));
		info.log();

		WillowTapestryFilter filter = new WillowTapestryFilterBuilder()
				.moduleDefClass(SpringModuleDef.class)
				.moduleDef(new InjectorModuleDef(injector))
				.moduleDef(new WillowModuleDef(injector.getInstance(DataSourceFactory.class).forName(LogAppInfo.DATA_SOURSE_NAME),
						injector.getInstance(ServerRuntime.class)))
				.appPackage(APP_PACKAGE)
				.initParam(CONFIG_LOCATION_PARAM, "classpath:application-context.xml")
				.build();
		return new MappedFilter<>(filter, Collections.singleton(URL_PATTERN), TAPESTRY_APP_NAME, 0);
	}

	@Singleton
	@Provides
	MappedServlet<CXFServlet> createCXFServlet() {
		Map<String, String> params = new HashMap<>();
		params.put(CXF_PARAM_HIDE_SERVICE_LIST_PAGE, Boolean.TRUE.toString());
		return new MappedServlet<>(new CXFServlet(), Collections.singleton(URL_PATTERN), CXF_APP_NAME,
				params);
	}


	@Override
	public void configure(Binder binder) {
		CayenneModule.extend(binder).addModule(new WillowCayenneModuleBuilder().queryCache(new NoopQueryCache()).build());
		JettyModule.extend(binder)
				.addMappedFilter(TAPESTRY_FILTER)
				.addMappedServlet(CXF_SERVLET)
				.addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME));
	}
}