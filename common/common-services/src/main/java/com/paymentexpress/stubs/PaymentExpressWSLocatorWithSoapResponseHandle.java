package com.paymentexpress.stubs;

import java.net.URL;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisFault;
/**
 * Customization for PaymentExpressWSLocator used to work with PaymentExpressWSSoap12WithSoapResponseHandleStub instead of PaymentExpressWSSoap12Stub.
 * @author vdavidovich
 *
 */
public class PaymentExpressWSLocatorWithSoapResponseHandle extends PaymentExpressWSLocator {
	private static final long serialVersionUID = -7039031459801206921L;

	/**
	 * {@inheritDoc}
	 */
	public PaymentExpressWSLocatorWithSoapResponseHandle() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentExpressWSSoap getPaymentExpressWSSoap12(URL portAddress) throws ServiceException {
		try {
			PaymentExpressWSSoap12WithSoapResponseHandleStub _stub = new PaymentExpressWSSoap12WithSoapResponseHandleStub(portAddress, this);
            _stub.setPortName(getPaymentExpressWSSoap12WSDDServiceName());
            return _stub;
        }
        catch (AxisFault e) {
            return null;
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentExpressWSSoap12WithSoapResponseHandleStub getPaymentExpressWSSoap12() throws ServiceException {
		return (PaymentExpressWSSoap12WithSoapResponseHandleStub) super.getPaymentExpressWSSoap12();
	}
	
	
}
