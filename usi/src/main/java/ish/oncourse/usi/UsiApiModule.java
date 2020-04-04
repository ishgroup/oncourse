package ish.oncourse.usi;

import com.google.inject.*;
import com.sun.xml.wss.XWSSecurityException;
import io.bootique.ConfigModule;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import ish.oncourse.api.cxf.CXFModule;
import ish.oncourse.configuration.ISHHealthCheckServlet;
import ish.oncourse.usi.rest.UsiRestService;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature;

import javax.xml.stream.XMLStreamException;

public class UsiApiModule extends ConfigModule {


    private static final TypeLiteral<MappedServlet<ISHHealthCheckServlet>> ISH_HEALTH_CHECK_SERVLET =
            new TypeLiteral<MappedServlet<ISHHealthCheckServlet>>() {
            };



    public void configure(Binder binder) {

        JettyModule.extend(binder).addMappedServlet(ISH_HEALTH_CHECK_SERVLET);

        CXFModule.contributeResources(binder).addBinding().to(JAXRSBeanValidationFeature.class);
        CXFModule.contributeResources(binder).addBinding().to(UsiRestService.class);
        CXFModule.contributeFeatures(binder);

    }
    @Singleton
    @Provides
    USIService createUSIService(Injector injector) throws XMLStreamException, XWSSecurityException {
        return new USIServiceBuilder().buildService();
    }

    @Singleton
    @Provides
    MappedServlet<ISHHealthCheckServlet> createHealthCheckServlet(Injector injector) {
        return new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME);
    }

}
