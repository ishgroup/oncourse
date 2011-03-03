package ish.oncourse.ui.components;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.textile.TextileUtil;

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
		if (detail == null) {
			return "";
		}
		if (isList) {
			String result = truncateHTML(detail, COURSE_DETAILS_LENGTH);
			return result + "...";
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
		return isList && courseClass.hasEnded();
	}

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

}
