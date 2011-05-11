package ish.oncourse.ui.components;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.menu.IWebMenuService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;

@SupportsInformalParameters
public class BodyStructure {

	@Inject
	private IWebMenuService webMenuService;

	@Parameter
	@Property
	private WebNodeType webNodeType;

    public WebMenu getRootMenu() {
		return webMenuService.getRootMenu();
	}
}
