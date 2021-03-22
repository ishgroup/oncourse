package ish.oncourse.admin

import com.google.inject.*
import groovy.transform.CompileStatic
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.bootique.tapestry.di.InjectorModuleDef
import ish.oncourse.admin.services.WillowModuleDef
import ish.oncourse.admin.services.access.AuthenticationFilter
import ish.oncourse.admin.services.access.AuthenticationService
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.listeners.LiquibaseServletListener
import ish.oncourse.tapestry.WillowTapestryFilter
import ish.oncourse.tapestry.WillowTapestryFilterBuilder
import ish.oncourse.util.log.LogAppInfo
import org.apache.cayenne.configuration.server.ServerRuntime

import javax.servlet.Filter

@CompileStatic
class AdminModule extends ConfigModule {
    private static final String URL_PATTERN = "/*"

    private static final String TAPESTRY_APP_NAME = "app"

    public static final String DATA_SOURCE_NAME = LogAppInfo.DATA_SOURSE_NAME

    private static final TypeLiteral<MappedFilter<WillowTapestryFilter>> TAPESTRY_FILTER =
            new TypeLiteral<MappedFilter<WillowTapestryFilter>>() {
            }

    private static final TypeLiteral<MappedFilter<AuthenticationFilter>> AUTH_FILTER =
            new TypeLiteral<MappedFilter<AuthenticationFilter>>() {
            }


    @Singleton
    @Provides
    MappedFilter<WillowTapestryFilter> createTapestryFilter(Injector injector) {
        new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(DATA_SOURCE_NAME)).log()
        Filter filter = new WillowTapestryFilterBuilder()
                .moduleDef(new InjectorModuleDef(injector))
                .moduleDef(
                new WillowModuleDef(
                        injector.getInstance(DataSourceFactory).forName(LogAppInfo.DATA_SOURSE_NAME),
                        injector.getInstance(ServerRuntime)))
                .appPackage("ish.oncourse.admin").build()
        return new MappedFilter<>(filter,
                Collections.singleton(URL_PATTERN),
                TAPESTRY_APP_NAME, 1
        )
    }

    @Singleton
    @Provides
    MappedFilter<AuthenticationFilter> createAuthenticationFilter(AuthenticationService authenticationService) {
        new MappedFilter<AuthenticationFilter>(new AuthenticationFilter(authenticationService), Collections.singleton(URL_PATTERN), 0)
    }

    @Singleton
    @Provides
    LiquibaseServletListener createLiquibaseListener(DataSourceFactory factory) {
        new LiquibaseServletListener(factory.forName(LogAppInfo.DATA_SOURSE_NAME))
    }

    @Override
    void configure(Binder binder) {
        CayenneModule.extend(binder)
                .addModule(new WillowCayenneModuleBuilder().build())
                .addModule(new JCacheModule())
        JettyModule.extend(binder)
                .addMappedFilter(AUTH_FILTER)
                .addMappedFilter(TAPESTRY_FILTER)
                .addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME))
                .addStaticServlet("resources", URL_PATTERN)
                .addListener(LiquibaseServletListener.class)
    }
}
