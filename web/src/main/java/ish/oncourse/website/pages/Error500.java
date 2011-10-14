package ish.oncourse.website.pages;

import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ExceptionReporter;

public class Error500 implements ExceptionReporter {

	private static final Logger logger = Logger.getLogger(Error500.class);

	@Property
	private Throwable exception;

	@Property
	private String stackTrace;

	@Property
	private String title;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private IEnvironmentService environmentService;


	public void reportException(Throwable exception) {
		logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);
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
