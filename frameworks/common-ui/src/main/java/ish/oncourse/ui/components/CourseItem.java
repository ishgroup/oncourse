package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseItem {

	private static final int COURSE_DETAILS_LENGTH = 410;

	private static final String COURSES_PAGE_NAME = "ui/Courses";

	@Inject
	private Messages messages;

	@Property
	@Parameter(required = true)
	private Course course;

	@Property
	private CourseClass courseClass;
	
	@Parameter
	@Property
	private boolean isList;
	
	@Parameter
	@Property
	private boolean linkToLocationsMap;

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
		String detail = course.getDetail();
		if(detail==null){
			return "";
		}
		if (isList) {
			String result = detail.length() > COURSE_DETAILS_LENGTH ? detail.substring(0, COURSE_DETAILS_LENGTH)
					: detail;
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
			return detail;
		}
	}
	
	public String getCourseItemClass(){
		if(isList){
			return "new_course_item small_class_detail clearfix";
		}else{
			return "new_course_item";
		}
	}
	/**
	 * Shows, whether to hide the class if it is outdated for the list view.
	 * @return
	 */
	public boolean isShouldHideClass(){
		return isList && courseClass.hasEnded();
	}
}
