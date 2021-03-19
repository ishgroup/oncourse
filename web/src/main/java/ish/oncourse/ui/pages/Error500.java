package ish.oncourse.ui.pages;

import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Error500 extends ISHCommon implements ExceptionReporter {

	private static final Logger logger = LogManager.getLogger();

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

	@Inject
	private Response response;


	@SetupRender
	public void setupRender() {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public void reportException(Throwable exception) {
		this.exception = exception;
	}

	public String getExceptionType() {
		return exception.getClass().getName();
	}

	public ArrayList<String> getStackTraces() {
		ArrayList<String> stackTraceElementMessage = new ArrayList<>();

		List<Throwable> throwables = ExceptionUtils.getThrowableList(exception);
		for (Throwable throwable : throwables) {
			stackTraceElementMessage.add("------------------------------------");
			stackTraceElementMessage.add(throwable.getClass().getName());
			stackTraceElementMessage.add(throwable.getLocalizedMessage());
			StackTraceElement[] st = throwable.getStackTrace();
			for (StackTraceElement ste : st) {
				stackTraceElementMessage.add(ste.toString());
			}
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
