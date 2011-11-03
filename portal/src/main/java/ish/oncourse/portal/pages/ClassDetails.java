package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;

import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ClassDetails {

	@Property
	private CourseClass courseClass;
	
	@Property
	private String details;

	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private ITextileConverter textileConverter;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			List<CourseClass> list = courseClassService.loadByIds(Long.parseLong((String) id));
			this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		} else {
			return pageNotFound;
		}
		
		if (courseClass != null) {
			String textileDetails = courseClass.getDetail();
			
			if (textileDetails == null || "".equals(textileDetails)) {
				textileDetails = courseClass.getCourse().getDetail();
			}
			
			details = textileConverter.convertCustomTextile(textileDetails, new ValidationErrors());
		}
		return null;
	}
}
