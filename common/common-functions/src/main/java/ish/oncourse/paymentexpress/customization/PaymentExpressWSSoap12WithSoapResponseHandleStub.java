package ish.oncourse.paymentexpress.customization;

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.NoEndPointException;
import org.apache.axis.client.Call;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Customization of PaymentExpressWSSoap12Stub used to receive soap response and be able to save it.
 * Required for #13074.
 * @author vdavidovich
 *
 */
public class PaymentExpressWSSoap12WithSoapResponseHandleStub extends PaymentExpressWSSoap12Stub {

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
        _call.setOperation(_operations[0]);
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
