package ish.oncourse.portal.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@SupportsInformalParameters
public class PageStructure {

	@Property
	@Parameter
	private String title;

	@Parameter
	@Property
	private String bodyClass;

	@Property
	@Parameter
	private String activeMenu;

	@Inject
	private ComponentResources resources;
}
