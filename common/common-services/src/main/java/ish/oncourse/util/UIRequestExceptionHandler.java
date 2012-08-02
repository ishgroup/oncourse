package ish.oncourse.util;

import org.apache.log4j.Logger;
import org.apache.tapestry5.services.*;

import java.io.IOException;

public class UIRequestExceptionHandler implements RequestExceptionHandler
{
    private static final Logger LOGGER = Logger.getLogger(UIRequestExceptionHandler.class);
    private final ResponseRenderer renderer;
    private final Response response;
    private final ComponentSource componentSource;
    private Request request;
    private String errorPageName;
    private String redirectPage;

    public UIRequestExceptionHandler(ComponentSource componentSource,
                                     ResponseRenderer renderer,
                                     Request request,
                                     Response response,
                                     String errorPageName,
                                     String redirectPage) {
        this.renderer = renderer;
        this.response = response;
        this.componentSource = componentSource;
        this.request = request;
        this.errorPageName = errorPageName;
        this.redirectPage = redirectPage;
    }

    public void handleRequestException(Throwable exception) throws IOException {


        if (response != null && exception != null && exception.getMessage() != null &&
                exception.getMessage().contains("Forms require that the request method be POST and that the t:formdata query parameter have values")) {
            response.sendRedirect(redirectPage);
        } else {
            LOGGER.error(String.format("Unexpected runtime exception on \"%s%s\". Request is \"%s\"",
                    request != null ? request.getServerName(): "undefined host",
                    request != null ? request.getPath(): "undefined path",
                    (request != null && request.isXHR())? "XmlHttpRequest":"HttpRequest") , exception);
            ExceptionReporter exceptionReporter = (ExceptionReporter) componentSource.getPage(errorPageName);
            exceptionReporter.reportException(exception);
            renderer.renderPageMarkupResponse(errorPageName);
        }
    }
}
