package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ClassTabs {


	@Inject
	private ICayenneService cayenneService;

    @Inject
    private IPortalService portalService;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}



	public boolean hasResults(){
		CourseClass courseClass = cayenneService.sharedContext().localObject(this.courseClass);

		return portalService.hasResult(courseClass);
		}


    public boolean hasResources(){

        return  !portalService.getResourcesBy(this.courseClass).isEmpty();
    }




}
