package ish.oncourse.webservices.soap.v16;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.6.16
 * 2017-12-21T14:04:42.659+03:00
 * Generated source version: 2.6.16
 * 
 */
@WebServiceClient(name = "ReplicationService", 
                  wsdlLocation = "/wsdl/v16_replication.wsdl",
                  targetNamespace = "http://repl.v16.soap.webservices.oncourse.ish/") 
public class ReplicationService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://repl.v16.soap.webservices.oncourse.ish/", "ReplicationService");
    public final static QName ReplicationPort = new QName("http://repl.v16.soap.webservices.oncourse.ish/", "ReplicationPort");
    public final static QName PaymentPortType = new QName("http://repl.v16.soap.webservices.oncourse.ish/", "PaymentPortType");
    static {
        URL url = ReplicationService.class.getResource("/wsdl/v16_replication.wsdl");
        if (url == null) {
            url = ReplicationService.class.getClassLoader().getResource("/wsdl/v16_replication.wsdl");
        } 
        if (url == null) {
            java.util.logging.Logger.getLogger(ReplicationService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "/wsdl/v16_replication.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public ReplicationService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ReplicationService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ReplicationService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ReplicationService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ReplicationService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ReplicationService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns ReplicationPortType
     */
    @WebEndpoint(name = "ReplicationPort")
    public ReplicationPortType getReplicationPort() {
        return super.getPort(ReplicationPort, ReplicationPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ReplicationPortType
     */
    @WebEndpoint(name = "ReplicationPort")
    public ReplicationPortType getReplicationPort(WebServiceFeature... features) {
        return super.getPort(ReplicationPort, ReplicationPortType.class, features);
    }
    /**
     *
     * @return
     *     returns PaymentPortType
     */
    @WebEndpoint(name = "PaymentPortType")
    public PaymentPortType getPaymentPortType() {
        return super.getPort(PaymentPortType, PaymentPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PaymentPortType
     */
    @WebEndpoint(name = "PaymentPortType")
    public PaymentPortType getPaymentPortType(WebServiceFeature... features) {
        return super.getPort(PaymentPortType, PaymentPortType.class, features);
    }

}
