package ish.oncourse.willow

import com.google.inject.Binder
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import ish.math.MoneyType
import ish.oncourse.cxf.CXFModule
import ish.oncourse.willow.cache.JCacheModule
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.cayenne.ISHObjectContextFactory
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.SearchFilter
import ish.oncourse.willow.search.SearchApiImpl
import ish.oncourse.willow.search.SearchService
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import ish.oncourse.willow.filters.ContactCredentialsValidator
import ish.oncourse.willow.service.impl.CourseClassesApiServiceImpl
import ish.oncourse.willow.service.impl.HealthCheckApiServiceImpl
import ish.oncourse.willow.service.impl.ProductsApiServiceImpl
import ish.oncourse.willow.service.impl.PromotionApiServiceImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.service.impl.ShutdownService
import org.apache.cayenne.access.types.ExtendedType
import org.apache.cayenne.configuration.Constants
import org.apache.cayenne.configuration.ObjectContextFactory
import org.apache.cayenne.configuration.server.ServerModule
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
        CXFModule.contributeResources(binder).addBinding().to(HealthCheckApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(ShutdownService).asEagerSingleton()
        CXFModule.contributeResources(binder).addBinding().to(RequestFilter)
        CXFModule.contributeResources(binder).addBinding().to(ContactCredentialsValidator)
        CXFModule.contributeResources(binder).addBinding().to(ContactCredentialsValidator)
        CXFModule.contributeResources(binder).addBinding().to(SearchService)
        CXFModule.contributeResources(binder).addBinding().to(SearchApiImpl)
        CXFModule.contributeResources(binder).addBinding().to(SearchFilter)
        CXFModule.contributeResources(binder).addBinding().to(CheckoutApiImpl)
        CXFModule.contributeResources(binder).addBinding().to(CayenneService)

    }

    static class WillowApiCayenneModule implements Module {
        @Override
        void configure(org.apache.cayenne.di.Binder binder) {
            binder.bind(ObjectContextFactory).to(ISHObjectContextFactory)
            ServerModule.contributeUserTypes(binder).add(MoneyType)
        }
    }
}
