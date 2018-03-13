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
import ish.oncourse.configuration.Configuration;
import ish.oncourse.configuration.ISHHealthCheckServlet;
import ish.oncourse.scheduler.ScheduledService;
import ish.oncourse.scheduler.zookeeper.ZookeeperExecutor;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.solr.BuildSolrClient;
import ish.oncourse.solr.reindex.ReindexCoursesJob;
import ish.oncourse.solr.reindex.ReindexSuburbsJob;
import ish.oncourse.solr.reindex.ReindexTagsJob;
import ish.oncourse.tapestry.WillowModuleDef;
import ish.oncourse.tapestry.WillowTapestryFilter;
import ish.oncourse.tapestry.WillowTapestryFilterBuilder;
import ish.oncourse.util.log.LogAppInfo;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tapestry5.internal.spring.SpringModuleDef;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;
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

		//Multibinder.newSetBinder(binder, Key.get(ScheduledService.class)).addBinding().to(ScheduledService.class).asEagerSingleton();
	}
	
    @Provides
    public ScheduledService createScheduledService(ServerRuntime runtime) {
        ObjectContext context = runtime.newContext();

		Properties appProperties = Configuration.loadProperties();

        ZookeeperExecutor courseReindexExecutor = ZookeeperExecutor.valueOf(new ReindexCoursesJob(context, BuildSolrClient.instance(appProperties).build()), Configuration.getValue(ZK_HOST), REINDEEX_PATH + "course");
        ZookeeperExecutor tagReindexExecutor = ZookeeperExecutor.valueOf(new ReindexTagsJob(context, BuildSolrClient.instance(appProperties).build()), Configuration.getValue(ZK_HOST), REINDEEX_PATH + "tag");
        ZookeeperExecutor suburbReindexExecutor = ZookeeperExecutor.valueOf(new ReindexSuburbsJob(context, BuildSolrClient.instance(appProperties).build()), Configuration.getValue(ZK_HOST), REINDEEX_PATH + "suburb");

        return ScheduledService.valueOf(courseReindexExecutor, tagReindexExecutor, suburbReindexExecutor).start();
    }
}