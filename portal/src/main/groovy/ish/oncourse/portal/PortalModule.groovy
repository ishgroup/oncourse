/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal

import com.google.inject.*
import groovy.transform.CompileStatic
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.bootique.tapestry.di.InjectorModuleDef
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.ICacheEnabledService
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.tapestry.WillowModuleDef
import ish.oncourse.tapestry.WillowTapestryFilter
import ish.oncourse.tapestry.WillowTapestryFilterBuilder
import ish.oncourse.util.log.LogAppInfo
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.tapestry5.internal.spring.SpringModuleDef

import javax.servlet.Filter

import static ish.oncourse.util.log.LogAppInfo.DATA_SOURSE_NAME
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM

/**
 * User: akoiro
 * Date: 23/8/17
 */
@CompileStatic
class PortalModule extends ConfigModule {
    private static final String URL_PATTERN = "/*"

    private static final String TAPESTRY_APP_NAME = "app"

    public static final String DATA_SOURCE_NAME = DATA_SOURSE_NAME;

    private static final TypeLiteral<MappedFilter<WillowTapestryFilter>> TAPESTRY_FILTER =
            new TypeLiteral<MappedFilter<WillowTapestryFilter>>() {
            }


    @Singleton
    @Provides
    MappedFilter<WillowTapestryFilter> createTapestryFilter(Injector injector) {
        LogAppInfo info = new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(DATA_SOURCE_NAME))
        info.log()

        Filter filter = new WillowTapestryFilterBuilder()
                .moduleDefClass(SpringModuleDef.class)
                .moduleDef(new InjectorModuleDef(injector))
                .moduleDef(new WillowModuleDef(injector.getInstance(DataSourceFactory).forName(DATA_SOURSE_NAME),
                injector.getInstance(ServerRuntime), injector.getInstance(ICacheEnabledService)))
                .appPackage("ish.oncourse.portal")
                .initParam(CONFIG_LOCATION_PARAM, "classpath:application-context.xml")
                .build()
        return new MappedFilter<>(filter,
                Collections.singleton(URL_PATTERN),
                TAPESTRY_APP_NAME, 0
        )
    }

    @Override
    void configure(Binder binder) {
        CayenneModule.extend(binder)
                .addModule(new WillowCayenneModuleBuilder().build())
                .addModule(new JCacheModule())
        JettyModule.extend(binder)
                .addMappedFilter(TAPESTRY_FILTER)
                .addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME))
                .addStaticServlet("resources", URL_PATTERN)

    }
}