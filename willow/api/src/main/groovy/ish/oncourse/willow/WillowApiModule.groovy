package ish.oncourse.willow

import com.google.inject.Binder
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import ish.math.MoneyType
import ish.oncourse.cxf.CXFModule
import ish.oncourse.willow.cache.JCacheConfigurationFactory
import ish.oncourse.willow.cache.JCacheModule
import ish.oncourse.willow.cache.JCacheQueryCache
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import ish.oncourse.willow.service.impl.CourseClassesApiServiceImpl
import ish.oncourse.willow.service.impl.ProductsApiServiceImpl
import ish.oncourse.willow.service.impl.PromotionApiServiceImpl
import org.apache.cayenne.configuration.Constants
import org.apache.cayenne.di.Module
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

class WillowApiModule extends ConfigModule {
    void configure(Binder binder) {
        CayenneModule.contributeModules(binder).addBinding().to(WillowApiCayenneModule)
        CayenneModule.contributeModules(binder).addBinding().to(JCacheModule)
        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(CourseClassesApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(ContactApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(ProductsApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(PromotionApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(CollegeService)
    }

    static class WillowApiCayenneModule implements Module {
        @Override
        void configure(org.apache.cayenne.di.Binder binder) {
            binder.bindList(Constants.SERVER_DEFAULT_TYPES_LIST).add(new MoneyType())
        }
    }
}
