package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;
@Deprecated
public class ShortListControlPage {

	@Inject
	private Request request;
	@Inject
	private ICourseClassService courseClassService;
	
	@Property
	private CourseClass courseClass;
	
	@SetupRender
	void beforeRender(){
		String id = request.getParameter(CourseClass.COURSE_CLASS_ID_PARAMETER);
		if(id!=null){
			List<CourseClass> results = courseClassService.loadByIds(id);
			if(!results.isEmpty()){
				courseClass = results.get(0);
			}
		}
	}
}
