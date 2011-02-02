package ish.oncourse.webservices.soap.v4;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.3.1
 * Wed Feb 02 17:48:38 EET 2011
 * Generated source version: 2.3.1
 * 
 */
 
@WebService(targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/", name = "ReplicationPortType")
@XmlSeeAlso({ish.oncourse.webservices.v4.stubs.replication.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ReplicationPortType {

    @WebResult(name = "result", targetNamespace = "", partName = "result")
    @WebMethod
    public ish.oncourse.webservices.v4.stubs.replication.ReplicationResult sendRecords(
        @WebParam(partName = "records", name = "records", targetNamespace = "")
        ish.oncourse.webservices.v4.stubs.replication.ReplicationRequest records
    );

    @WebResult(name = "parameters", targetNamespace = "", partName = "parameters")
    @WebMethod
    public ish.oncourse.webservices.v4.stubs.replication.ReplicationResult getRecords();
}
