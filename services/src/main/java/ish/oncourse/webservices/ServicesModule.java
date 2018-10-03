/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import com.google.inject.*;
import com.google.inject.Binder;
import com.google.inject.Injector;
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
import ish.oncourse.solr.ReindexConstants;
import ish.oncourse.util.log.LogAppInfo;
import ish.oncourse.webservices.solr.ReindexServlet;
import ish.oncourse.webservices.solr.SolrUpdateCourseDocumentsListener;
import org.apache.cayenne.commitlog.CommitLogModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.di.Module;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.tapestry5.internal.spring.SpringModuleDef;

import java.util.*;

import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class ServicesModule extends ConfigModule {
	public static final String APP_PACKAGE = "ish.oncourse.webservices";
	
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


	private static final TypeLiteral<MappedServlet<ReindexServlet>> REINDEX_SERVLET =
			new TypeLiteral<MappedServlet<ReindexServlet>>() {
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
	
	@Singleton
	@Provides
	MappedServlet<ReindexServlet> createReindexServlet(SolrClient solrClient, ServerRuntime serverRuntime) {
		return new MappedServlet<>(new ReindexServlet(solrClient, serverRuntime), Collections.singleton(ReindexConstants.REINDEX_PATH));
	}
	
	@Singleton
	@Provides
	public CommitLogModuleEx createCommitLogModule(Injector injector) {
		return new CommitLogModuleEx(new SolrUpdateCourseDocumentsListener().injector(injector));
	}

	@Singleton
	@Provides
	public ServiceProvider createServiceProvider(MappedFilter<WillowTapestryFilter> filter) {
		return new ServiceProvider(filter);
	}
	
	@Override
	public void configure(Binder binder) {
		CayenneModule.extend(binder)
				.addModule(new WillowCayenneModuleBuilder().queryCache(new NoopQueryCache()).build())
				.addModule(CommitLogModuleEx.class);
		
		JettyModule.extend(binder)
				.addMappedFilter(TAPESTRY_FILTER)
				.addMappedServlet(CXF_SERVLET)
				.addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME))
				.addMappedServlet(REINDEX_SERVLET);
	}


	
	public static class ServiceProvider {

		WillowTapestryFilter tapestry;

		ServiceProvider(MappedFilter<WillowTapestryFilter> filter) {
			tapestry = filter.getFilter();
		}

		public <T> T get(Class<T> c) {
			return tapestry.getService(c);
		}

	}
	
	static class CommitLogModuleEx implements Module {

		private SolrUpdateCourseDocumentsListener listener;

		CommitLogModuleEx(SolrUpdateCourseDocumentsListener listener) {
			this.listener = listener;
		}

		@Override
		public void configure(org.apache.cayenne.di.Binder binder) {
			CommitLogModule.extend()
					.addListener(listener)
					.module().configure(binder);
		}
	} 
}