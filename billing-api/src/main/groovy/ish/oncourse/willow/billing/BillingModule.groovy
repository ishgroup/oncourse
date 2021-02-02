package ish.oncourse.willow.billing

import com.google.inject.Binder
import io.bootique.ConfigModule
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.willow.billing.filter.SessionFilter
import ish.oncourse.willow.billing.filter.ZKSessionManager
import ish.oncourse.willow.billing.service.impl.BillingApiImpl
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature 

class BillingModule extends ConfigModule {
    
    @Override
    void configure(Binder binder) {
        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(SessionFilter)
        CXFModule.contributeResources(binder).addBinding().to(ZKSessionManager)
        CXFModule.contributeResources(binder).addBinding().to(BillingApiImpl)
        CXFModule.contributeFeatures(binder)
    }
}

