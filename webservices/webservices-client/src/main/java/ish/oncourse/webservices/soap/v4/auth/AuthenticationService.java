
/*
 * 
 */

package ish.oncourse.webservices.soap.v4.auth;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.3.1
 * Fri Jan 28 10:48:34 EET 2011
 * Generated source version: 2.3.1
 * 
 */


@WebServiceClient(name = "AuthenticationService", 
                  wsdlLocation = "file:/Users/anton/willow/code/webservices/webservices-client/src/main/wsdl/v4_auth.wsdl",
                  targetNamespace = "http://auth.v4.soap.webservices.oncourse.ish/") 
public class AuthenticationService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://auth.v4.soap.webservices.oncourse.ish/", "AuthenticationService");
    public final static QName AuthenticationPort = new QName("http://auth.v4.soap.webservices.oncourse.ish/", "AuthenticationPort");
    static {
        URL url = null;
        try {
            url = new URL("file:/Users/anton/willow/code/webservices/webservices-client/src/main/wsdl/v4_auth.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:/Users/anton/willow/code/webservices/webservices-client/src/main/wsdl/v4_auth.wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public AuthenticationService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public AuthenticationService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public AuthenticationService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns AuthenticationPortType
     */
    @WebEndpoint(name = "AuthenticationPort")
    public AuthenticationPortType getAuthenticationPort() {
        return super.getPort(AuthenticationPort, AuthenticationPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns AuthenticationPortType
     */
    @WebEndpoint(name = "AuthenticationPort")
    public AuthenticationPortType getAuthenticationPort(WebServiceFeature... features) {
        return super.getPort(AuthenticationPort, AuthenticationPortType.class, features);
    }

}
