package ish.oncourse.paymentexpress.customization;

import com.paymentexpress.stubs.PaymentExpressWSLocator;
import com.paymentexpress.stubs.PaymentExpressWSSoap_PortType;
import org.apache.axis.AxisFault;

import javax.xml.rpc.ServiceException;
import java.net.URL;

/**
 * Customization for PaymentExpressWSLocator used to work with PaymentExpressWSSoap12WithSoapResponseHandleStub instead of PaymentExpressWSSoap12Stub.
 * @author vdavidovich
 */
public class PaymentExpressWSLocatorWithSoapResponseHandle extends PaymentExpressWSLocator {
	private static final long serialVersionUID = -5462752157615867729L;

	public PaymentExpressWSLocatorWithSoapResponseHandle() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentExpressWSSoap12WithSoapResponseHandleStub getPaymentExpressWSSoap12() throws ServiceException {
		return (PaymentExpressWSSoap12WithSoapResponseHandleStub) super.getPaymentExpressWSSoap12();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentExpressWSSoap_PortType getPaymentExpressWSSoap12(URL portAddress) throws ServiceException {
		try {
			PaymentExpressWSSoap12WithSoapResponseHandleStub _stub = new PaymentExpressWSSoap12WithSoapResponseHandleStub(portAddress, this);
            _stub.setPortName(getPaymentExpressWSSoap12WSDDServiceName());
            return _stub;
        }
        catch (AxisFault e) {
            return null;
        }
	}

}
