package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Module;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.ui.utils.CourseItemModel;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseItem {

	private static final int COURSE_DETAILS_LENGTH = 490;
	private static final Logger LOGGER = Logger.getLogger(CourseItem.class);

	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private boolean isList;

	@SuppressWarnings("all")
	@Parameter
	@Property
	private boolean linkToLocationsMap;
	
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


    @Property
    @Parameter(required = true)
    private CourseItemModel courseItemModel;


    @SetupRender
    public void beforeRender() {

    }



	public String getZoneId() {
		return "modulesZone" + getCurrentCourseId();
	}
	
	public Long getCurrentCourseId() {
		return courseItemModel.getCourse().getId();
	}
	
	public String getAvailMsg() {
		int numberOfClasses = courseItemModel.getCourse().getEnrollableClasses().size();
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
		return "/course/" + courseItemModel.getCourse().getCode();
	}
	
	public boolean isCourseContainQualification() {
		return courseItemModel.getCourse().getQualification() != null && !courseItemModel.getCourse().getModules().isEmpty();
	}
	
	public boolean isCourseContainsOnlyModules() {
		return courseItemModel.getCourse().getQualification() == null && !courseItemModel.getCourse().getModules().isEmpty();
	}
	
	public boolean isContainsQualificationOrModules() {
		return courseItemModel.getCourse().getQualification() != null || !courseItemModel.getCourse().getModules().isEmpty();
	}

	public String getCourseDetail() {
		String detail = textileConverter.convertCustomTextile(courseItemModel.getCourse().getDetail(), new ValidationErrors());
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

    public boolean isHasMoreAvailablePlaces() {
        int places = 0;
        for (CourseClass courseClass : courseItemModel.getCourse().getEnrollableClasses()) {
            places += courseClass.getAvailableEnrolmentPlaces();
        }
        return places > 0;
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
		Stack<String> opentagsStack = new Stack<>();
		Stack<String> closedtagsStack = new Stack<>();

		while (openedTags.find()) {
			String tag = openedTags.group();

			String openedtag = tag.substring(1, tag.length() - 1);
			// strip any attributes
			if (openedtag.contains(" ")) {
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

	public boolean getAddThisEnabled() {
		return preferenceController.getEnableSocialMediaLinks() && preferenceController.getEnableSocialMediaLinksCourse();
	}
	
	public String getAddThisProfileId() {
		return preferenceController.getAddThisProfileId();
	}
}
