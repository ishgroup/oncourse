package ish.oncourse.webservices.soap.v10;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.6.16
 * 2015-04-29T09:41:48.151+03:00
 * Generated source version: 2.6.16
 * 
 */
@WebService(targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", name = "ReplicationPortType")
@XmlSeeAlso({ish.oncourse.webservices.v10.stubs.replication.ObjectFactory.class})
public interface ReplicationPortType {

    @WebMethod(action = "getUnreplicatedEntities")
    @RequestWrapper(localName = "getUnreplicatedEntities", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v10.stubs.replication.GetUnreplicatedEntities")
    @ResponseWrapper(localName = "getUnreplicatedEntitiesResponse", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v10.stubs.replication.GetUnreplicatedEntitiesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<ish.oncourse.webservices.v10.stubs.replication.UnreplicatedEntitiesStub> getUnreplicatedEntities();

    @WebMethod(action = "authenticate")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "authNewCommunicationKey", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", partName = "newCommunicationKey")
    public long authenticate(
        @WebParam(partName = "securityCode", name = "authSecurityCode", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/")
        java.lang.String securityCode,
        @WebParam(partName = "lastCommunicationKey", name = "authLastCommunicationKey", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/")
        long lastCommunicationKey
    ) throws AuthFailure;

    @WebMethod(action = "getInstructions")
    @RequestWrapper(localName = "getInstructions", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v10.stubs.replication.GetInstructions")
    @ResponseWrapper(localName = "getInstructionsResponse", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v10.stubs.replication.GetInstructionsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<ish.oncourse.webservices.v10.stubs.replication.InstructionStub> getInstructions();

    @WebMethod(action = "getRecords")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "getRecordsResponse", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", partName = "parameters")
    public ish.oncourse.webservices.v10.stubs.replication.ReplicationRecords getRecords() throws ReplicationFault;

    @WebMethod(action = "sendRecords")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "sendRecordsResponse", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", partName = "result")
    public ish.oncourse.webservices.v10.stubs.replication.ReplicationResult sendRecords(
        @WebParam(partName = "records", name = "sendRecordsRequest", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v10.stubs.replication.ReplicationRecords records
    ) throws ReplicationFault;

    @WebMethod(action = "sendResults")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "resultsResponse", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", partName = "numberUpdated")
    public int sendResults(
        @WebParam(partName = "replResult", name = "resultsRequest", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v10.stubs.replication.ReplicationResult replResult
    ) throws ReplicationFault;

    @WebMethod(action = "confirmExecution")
    @Oneway
    @RequestWrapper(localName = "confirmExecution", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v10.stubs.replication.ConfirmExecution")
    public void confirmExecution(
        @WebParam(name = "id", targetNamespace = "")
        java.lang.Long id,
        @WebParam(name = "message", targetNamespace = "")
        java.lang.String message
    );
}
