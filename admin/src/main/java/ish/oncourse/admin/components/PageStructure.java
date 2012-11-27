package ish.oncourse.admin.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageStructure {
	
	@Property
	@Parameter
	private String title;
	
	@Inject
	private ComponentResources resources;
	
	public String getCurrentPage() {
		return resources.getPage().getClass().getSimpleName();
	}
}
