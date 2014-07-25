package ish.oncourse.util;

import org.apache.log4j.Logger;
import org.apache.tapestry5.services.*;

import java.io.IOException;

public class UIRequestExceptionHandler implements RequestExceptionHandler {
	private static final String TAPESTRY_2563_POST_VALIDATION_MESSAGE = "Forms require that the request method be POST and that the t:formdata query parameter have values.";
	public static final String DEFAULT_ERROR_PAGE = "ErrorPage";
	public static final String ERROR_500_PAGE = "ui/Error500";
	public static final String APPLICATION_ROOT_PAGE = "/";
    private static final Logger LOGGER = Logger.getLogger(UIRequestExceptionHandler.class);
    private final ResponseRenderer renderer;
    private final Response response;
    private final ComponentSource componentSource;
    private Request request;
    private String errorPageName;
    private String redirectPage;
    private boolean redirectOnInvalidPostRequest;

    public UIRequestExceptionHandler(ComponentSource componentSource, ResponseRenderer renderer, Request request, Response response, String errorPageName,
    	String redirectPage, boolean redirectOnInvalidPostRequest) {
        this.renderer = renderer;
        this.response = response;
        this.componentSource = componentSource;
        this.request = request;
        this.errorPageName = errorPageName;
        this.redirectPage = redirectPage;
        this.redirectOnInvalidPostRequest = redirectOnInvalidPostRequest;
    }

    public void handleRequestException(Throwable exception) throws IOException {
    	if (TAPESTRY_2563_POST_VALIDATION_MESSAGE.equals(exception.getMessage())) {
            //this is Tapestry 5 validation to prevent hack attempts https://issues.apache.org/jira/browse/TAPESTRY-2563
            //we may ignore it
            LOGGER.warn("Unexpected runtime exception: " + exception.getMessage(), exception);
            if (response != null && redirectOnInvalidPostRequest) {
                response.sendRedirect(redirectPage);
            }
        } else {
    		LOGGER.error(String.format("Unexpected runtime exception. Request: %s",
                        toString(request)) , exception);
    	}
    	final String errorPage = getErrorPageName(exception);
    	ExceptionReporter exceptionReporter = (ExceptionReporter) componentSource.getPage(errorPage);
        exceptionReporter.reportException(exception);
        renderer.renderPageMarkupResponse(errorPage);
    }
    
    public String getErrorPageName(Throwable exception) {
    	return errorPageName;
    }

    public String toString(Request request) {
        return String.format("Url: %s%s%s; Method: %s; Type: %s",
                request != null ? request.getServerName() : "undefined host",
                request != null ? request.getContextPath() : "undefined host",
                request != null ? request.getPath() : "undefined path",
                request != null ? request.getMethod() : "undefined method",
                (request != null && request.isXHR()) ? "XmlHttpRequest" : "HttpRequest");
    }
}
