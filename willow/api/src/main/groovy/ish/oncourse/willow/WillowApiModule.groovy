package ish.oncourse.willow

import com.google.inject.Binder
import io.bootique.ConfigModule
import ish.oncourse.cxf.CXFModule
import ish.oncourse.willow.service.*
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

/**
 * Created by akoira on 2/5/17.
 */
class WillowApiModule extends ConfigModule{
    void configure(Binder binder) {
        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeResources(binder).addBinding().to(ShoppingCartService)
        CXFModule.contributeResources(binder).addBinding().to(CourseClassService)
        CXFModule.contributeResources(binder).addBinding().to(ContactService)

    }
}
