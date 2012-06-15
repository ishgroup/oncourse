package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Module;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseItem {

	private static final int COURSE_DETAILS_LENGTH = 490;

	@Property
	@Parameter(required = true)
	private Course course;

	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private boolean isList;

	@SuppressWarnings("all")
	@Parameter
	@Property
	private boolean linkToLocationsMap;
	
	@SuppressWarnings("all")
	@Parameter
	@Property
	private boolean expandedModules;

	@Inject
	private IPlainTextExtractor extractor;

	@Inject
	private ITextileConverter textileConverter;
	
	@Inject
	private PreferenceController preferenceController;
	
	@Property
	//@InjectComponent
	private Zone modulesZone;
	
	@SuppressWarnings("all")
	@Property
	private Module module;
		
	@Inject
	private ICourseService courseService;
	
	@Inject
	private Request request;
		
	public String getZoneId() {
		return "modulesZone" + getCurrentCourseId();
	}
	
	public Long getCurrentCourseId() {
		return takeCourse().getId();
	}
	
	@OnEvent(value = EventConstants.ACTION, component = "expandModules")
	public final Object expandModules(final Long currentCourseId) {
		return changeModules(currentCourseId, true);
	}
	
	private Object changeModules(final Long id, final boolean expanded) {
		expandedModules = expanded;
		course = courseService.loadByIds(id).get(0);
		if (request.isXHR()) {
			return modulesZone.getBody();
		} else {
			//this case mean IE request
			return this;
		}
	}
		
	@OnEvent(value = EventConstants.ACTION, component = "collapsModules")
	public final Object collapsModules(final Long currentCourseId) {
		return changeModules(currentCourseId, false);
	}
	
	public List<Module> getCourseModules() {
		return takeCourse().getModules();
	}

	public String getAvailMsg() {
		int numberOfClasses = course.getEnrollableClasses().size();
		String msg;

		if (numberOfClasses <= 0) {
			msg = "There are no classes currently available for this course.";
		} else if (numberOfClasses == 1) {
			msg = "There is one class available for this course.";
		} else {
			msg = String.format("There are %1$d classes available for this course.", numberOfClasses);
		}

		return msg;
	}

	public String getMoreLink() {
		return "/course/" + course.getCode();
	}
	
	protected Course takeCourse() {
		return course;
	}
	
	public boolean isCourseContainQualification() {
		return takeCourse().getQualification() != null && !takeCourse().getModules().isEmpty();
	}
	
	public boolean isCourseContainsOnlyModules() {
		return takeCourse().getQualification() == null && !takeCourse().getModules().isEmpty();
	}
	
	public boolean isContainsQualificationOrModules() {
		return takeCourse().getQualification() != null || !takeCourse().getModules().isEmpty();
	}

	public String getCourseDetail() {
		String detail = textileConverter.convertCustomTextile(takeCourse().getDetail(), new ValidationErrors());
		if (detail == null) {
			return StringUtils.EMPTY;
		}

		if (isList) {
			String plainText = extractor.extractFromHtml(detail);
			String result = StringUtils.abbreviate(plainText, COURSE_DETAILS_LENGTH);
			return result;
		} else {
			return detail;
		}
	}

	public String getCourseItemClass() {
		if (isList) {
			return "new_course_item small_class_detail clearfix";
		} else {
			return "new_course_item";
		}
	}

	/**
	 * Shows, whether to hide the class if it is outdated for the list view.
	 * 
	 * @return
	 */
	public boolean isShouldHideClass() {
		return courseClass.hasEnded();
	}

	@SuppressWarnings("unused")
	private String truncateHTML(String text, int size) {
		boolean inTag = false;
		int cntr = 0;
		int cntrContent = 0;

		// loop through html, counting only viewable content
		for (char c : text.toCharArray()) {
			if (cntrContent == size)
				break;
			cntr++;
			if (c == '<') {
				inTag = true;
				continue;
			}

			if (c == '>') {
				inTag = false;
				continue;
			}
			if (!inTag)
				cntrContent++;
		}

		String substr = text.substring(0, cntr);

		// search for nonclosed tags
		Matcher openedTags = Pattern.compile("<[^/](.|\n)*?[^/]>").matcher(substr);
		Matcher closedTags = Pattern.compile("<[/](.|\n)*?>").matcher(substr);

		// create stack
		Stack<String> opentagsStack = new Stack<String>();
		Stack<String> closedtagsStack = new Stack<String>();

		while (openedTags.find()) {
			String tag = openedTags.group();

			String openedtag = tag.substring(1, tag.length() - 1);
			// strip any attributes
			if (openedtag.indexOf(" ") >= 0) {
				openedtag = openedtag.substring(0, openedtag.indexOf(" "));
			}
		}

		while (closedTags.find()) {
			String tag = closedTags.group();

			String closedtag = tag.substring(2, tag.length() - 1);
			closedtagsStack.push(closedtag);
		}

		if (closedtagsStack.size() < opentagsStack.size()) {
			while (opentagsStack.size() > 0) {
				String tagstr = opentagsStack.pop();

				if (closedtagsStack.size() == 0 || tagstr != closedtagsStack.peek()) {
					substr += "</" + tagstr + ">";
				} else {
					closedtagsStack.pop();
				}
			}
		}

		return substr;
	}

	public boolean isHasMoreAvailablePlaces() {
		int places = 0;
		for (CourseClass courseClass : course.getEnrollableClasses()) {
			places += courseClass.getAvailableEnrolmentPlaces();
		}
		return places > 0;
	}

	public List<CourseClass> getClasses() {
		List<CourseClass> classes = (isList) ? course.getEnrollableClasses() :  course.getCurrentClasses();
		Ordering ordering = new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(classes);
		return classes;
	}
	
	public boolean getAddThisEnabled() {
		return preferenceController.getEnableSocialMediaLinks() && preferenceController.getEnableSocialMediaLinksCourse();
	}
	
	public String getAddThisProfileId() {
		return preferenceController.getAddThisProfileId();
	}
}
