package ish.oncourse.webservices.v25.stubs.replication;

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
 * This class was generated by Apache CXF 3.2.6
 * 2021-11-09T10:40:40.567+03:00
 * Generated source version: 3.2.6
 *
 */
@WebService(targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", name = "ReplicationPortType")
@XmlSeeAlso({ObjectFactory.class})
public interface ReplicationPortType {

    @WebMethod(action = "confirmExecution")
    @Oneway
    @RequestWrapper(localName = "confirmExecution", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v25.stubs.replication.ConfirmExecution")
    public void confirmExecution(
        @WebParam(name = "id", targetNamespace = "")
        java.lang.Long id,
        @WebParam(name = "message", targetNamespace = "")
        java.lang.String message
    );

    @WebMethod(action = "getRecords")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "getRecordsResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", partName = "parameters")
    public ReplicationRecords getRecords() throws ReplicationFault;

    @WebMethod(action = "sendRecords")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "sendRecordsResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", partName = "result")
    public ReplicationResult sendRecords(
        @WebParam(partName = "records", name = "sendRecordsRequest", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
        ReplicationRecords records
    ) throws ReplicationFault;

    @WebMethod(action = "sendResults")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "resultsResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", partName = "numberUpdated")
    public int sendResults(
        @WebParam(partName = "replResult", name = "resultsRequest", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
        ReplicationResult replResult
    ) throws ReplicationFault;

    @WebMethod(action = "getInstructions")
    @RequestWrapper(localName = "getInstructions", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v25.stubs.replication.GetInstructions")
    @ResponseWrapper(localName = "getInstructionsResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v25.stubs.replication.GetInstructionsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<ish.oncourse.webservices.v25.stubs.replication.InstructionStub> getInstructions();

    @WebMethod(action = "authenticate")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "authNewCommunicationKey", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", partName = "newCommunicationKey")
    public long authenticate(
        @WebParam(partName = "securityCode", name = "authSecurityCode", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
        java.lang.String securityCode,
        @WebParam(partName = "lastCommunicationKey", name = "authLastCommunicationKey", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
        long lastCommunicationKey
    ) throws AuthFailure;

    @WebMethod(action = "getRecordByInstruction")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @WebResult(name = "instructionResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", partName = "instructionResponse")
    public TransactionGroup getRecordByInstruction(
        @WebParam(partName = "instructionRequest", name = "instructionRequest", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
        java.lang.String instructionRequest
    ) throws ReplicationFault;

    @WebMethod(action = "getUnreplicatedEntities")
    @RequestWrapper(localName = "getUnreplicatedEntities", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v25.stubs.replication.GetUnreplicatedEntities")
    @ResponseWrapper(localName = "getUnreplicatedEntitiesResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/", className = "ish.oncourse.webservices.v25.stubs.replication.GetUnreplicatedEntitiesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<ish.oncourse.webservices.v25.stubs.replication.UnreplicatedEntitiesStub> getUnreplicatedEntities();
}
