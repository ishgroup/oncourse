package ish.oncourse.webservices.pages;

import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.util.GetMetaGeneratorContent;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Status {
	@Inject
	private IEnvironmentService environmentService;
	
	public String getMetaGeneratorContent() {
		return new GetMetaGeneratorContent(environmentService).get();
	}
}
