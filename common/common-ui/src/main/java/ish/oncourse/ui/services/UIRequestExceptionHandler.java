package ish.oncourse.ui.services;

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

    public UIRequestExceptionHandler(ComponentSource componentSource,
                                     ResponseRenderer renderer,
                                     Request request,
                                     Response response) {
        this.renderer = renderer;
        this.response = response;
        this.componentSource = componentSource;
        this.request = request;
    }


    public void handleRequestException(Throwable exception) throws IOException {


        if (response != null && exception != null && exception.getMessage() != null &&
                exception.getMessage().contains("Forms require that the request method be POST and that the t:formdata query parameter have values")) {
            response.sendRedirect("/");
        } else {
            LOGGER.error(String.format("Unexpected runtime exception on \"%s\"", request != null ? request.getPath(): "undefined path") , exception);
            String exceptionPageName = "ui/Error500";
            ExceptionReporter exceptionReporter = (ExceptionReporter) componentSource.getPage(exceptionPageName);
            exceptionReporter.reportException(exception);
            renderer.renderPageMarkupResponse(exceptionPageName);
        }
    }
}
