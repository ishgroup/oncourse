package ish.oncourse.willow

import com.google.inject.Binder
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedServlet
import ish.math.MoneyType
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.cxf.CXFModule
import ish.oncourse.willow.cache.JCacheModule
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.cayenne.ISHObjectContextFactory
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.checkout.corporatepass.CorporatePassApiImpl
import ish.oncourse.willow.filters.ContactCredentialsValidator
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.filters.SearchFilter
import ish.oncourse.willow.preference.PreferenceApiImpl
import ish.oncourse.willow.search.SearchApiImpl
import ish.oncourse.willow.search.SearchService
import ish.oncourse.willow.service.impl.*
import org.apache.cayenne.configuration.ObjectContextFactory
import org.apache.cayenne.configuration.server.ServerModule
import org.apache.cayenne.di.Module
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

class WillowApiModule extends ConfigModule {
    void configure(Binder binder) {
        CayenneModule.extend(binder).addModule(WillowApiCayenneModule)
        CayenneModule.extend(binder).addModule(JCacheModule)
        JettyModule.extend(binder).addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME))
        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(CourseClassesApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(ContactApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(ProductsApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(PromotionApiServiceImpl)
        CXFModule.contributeResources(binder).addBinding().to(CollegeService)
        CXFModule.contributeResources(binder).addBinding().to(RequestFilter)
        CXFModule.contributeResources(binder).addBinding().to(ContactCredentialsValidator)
        CXFModule.contributeResources(binder).addBinding().to(ContactCredentialsValidator)
        CXFModule.contributeResources(binder).addBinding().to(SearchService)
        CXFModule.contributeResources(binder).addBinding().to(SearchApiImpl)
        CXFModule.contributeResources(binder).addBinding().to(SearchFilter)
        CXFModule.contributeResources(binder).addBinding().to(CheckoutApiImpl)
        CXFModule.contributeResources(binder).addBinding().to(CorporatePassApiImpl)
        CXFModule.contributeResources(binder).addBinding().to(PreferenceApiImpl)
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
