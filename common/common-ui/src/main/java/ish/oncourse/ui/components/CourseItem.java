package ish.oncourse.ui.components;

import ish.oncourse.model.*;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.ui.pages.Courses;
import ish.oncourse.ui.utils.SearchCoursesModel;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.CourseClassUtils;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.tapestry5.EventConstants;
//import org.apache.tapestry5.annotations.InjectComponent;
//import org.apache.tapestry5.annotations.OnEvent;

public class CourseItem {

	private static final int COURSE_DETAILS_LENGTH = 490;
	private static final Logger LOGGER = Logger.getLogger(CourseItem.class);

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
	//@Parameter
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
	
	@Property
	private String postcodeParameter;
	
	@Property
	private String kmParameter;
	
	@Property
	private Double[] locationPoints;
	
	@Property
	private SearchCoursesModel searchCoursesModel;
		
	public SearchCoursesModel takeSearchCoursesModel() {
		return searchCoursesModel;
	}

	public boolean isPostcodeSpecified() {
		if (postcodeParameter == null) {
			searchCoursesModel = (SearchCoursesModel) request.getAttribute(Courses.ATTR_searchCoursesModel);
			if (takeSearchCoursesModel() != null) {
				postcodeParameter = takeSearchCoursesModel().getPostcode();
                this.kmParameter = (String) (takeSearchCoursesModel().getSearchParams().containsKey(SearchParam.km) ?
                    takeSearchCoursesModel().getSearchParams().get(SearchParam.km) : String.valueOf(SearchService.MAX_DISTANCE));
				locationPoints = takeSearchCoursesModel().getLocationPoints();
			}
		}
		return StringUtils.trimToNull(postcodeParameter) != null;
	}
	
	private boolean isLocationPointsFounded() {
		return locationPoints != null && locationPoints.length == 2 && locationPoints[0] != null && locationPoints[1] != null;
	}
	
	private boolean isKmParametherSpecified() {
		return StringUtils.trimToNull(kmParameter) != null;
	}
		
	public String getZoneId() {
		return "modulesZone" + getCurrentCourseId();
	}
	
	public Long getCurrentCourseId() {
		return course.getId();
	}
	
	//@OnEvent(value = EventConstants.ACTION, component = "expandModules")
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
		
	//@OnEvent(value = EventConstants.ACTION, component = "collapsModules")
	public final Object collapsModules(final Long currentCourseId) {
		return changeModules(currentCourseId, false);
	}
	
	public List<Module> getCourseModules() {
		return course.getModules();
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
	
	public boolean isCourseContainQualification() {
		return course.getQualification() != null && !course.getModules().isEmpty();
	}
	
	public boolean isCourseContainsOnlyModules() {
		return course.getQualification() == null && !course.getModules().isEmpty();
	}
	
	public boolean isContainsQualificationOrModules() {
		return course.getQualification() != null || !course.getModules().isEmpty();
	}

	public String getCourseDetail() {
		String detail = textileConverter.convertCustomTextile(course.getDetail(), new ValidationErrors());
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
		final boolean filterByPostcode = isPostcodeSpecified();
		final List<CourseClass> classes = (isList) ? getEnrollableClasses(filterByPostcode, true) :  getCurrentClasses();
		Ordering ordering = new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(classes);
		return classes;
	}
	
	public List<CourseClass> getOtherClasses() {
		final boolean filterByPostcode = isPostcodeSpecified();
		@SuppressWarnings("unchecked")
		final List<CourseClass> classes = filterByPostcode ? getEnrollableClasses(true, false) : Collections.EMPTY_LIST;
		Ordering ordering = new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(classes);
		//TODO: this logger should be info, used only to detect what's wrong on live server.
		String postcode = takeSearchCoursesModel() != null ? postcodeParameter : "no postcode defined";
		String km = takeSearchCoursesModel() != null ? kmParameter : "no km defined";
		String message = String.format("%s of %s classes suppressed for course %s with search params postcode : %s and km : %s for college %s", 
			classes.size(), getEnrollableClasses(false, false).size(), course.getName(), postcode, km, course.getCollege().getId());
		LOGGER.error(message);
		return classes;
	}
	
	private List<CourseClass> getEnrollableClasses(final boolean filterByPostcode, final boolean includeMatching) {
		final List<CourseClass> data = course.getEnrollableClasses();
		return filterByPostcode ? filterByPostcode(data, includeMatching) : data;
	}
	
	private List<CourseClass> filterByPostcode(final List<CourseClass> data, final boolean includeMatching) {
		final List<CourseClass> result = new ArrayList<CourseClass>();
		for (final CourseClass courseClass : data) {
			if (includeMatching && isCourseClassMatchByPostcode(courseClass, postcodeParameter)) {
				result.add(courseClass);
			}
			if (!includeMatching && !isCourseClassMatchByPostcode(courseClass, postcodeParameter)) {
				result.add(courseClass);
			}
		}
		return result;
	}
	
	private boolean isCourseClassMatchByPostcode(final CourseClass courseClass, final String postcode) {
		Room room = courseClass.getRoom();
		if (room != null) {
			Site site = room.getSite();
			if (site != null && site.getIsWebVisible() && StringUtils.trimToNull(site.getSuburb()) != null && site.isHasCoordinates() 
				&& StringUtils.trimToNull(site.getPostcode()) != null) {
				boolean isEqualPostcode = site.getPostcode().equals(postcode);
				if (isEqualPostcode) {
					return true;
				}
				if (isKmParametherSpecified() && isLocationPointsFounded()) {
					double distance = CourseClassUtils.evaluateDistanceForCourseClassSiteAndLocation(courseClass, locationPoints[0], locationPoints[1]);
					if (distance <= Double.valueOf(kmParameter)) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	private List<CourseClass> getCurrentClasses() {
		List<CourseClass> data = course.getCurrentClasses();
		return data;
	}
	
	public List<CourseClass> getFullClasses() {
		List<CourseClass> data = course.getFullClasses();
		return data;
	}
	
	public boolean getAddThisEnabled() {
		return preferenceController.getEnableSocialMediaLinks() && preferenceController.getEnableSocialMediaLinksCourse();
	}
	
	public String getAddThisProfileId() {
		return preferenceController.getAddThisProfileId();
	}
}
