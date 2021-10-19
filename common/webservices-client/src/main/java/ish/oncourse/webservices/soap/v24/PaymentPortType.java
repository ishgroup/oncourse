package ish.oncourse.webservices.soap.v24;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.2.6
 * 2021-10-19T20:57:17.535+11:00
 * Generated source version: 3.2.6
 *
 */
@WebService(targetNamespace = "http://repl.v24.soap.webservices.oncourse.ish/", name = "PaymentPortType")
@XmlSeeAlso({ish.oncourse.webservices.v24.stubs.replication.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface PaymentPortType {

    @WebMethod(action = "getVouchers")
    @WebResult(name = "voucherResponse", targetNamespace = "http://repl.v24.soap.webservices.oncourse.ish/", partName = "transactionResponse")
    public ish.oncourse.webservices.v24.stubs.replication.TransactionGroup getVouchers(
        @WebParam(partName = "transactionRequest", name = "voucherRequest", targetNamespace = "http://repl.v24.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v24.stubs.replication.TransactionGroup transactionRequest
    ) throws ReplicationFault;

    @WebMethod(action = "verifyCheckout")
    @WebResult(name = "checkoutResponse", targetNamespace = "http://repl.v24.soap.webservices.oncourse.ish/", partName = "verifyCheckoutResponse")
    public ish.oncourse.webservices.v24.stubs.replication.ParametersMap verifyCheckout(
        @WebParam(partName = "verificationRequest", name = "checkoutRequest", targetNamespace = "http://repl.v24.soap.webservices.oncourse.ish/")
        ish.oncourse.webservices.v24.stubs.replication.ParametersMap verificationRequest
    ) throws ReplicationFault;
}
