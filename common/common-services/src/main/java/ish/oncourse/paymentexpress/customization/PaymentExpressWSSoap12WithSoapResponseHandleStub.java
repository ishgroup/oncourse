package ish.oncourse.paymentexpress.customization;

import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.NoEndPointException;
import org.apache.axis.client.Call;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;

import com.paymentexpress.stubs.EnrolmentCheckRequest;
import com.paymentexpress.stubs.EnrolmentCheckResult;
import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;

/**
 * Customization of PaymentExpressWSSoap12Stub used to receive soap response and be able to save it.
 * Required for #13074.
 * @author vdavidovich
 *
 */
public class PaymentExpressWSSoap12WithSoapResponseHandleStub extends PaymentExpressWSSoap12Stub {
	/**
	 * This field moved here because no ability to change the modifier to protected from default for them.
	 */
	static OperationDesc [] supportedOperations;

    static {
    	supportedOperations = new OperationDesc[6];
        initOperationDesc1();
    }
    
    /**
     * This method is a full clone of PaymentExpressWSSoap12Stub#_initOperationDesc1 
     * and should be regenerated in case of change the amount or ordering of supported operations
     */
    private static void initOperationDesc1(){
        OperationDesc oper;
        ParameterDesc param;
        oper = new OperationDesc();
        oper.setName("SubmitTransaction");
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postUsername"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postPassword"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "transactionDetails"), ParameterDesc.IN, 
        	new QName("http://PaymentExpress.com", "TransactionDetails"), TransactionDetails.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://PaymentExpress.com", "TransactionResult2"));
        oper.setReturnClass(TransactionResult2.class);
        oper.setReturnQName(new QName("http://PaymentExpress.com", "SubmitTransactionResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        supportedOperations[0] = oper;

        oper = new OperationDesc();
        oper.setName("SubmitTransaction2");
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postUsername"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postPassword"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "transactionDetails"), ParameterDesc.IN, 
        	new QName("http://PaymentExpress.com", "TransactionDetails"), TransactionDetails.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://PaymentExpress.com", "TransactionResult2"));
        oper.setReturnClass(TransactionResult2.class);
        oper.setReturnQName(new QName("http://PaymentExpress.com", "SubmitTransaction2Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        supportedOperations[1] = oper;

        oper = new OperationDesc();
        oper.setName("Check3dsEnrollment");
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postUsername"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postPassword"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "transactionDetails"), ParameterDesc.IN, 
        	new QName("http://PaymentExpress.com", "EnrolmentCheckRequest"), EnrolmentCheckRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://PaymentExpress.com", "EnrolmentCheckResult"));
        oper.setReturnClass(EnrolmentCheckResult.class);
        oper.setReturnQName(new QName("http://PaymentExpress.com", "Check3dsEnrollmentResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        supportedOperations[2] = oper;

        oper = new OperationDesc();
        oper.setName("GetStatus");
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postUsername"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postPassword"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "txnRef"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://PaymentExpress.com", "TransactionResult2"));
        oper.setReturnClass(TransactionResult2.class);
        oper.setReturnQName(new QName("http://PaymentExpress.com", "GetStatusResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        supportedOperations[3] = oper;

        oper = new OperationDesc();
        oper.setName("GetStatus2");
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postUsername"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postPassword"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "txnRef"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://PaymentExpress.com", "TransactionResult2"));
        oper.setReturnClass(TransactionResult2.class);
        oper.setReturnQName(new QName("http://PaymentExpress.com", "GetStatus2Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        supportedOperations[4] = oper;

        oper = new OperationDesc();
        oper.setName("UpdateCard");
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postUsername"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "postPassword"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new ParameterDesc(new QName("http://PaymentExpress.com", "cardDetails"), ParameterDesc.IN, 
        	new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(String.class);
        oper.setReturnQName(new QName("http://PaymentExpress.com", "UpdateCardResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        supportedOperations[5] = oper;
    }

	/**
	 * {@inheritDoc}
	 */
	public PaymentExpressWSSoap12WithSoapResponseHandleStub(URL endpointURL, Service service) throws AxisFault {
		super(endpointURL, service);
	}

	/**
	 * {@inheritDoc}
	 */
	public PaymentExpressWSSoap12WithSoapResponseHandleStub(Service service) throws AxisFault {
		super(service);
	}

	/**
	 * {@inheritDoc}
	 */
	public PaymentExpressWSSoap12WithSoapResponseHandleStub() throws AxisFault {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransactionResult2 submitTransaction(final String postUsername, final String postPassword, final TransactionDetails transactionDetails)
			throws RemoteException {
		if (super.cachedEndpoint == null) {
            throw new NoEndPointException();
        }
        Call _call = createCall();
        _call.setOperation(supportedOperations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://PaymentExpress.com/SubmitTransaction");
        _call.setEncodingStyle(null);
        _call.setProperty(Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new QName("http://PaymentExpress.com", "SubmitTransaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
        	Object _resp = _call.invoke(new Object[] {postUsername, postPassword, transactionDetails});
        	if (_resp instanceof RemoteException) {
        		throw new RemoteException(_call.getResponseMessage().getSOAPPartAsString(), (RemoteException)_resp);
        	}
        	else {
        		extractAttachments(_call);
        		try {
        			final TransactionResult2 response = (TransactionResult2) _resp;
        			response.setMerchantHelpText(_call.getResponseMessage().getSOAPPartAsString());
        			return response;
        		} catch (Exception _exception) {
        			final TransactionResult2 response = (TransactionResult2) JavaUtils.convert(_resp, TransactionResult2.class);
        			response.setMerchantHelpText(_call.getResponseMessage().getSOAPPartAsString());
        			return response;
        		}
        	}
        } catch (AxisFault axisFaultException) {
        	throw new AxisFault(_call.getResponseMessage().getSOAPPartAsString(), axisFaultException);
        }
	}
	
}
