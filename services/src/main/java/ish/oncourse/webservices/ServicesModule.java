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
import ish.oncourse.configuration.ISHHealthCheckServlet;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.services.persistence.ISHObjectContextFactory;
import ish.oncourse.util.log.LogAppInfo;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cxf.transport.servlet.CXFServlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class ServicesModule extends ConfigModule {
	private static final String URL_PATTERN = "/*";

	public static final String DATA_SOURCE_NAME = "willow";

	private static final String TAPESTRY_APP_NAME = "app";

	private static final String CXF_APP_NAME = "cxf";
	private static final String CXF_PARAM_HIDE_SERVICE_LIST_PAGE = "hide-service-list-page";

	private static final TypeLiteral<MappedFilter<ServicesTapestryFilter>> TAPESTRY_FILTER =
			new TypeLiteral<MappedFilter<ServicesTapestryFilter>>() {
			};

	private static final TypeLiteral<MappedServlet<CXFServlet>> CXF_SERVLET =
			new TypeLiteral<MappedServlet<CXFServlet>>() {
			};


	@Singleton
	@Provides
	MappedFilter<ServicesTapestryFilter> createTapestryFilter(Injector injector) {
		new LogAppInfo().log(injector.getInstance(DataSourceFactory.class).forName(DATA_SOURCE_NAME));

		ServicesTapestryFilter filter = new ServicesTapestryFilter(injector);
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
		CayenneModule.extend(binder).addModule(new ServicesCayenneModule());
		JettyModule.extend(binder)
				.addMappedFilter(TAPESTRY_FILTER)
				.addMappedServlet(CXF_SERVLET)
				.addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME));

	}


	public static class ServicesCayenneModule implements org.apache.cayenne.di.Module {
		@Override
		public void configure(org.apache.cayenne.di.Binder binder) {
			binder.bindMap(Object.class, Constants.PROPERTIES_MAP).put(Constants.CI_PROPERTY, "true");
			binder.bind(ObjectContextFactory.class).toInstance(new ISHObjectContextFactory(false));
			binder.bind(QueryCache.class).toInstance(new NoopQueryCache());
			binder.bind(org.apache.cayenne.di.Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).toInstance(new NoopQueryCache());
		}
	}


}