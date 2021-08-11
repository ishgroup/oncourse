package ish.oncourse.willow.portal

import com.google.inject.Binder
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jetty.JettyModule
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.portal.auth.ZKSessionManager
import ish.oncourse.willow.portal.filter.SessionFilter
import ish.oncourse.willow.portal.service.impl.AuthenticationApiImpl
import ish.oncourse.willow.portal.service.impl.CourseClassApiImpl
import ish.oncourse.willow.portal.service.impl.SessionApiImpl
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

class PortalModule extends ConfigModule {

    private static final String ROOT_URL_PATTERN = "/*";

    
    @Override
    void configure(Binder binder) {
        binder.bind(ZKSessionManager)
        JettyModule.extend(binder)
                .addServlet(new ResourceServlet(),"resources", ROOT_URL_PATTERN)

        CayenneModule.extend(binder).addModule(new WillowCayenneModuleBuilder().build())

        binder.bind(ICayenneService).to(CayenneService)
        
        CXFModule.contributeResources(binder).addBinding().to(SessionFilter)

        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature)
        CXFModule.contributeFeatures(binder).addBinding().to(AuthenticationApiImpl)
        CXFModule.contributeFeatures(binder).addBinding().to(CourseClassApiImpl)
        CXFModule.contributeFeatures(binder).addBinding().to(SessionApiImpl)
        
        CXFModule.contributeFeatures(binder)
    }


}

