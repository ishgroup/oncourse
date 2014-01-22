package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import java.util.List;

public class ClassTabs {


	@Inject
	private ICayenneService cayenneService;

    @Inject
    private IPortalService portalService;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	@Property
	private IAuthenticationService authService;

	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}



	public boolean isHasResults(){
		CourseClass courseClass = cayenneService.sharedContext().localObject(this.courseClass);

		return portalService.isHasResult(courseClass);
		}


    public boolean isHasResources(){

        return  !portalService.getAttachedFiles(courseClass, authService.getUser()).isEmpty();
    }




}
