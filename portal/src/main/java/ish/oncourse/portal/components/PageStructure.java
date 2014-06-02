package ish.oncourse.portal.components;

import ish.oncourse.portal.pages.Login;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@SupportsInformalParameters
public class PageStructure {

    @Inject
    private Request request;

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

    @InjectPage
    private Login loginPage;


    public Object onException(Throwable cause) throws Throwable{

        if (request.getSession(false) == null)
            return loginPage;
        else
            throw cause;
    }
}
