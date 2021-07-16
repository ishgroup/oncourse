package ish.oncourse.willow.billing

import com.google.inject.Binder
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.api.request.RequestFilter
import ish.oncourse.api.request.RequestModule
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.configuration.Configuration
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.services.s3.S3Service
import ish.oncourse.willow.billing.filter.CORSFilter
import ish.oncourse.willow.billing.filter.GuestSessionFilter
import ish.oncourse.willow.billing.filter.UserSessionFilter
import ish.oncourse.willow.billing.filter.ZKSessionManager
import ish.oncourse.willow.billing.service.impl.BillingApiImpl
import ish.oncourse.willow.billing.service.impl.WebSiteApiImpl
import ish.oncourse.willow.billing.website.WebSiteService
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

import static ish.oncourse.configuration.Configuration.AdminProperty.STORAGE_ACCESS_ID
import static ish.oncourse.configuration.Configuration.AdminProperty.STORAGE_ACCESS_KEY 

class BillingModule extends ConfigModule {

    private static final TypeLiteral<MappedFilter<RequestFilter>> CORS_FILTER =
            new TypeLiteral<MappedFilter<CORSFilter>>() {
            }
    @Override
    void configure(Binder binder) {
        binder.bind(ZKSessionManager)
        CayenneModule.extend(binder).addModule(new WillowCayenneModuleBuilder().build())

        binder.bind(ICayenneService).to(CayenneService)

        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(GuestSessionFilter)
        CXFModule.contributeResources(binder).addBinding().to(UserSessionFilter)
        CXFModule.contributeResources(binder).addBinding().to(WebSiteService)
        CXFModule.contributeResources(binder).addBinding().to(BillingApiImpl)
        CXFModule.contributeResources(binder).addBinding().to(WebSiteApiImpl)
        

        CXFModule.contributeFeatures(binder)
        JettyModule.extend(binder).addMappedFilter(CORS_FILTER)
    }

    @Provides
    IS3Service createS3() {

        String s3AccessId = Configuration.getValue(STORAGE_ACCESS_ID)
        String s3AccessKey = Configuration.getValue(STORAGE_ACCESS_KEY)

        if (s3AccessId == null || s3AccessKey == null) {
            throw new IllegalStateException("S3 administrative account is not set up.");
        }

        return new S3Service(s3AccessId, s3AccessKey)
    }

    @Singleton
    @Provides
    MappedFilter<CORSFilter> createRequestFilter() {
        new MappedFilter<CORSFilter>(new CORSFilter(),
                Collections.singleton(RequestModule.ROOT_URL_PATTERN), CORSFilter.simpleName, 0)
    }
    
}

