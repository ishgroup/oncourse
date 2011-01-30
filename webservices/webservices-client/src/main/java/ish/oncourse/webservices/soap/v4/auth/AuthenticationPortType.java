package ish.oncourse.webservices.soap.v4.auth;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.3.1
 * Fri Jan 28 10:48:34 EET 2011
 * Generated source version: 2.3.1
 * 
 */
 
@WebService(targetNamespace = "http://auth.v4.soap.webservices.oncourse.ish/", name = "AuthenticationPortType")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface AuthenticationPortType {

    @WebResult(name = "parameters", targetNamespace = "", partName = "parameters")
    @WebMethod
    public long authenticate(
        @WebParam(partName = "securityCode", name = "securityCode", targetNamespace = "")
        java.lang.String securityCode,
        @WebParam(partName = "lastCommunicationKey", name = "lastCommunicationKey", targetNamespace = "")
        long lastCommunicationKey
    );

    @WebResult(name = "status", targetNamespace = "", partName = "status")
    @WebMethod
    public Status logout(
        @WebParam(partName = "communicationKey", name = "communicationKey", targetNamespace = "")
        long communicationKey
    );
}
