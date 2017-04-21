package ish.oncourse.webservices.soap.v15;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.6.16
 * 2017-04-19T17:38:33.130+03:00
 * Generated source version: 2.6.16
 * 
 */
@WebService(targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/", name = "PaymentPortType")
@XmlSeeAlso({ish.oncourse.webservices.v15.stubs.replication.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface PaymentPortType {

    @WebMethod(action = "getPaymentStatus")
    @WebResult(name = "paymentStatusResponse", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/", partName = "transactionGroup")
    public ish.oncourse.webservices.v15.stubs.replication.TransactionGroup getPaymentStatus(
        @WebParam(partName = "sessionId", name = "paymentStatusRequest", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/")
        java.lang.String sessionId
    ) throws ReplicationFault;

    @WebMethod(action = "processRefund")
    @WebResult(name = "refundResponse", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/", partName = "transactionGroup")
    public ish.oncourse.webservices.v15.stubs.replication.TransactionGroup processRefund(
        @WebParam(partName = "paymentOut", name = "refundRequest", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v15.stubs.replication.TransactionGroup paymentOut
    ) throws ReplicationFault;

    @WebMethod(action = "getVouchers")
    @WebResult(name = "voucherResponse", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/", partName = "transactionResponse")
    public ish.oncourse.webservices.v15.stubs.replication.TransactionGroup getVouchers(
        @WebParam(partName = "transactionRequest", name = "voucherRequest", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v15.stubs.replication.TransactionGroup transactionRequest
    ) throws ReplicationFault;

    @WebMethod(action = "verifyUSI")
    @WebResult(name = "usiResponse", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/", partName = "verificationResponse")
    public ish.oncourse.webservices.v15.stubs.replication.ParametersMap verifyUSI(
        @WebParam(partName = "verificationRequest", name = "usiRequest", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v15.stubs.replication.ParametersMap verificationRequest
    ) throws ReplicationFault;

    @WebMethod(action = "processPayment")
    @WebResult(name = "paymentResponse", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/", partName = "transactionGroup")
    public ish.oncourse.webservices.v15.stubs.replication.TransactionGroup processPayment(
        @WebParam(partName = "transaction", name = "paymentRequest", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v15.stubs.replication.TransactionGroup transaction,
        @WebParam(partName = "paymentModel", name = "paymentModel", targetNamespace = "http://repl.v15.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v15.stubs.replication.ParametersMap paymentModel
    ) throws ReplicationFault;
}
