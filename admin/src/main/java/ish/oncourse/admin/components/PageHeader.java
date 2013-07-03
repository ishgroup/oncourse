package ish.oncourse.admin.components;

import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageHeader {

	@Inject
	private IEnvironmentService environmentService;

	@Property
	@Parameter
	private String title;
	
	@Inject
	private Request request;
	
	public String getContextPath() {

		return request.getContextPath();
	}

	public String getMetaGeneratorContent()
	{
		return HTMLUtils.getMetaGeneratorContent(environmentService);
	}
}
