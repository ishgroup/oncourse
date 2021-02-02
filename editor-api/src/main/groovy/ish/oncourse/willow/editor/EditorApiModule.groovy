package ish.oncourse.willow.editor

import com.google.inject.*
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.milton.servlet.MiltonFilter
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.api.zk.ZKProvider
import ish.oncourse.willow.editor.services.access.*
import ish.oncourse.willow.editor.v1.service.impl.*
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

import javax.servlet.FilterConfig
import javax.servlet.ServletException 

class EditorApiModule extends ConfigModule {
    
    private static final String WEBDAV_URL_PATTERN = '/webdav/*'
    private static final String MILTON_CONFIGURATOR_PARAM = 'milton.configurator'
    private static final String MILTON_CONFIGURATOR_CLASS = 'ish.oncourse.willow.editor.webdav.Configurator'
    public static final String INJECTOR_PROPERTY = 'injector'


    
    private static final TypeLiteral<MappedFilter<MiltonFilter>> MILTON_FILTER =
            new TypeLiteral<MappedFilter<MiltonFilter>>() {
            }



    void configure(Binder binder) {
        CayenneModule.extend(binder).addModule(new WillowCayenneModuleBuilder().build())
        CayenneModule.extend(binder).addModule(JCacheModule)
        JettyModule.extend(binder)
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
        CXFModule.contributeResources(binder).addBinding().to(SpecialPageApiServiceImpl)
        CXFModule.contributeFeatures(binder).addBinding().to(PostProcessFeature)

        binder.bind(ZKProvider)
        binder.bind(ZKSessionManager)
        binder.bind(ICayenneService).to(CayenneService)
        binder.bind(AuthenticationService)
        binder.bind(UserService)
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
    
}
