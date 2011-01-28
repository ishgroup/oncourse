package ish.oncourse.webservices.soap.v4;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.3.1
 * Fri Jan 28 16:12:49 EST 2011
 * Generated source version: 2.3.1
 * 
 */
 
@WebService(targetNamespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "ReferencePortType")
@XmlSeeAlso({ish.oncourse.webservices.v4.stubs.reference.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ReferencePortType {

    @WebResult(name = "maxVersion", targetNamespace = "", partName = "maxVersion")
    @WebMethod
    public long getMaximumVersion();

    @WebResult(name = "results", targetNamespace = "", partName = "results")
    @WebMethod
    public ish.oncourse.webservices.v4.stubs.reference.ReferenceResult getRecords(
        @WebParam(partName = "ishVersion", name = "ishVersion", targetNamespace = "")
        long ishVersion
    );
}
