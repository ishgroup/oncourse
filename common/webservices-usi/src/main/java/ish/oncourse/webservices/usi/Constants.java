package ish.oncourse.webservices.usi;

/**
 * The class was introduced to exclude from dependencies webservices-rt-2.0 because
 * webservices-rt-2.0 includes classes from other libraries and some of them conflict with our project dependencies
 * com.ctc.wstx.stax.WstxInputFactory - the class is used for cxf but webservices-rt-2.0 contains wrong version of the class.
 */
public interface Constants {
    //webservices-rt-2.0.jar com.sun.xml.wss.XWSSConstants
    public static final String CERTIFICATE_PROPERTY = "certificate";
    public static final String PRIVATEKEY_PROPERTY = "privatekey";


    //webservices-rt-2.0.jar com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration
    public static final String STS_ENDPOINT = "sts-endpoint";
    public static final String STS_WSDL_LOCATION = "sts-wsdlLocation";
    public static final String STS_SERVICE_NAME = "sts-service-name";
    public static final String STS_PORT_NAME = "sts-port-name";
    public static final String STS_NAMESPACE = "sts-namespace";
    public static final String LIFE_TIME = "LifeTime";

    //webservices-rt-2.0.jar com.sun.xml.ws.developer.JAXWSProperties
    public static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    public static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
}
