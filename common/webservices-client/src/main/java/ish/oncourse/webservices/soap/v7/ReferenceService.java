package ish.oncourse.webservices.soap.v7;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.2.6
 * 2020-10-29T19:55:37.147+11:00
 * Generated source version: 3.2.6
 *
 */
@WebServiceClient(name = "ReferenceService",
                  wsdlLocation = "/wsdl/v7_reference.wsdl",
                  targetNamespace = "http://ref.v7.soap.webservices.oncourse.ish/")
public class ReferenceService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://ref.v7.soap.webservices.oncourse.ish/", "ReferenceService");
    public final static QName ReferencePort = new QName("http://ref.v7.soap.webservices.oncourse.ish/", "ReferencePort");
    static {
        URL url = ReferenceService.class.getResource("/wsdl/v7_reference.wsdl");
        if (url == null) {
            url = ReferenceService.class.getClassLoader().getResource("/wsdl/v7_reference.wsdl");
        }
        if (url == null) {
            java.util.logging.Logger.getLogger(ReferenceService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "/wsdl/v7_reference.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ReferenceService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ReferenceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ReferenceService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public ReferenceService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public ReferenceService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public ReferenceService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns ReferencePortType
     */
    @WebEndpoint(name = "ReferencePort")
    public ReferencePortType getReferencePort() {
        return super.getPort(ReferencePort, ReferencePortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ReferencePortType
     */
    @WebEndpoint(name = "ReferencePort")
    public ReferencePortType getReferencePort(WebServiceFeature... features) {
        return super.getPort(ReferencePort, ReferencePortType.class, features);
    }

}
