package ish.oncourse.portal.pages.student;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

@UserRole({"student"})
public class ClassDetails {

	@Property
	private CourseClass courseClass;
	
	@Property
	private String details;

	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private ITextileConverter textileConverter;
	
	void onActivate(long id) {
		List<CourseClass> list = courseClassService.loadByIds(id);
		this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		
		if (courseClass != null) {
			String textileDetails = courseClass.getDetail();
			
			if (textileDetails == null || "".equals(textileDetails)) {
				textileDetails = courseClass.getCourse().getDetail();
			}
			
			details = textileConverter.convertCustomTextile(textileDetails, new ValidationErrors());
		}
	}
}
