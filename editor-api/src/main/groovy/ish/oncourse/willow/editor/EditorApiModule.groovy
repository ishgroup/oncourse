package ish.oncourse.willow.editor


import com.google.inject.Binder
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.api.zk.ZKProvider
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.services.access.*
import ish.oncourse.willow.editor.v1.service.impl.*
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature 

class EditorApiModule extends ConfigModule {
    
    void configure(Binder binder) {
        CayenneModule.extend(binder).addModule(new WillowCayenneModuleBuilder().build())
        CayenneModule.extend(binder).addModule(JCacheModule)


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
    
}
