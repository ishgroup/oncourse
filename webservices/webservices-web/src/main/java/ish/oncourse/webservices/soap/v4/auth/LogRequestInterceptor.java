package ish.oncourse.webservices.soap.v4.auth;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;

public class LogRequestInterceptor extends AbstractSoapInterceptor {

	private static final Logger LOGGER = Logger.getLogger(LogRequestInterceptor.class);

	public LogRequestInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		HttpServletRequest req = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

		String ip = req.getRemoteAddr();
		Date time = new Date();
		
		BindingOperationInfo boi = message.getExchange().get(BindingOperationInfo.class);

		LOGGER.info(String.format("Invoke %s from %s at %s.", boi.getName(),  ip, time));

	}
}
