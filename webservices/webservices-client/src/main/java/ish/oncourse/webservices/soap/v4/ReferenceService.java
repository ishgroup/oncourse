
/*
 * 
 */

package ish.oncourse.webservices.soap.v4;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.3.1
 * Mon Jan 24 16:14:27 EST 2011
 * Generated source version: 2.3.1
 * 
 */


@WebServiceClient(name = "ReferenceService", 
                  wsdlLocation = "file:/Users/marek/Development/ish-svn/willow/webservices/webservices-web/src/main/wsdl/v4_reference.wsdl",
                  targetNamespace = "http://v4.soap.webservices.oncourse.ish/") 
public class ReferenceService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://v4.soap.webservices.oncourse.ish/", "ReferenceService");
    public final static QName ReferencePort = new QName("http://v4.soap.webservices.oncourse.ish/", "ReferencePort");
    static {
        URL url = null;
        try {
            url = new URL("file:/Users/marek/Development/ish-svn/willow/webservices/webservices-web/src/main/wsdl/v4_reference.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:/Users/marek/Development/ish-svn/willow/webservices/webservices-web/src/main/wsdl/v4_reference.wsdl");
            // e.printStackTrace();
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
