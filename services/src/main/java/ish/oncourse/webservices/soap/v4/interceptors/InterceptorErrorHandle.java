package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.webservices.exception.AuthSoapFault;
import ish.oncourse.webservices.exception.StackTraceUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.common.util.SOAPConstants;
import org.apache.cxf.message.Message;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class InterceptorErrorHandle {

	static final String DEFAULT_MESSAGE = "Willow generic exception.";
	static final String SOAP_MESSAGE_TEMPLATE = "%s\n(SoapAction: %s; basePath: %s; requestUrl: %s)";
	static final String ERROR_MESSAGE_TEMPLATE = "%s\n%s";


	private SoapMessage soapMessage;
	private Logger logger;
	private Level level;

	public InterceptorErrorHandle(SoapMessage soapMessage, Logger logger)
	{
		this(soapMessage,logger,Level.ERROR);
	}

	public InterceptorErrorHandle(SoapMessage soapMessage, Logger logger, Level level) {
		this.soapMessage = soapMessage;
		this.logger = logger;
		this.level = level == null ? Level.ERROR : level;
	}

	public AuthSoapFault handle(Throwable throwable, String message) {
		String soapAction = (String) this.soapMessage.get(SOAPConstants.SOAP_ACTION);
		String basePath = (String) this.soapMessage.get(Message.BASE_PATH);
		String requestUrl = (String) this.soapMessage.get(Message.REQUEST_URL);

		String errorMessage = String.format(SOAP_MESSAGE_TEMPLATE, message, soapAction, basePath, requestUrl);

		logger.log(level, errorMessage, throwable);
		if (throwable != null) {
			errorMessage = String.format(ERROR_MESSAGE_TEMPLATE, errorMessage, StackTraceUtils.stackTraceAsString(throwable));
		}
		return new AuthSoapFault(errorMessage);
	}

	public AuthSoapFault handle(Throwable throwable) {
		return handle(throwable, DEFAULT_MESSAGE);
	}

	public AuthSoapFault handle(String message) {
		return handle(null, message);
	}

}
