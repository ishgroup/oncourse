package ish.oncourse.cms.pages;

import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.OperationException;
import org.apache.tapestry5.runtime.ComponentEventException;
import org.apache.tapestry5.services.ExceptionReporter;

public class Error500 implements ExceptionReporter {

	private static final Logger logger = Logger.getLogger(Error500.class);

	@Property
	private Throwable exception;

	@SuppressWarnings("all")
	@Property
	private String stackTrace;

	@SuppressWarnings("all")
	@Property
	private String title;

	@SuppressWarnings("all")
	@Inject
	private IWebSiteService siteService;

	@Inject
	private IEnvironmentService environmentService;


	public void reportException(Throwable exception) {
		if (exception instanceof OperationException && exception.getCause() instanceof ComponentEventException && 
			exception.getCause().getCause() instanceof RuntimeException && 
			"Forms require that the request method be POST and that the t:formdata query parameter have values.".equals(exception.getMessage())) {
			logger.warn("Unexpected runtime exception: " + exception.getMessage(), exception);
		} else {
			logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);
		}
		this.exception = exception;
	}

	public String getExceptionType() {
		return exception.getClass().getName();
	}

	public ArrayList<String> getStackTraces() {
		StackTraceElement[] st = exception.getStackTrace();
		ArrayList<String> stackTraceElementMessage = new ArrayList<String>();

		for (StackTraceElement ste : st) {
			stackTraceElementMessage.add(ste.toString());
		}

		return stackTraceElementMessage;
	}
	
	public String getMetaGeneratorContent() {
		StringBuilder buff = new StringBuilder(
				environmentService.getApplicationName());

		String buildServerID = environmentService.getBuildServerID();
		if (!StringUtils.isEmpty(buildServerID)) {
			buff.append(' ').append(buildServerID);
		}

		String scmVersion = environmentService.getScmVersion();
		if (!StringUtils.isEmpty(scmVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(scmVersion);
		}

		String ciVersion = environmentService.getCiVersion();
		if (!StringUtils.isEmpty(ciVersion)) {
			buff.append(StringUtils.isEmpty(buildServerID) ? ' ' : '/');
			buff.append('r');
			buff.append(ciVersion);
		}
		return buff.toString();
	}
}
