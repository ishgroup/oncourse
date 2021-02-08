package ish.oncourse.willow.billing

import com.google.inject.Binder
import com.google.inject.Provides
import io.bootique.ConfigModule
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.configuration.Configuration
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.services.s3.S3Service
import ish.oncourse.willow.billing.filter.SessionFilter
import ish.oncourse.willow.billing.filter.ZKSessionManager
import ish.oncourse.willow.billing.service.impl.BillingApiImpl
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

import static ish.oncourse.configuration.Configuration.AdminProperty.STORAGE_ACCESS_ID
import static ish.oncourse.configuration.Configuration.AdminProperty.STORAGE_ACCESS_KEY 

class BillingModule extends ConfigModule {
    @Override
    void configure(Binder binder) {
        binder.bind(ZKSessionManager)

        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(SessionFilter)
        CXFModule.contributeResources(binder).addBinding().to(BillingApiImpl)
        CXFModule.contributeFeatures(binder)
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
    
    
}

