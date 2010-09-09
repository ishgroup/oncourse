package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseItem {

	@Inject
	private Messages messages;

	@Property
	@Parameter(required = true)
	private Course course;

	@Property
	private CourseClass courseClass;

	@Inject
	private ComponentResources componentResources;

	public String getAvailMsg() {
		int numberOfClasses = course.getEnrollableClasses().size();
		String msg;

		if (numberOfClasses <= 0) {
			msg = messages.get("noClasses");
		} else if (numberOfClasses == 1) {
			msg = messages.get("oneClass");
		} else {
			msg = messages.format("someClasses", numberOfClasses);
		}

		return msg;
	}

	public String getMoreLink() {
		return "/course/" + course.getCode();
	}

	public String getCourseDetail() {
		if ("Courses".equals(componentResources.getPageName())) {
			String result = course.getDetail().substring(0, 410);
			int closingExpanded = result.lastIndexOf("</");
			int closingCollapsed = result.lastIndexOf("/>");
			int lastOpenClosing=Math.max(closingExpanded, closingCollapsed);
			int lastOpening=result.lastIndexOf("<");
			int lastClosing=result.lastIndexOf(">");
			if(lastOpening>lastOpenClosing){
				result=result.substring(0,lastOpening);
			}else if(lastClosing<closingExpanded){
				result=result.substring(0,closingExpanded);
				lastOpening=result.indexOf("<");
				result=result.substring(0,lastOpening);
			}
			return result + "...";
		} else {
			return course.getDetail();
		}
	}
}
