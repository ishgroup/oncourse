package ish.oncourse.willow.editor

import com.google.inject.*
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.milton.servlet.MiltonFilter
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.log.LogAppInfo
import ish.oncourse.willow.editor.services.RequestFilter
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.services.access.AuthenticationFilter
import ish.oncourse.willow.editor.services.access.AuthenticationService
import ish.oncourse.willow.editor.services.access.UserService
import ish.oncourse.willow.editor.services.access.ZKSessionManager
import ish.oncourse.willow.editor.v1.service.impl.AuthApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.BlockApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.MenuApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.PageApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.RedirectApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.SettingsApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.ThemeApiServiceImpl
import ish.oncourse.willow.editor.v1.service.impl.VersionApiServiceImpl
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

import javax.servlet.FilterConfig
import javax.servlet.ServletException

class EditorApiModule extends ConfigModule {
    
    private static final String ROOT_URL_PATTERN = '/*'
    private static final String WEBDAV_URL_PATTERN = '/webdav/*'
    private static final String MILTON_CONFIGURATOR_PARAM = 'milton.configurator'
    private static final String MILTON_CONFIGURATOR_CLASS = 'ish.oncourse.willow.editor.webdav.Configurator'
    public static final String INJECTOR_PROPERTY = 'injector'

    private static final TypeLiteral<MappedServlet<ISHHealthCheckServlet>> ISH_HEALTH_CHECK_SERVLET =
            new TypeLiteral<MappedServlet<ISHHealthCheckServlet>>() {
            }
    
    private static final TypeLiteral<MappedFilter<MiltonFilter>> MILTON_FILTER =
            new TypeLiteral<MappedFilter<MiltonFilter>>() {
            }
    private static final TypeLiteral<MappedFilter<RequestFilter>> REQUEST_FILTER =
            new TypeLiteral<MappedFilter<RequestFilter>>() {
            }


    void configure(Binder binder) {
        CayenneModule.extend(binder).addModule(new WillowCayenneModuleBuilder().build())
        CayenneModule.extend(binder).addModule(JCacheModule)
        JettyModule.extend(binder)
                .addMappedFilter(REQUEST_FILTER)
                .addMappedServlet(ISH_HEALTH_CHECK_SERVLET)
                .addMappedFilter(MILTON_FILTER)

        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(AuthenticationFilter)
        CXFModule.contributeResources(binder).addBinding().to(AuthApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(PageApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(BlockApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(ThemeApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(SettingsApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(VersionApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(MenuApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(RedirectApiServiceImpl)

        binder.bind(RequestService)
        binder.bind(ZKSessionManager)
        binder.bind(ICayenneService).to(CayenneService)
        binder.bind(AuthenticationService)
        binder.bind(UserService)
    }

    @Singleton
    @Provides
    MappedServlet<ISHHealthCheckServlet> createHealthCheckServlet(Injector injector) {
        LogAppInfo info = new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(LogAppInfo.DATA_SOURSE_NAME))
        info.log()
        new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME)
    }
    
    @Singleton
    @Provides
    MappedFilter<MiltonFilter> createMiltonFilter(Injector injector) {
        return new MappedFilter<MiltonFilter>(new MiltonFilter() {
            @Override 
            void init(FilterConfig config) throws ServletException {
                config.servletContext.setAttribute(INJECTOR_PROPERTY, injector)
                super.init(config)
            }
        }, Collections.singleton(WEBDAV_URL_PATTERN), MiltonFilter.simpleName, [(MILTON_CONFIGURATOR_PARAM) : MILTON_CONFIGURATOR_CLASS], 0)
    }
    
    @Singleton
    @Provides
    MappedFilter<RequestFilter> createRequestFilter() {
         new MappedFilter<RequestFilter>(new RequestFilter(),
                Collections.singleton(ROOT_URL_PATTERN), RequestFilter.simpleName, 0)
    }
}
