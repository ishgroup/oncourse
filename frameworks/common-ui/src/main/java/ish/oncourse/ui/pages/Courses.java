package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Courses {

	private static final Logger LOGGER = Logger.getLogger(Courses.class);

	private static final int START_DEFAULT = 0;
	private static final int ROWS_DEFAULT = 10;

	@Inject
	private ICourseService courseService;

	@Inject
	private ISearchService searchService;

	@Inject
	private Request request;

	@Property
	@Persist
	private List<Course> courses;

	@Property
	private Course course;

	@Property
	@Persist
	private Integer coursesCount;

	@Property
	@Persist
	private Integer itemIndex;

	@SetupRender
	public void beforeRender() {
		this.courses = (request.getParameterNames().size() == 0) ? courseService
				.getCourses(START_DEFAULT, ROWS_DEFAULT)
				: searchCourses();
		this.coursesCount = courseService.getCoursesCount();
		this.itemIndex = courses.size();
	}

	@InjectComponent
	private Zone coursesZone;

	@OnEvent(component = "showMoreCourses")
	Object onActionFromShowMoreCourses() {
		courses.addAll(courseService.getCourses(itemIndex, ROWS_DEFAULT));
		itemIndex = courses.size();
		return coursesZone.getBody();
	}

	public Collection<Site> getMapSites() {
		Set<Site> sites = new HashSet<Site>();
		for (Course course : courses) {
			for (CourseClass courseClass : course.getCourseClasses()) {
				for (Session s : courseClass.getSessions()) {
					sites.add(s.getRoom().getSite());
				}
			}
		}
		return sites;
	}

	private List<Course> searchCourses() {
		String query = request.getParameter(SearchParam.s.name());

		int start = getIntParam(request.getParameter("start"), START_DEFAULT);
		int rows = getIntParam(request.getParameter("rows"), ROWS_DEFAULT);

		Map<SearchParam, String> params = new HashMap<SearchParam, String>();

		for (SearchParam name : SearchParam.values()) {
			if (request.getParameter(name.name()) != null) {
				params.put(name, request.getParameter(name.name()));
			}
		}

		QueryResponse resp = searchService.searchCourses(params, start, rows);

		LOGGER.info(String.format("The number of courses found: %s", resp
				.getResults().size()));

		List<String> ids = new ArrayList<String>(resp.getResults().size());

		for (SolrDocument doc : resp.getResults()) {
			ids.add((String) doc.getFieldValue("id"));
		}

		return courseService.loadByIds(ids.toArray());
	}

	public boolean isHasAnyItems() {
		return !courses.isEmpty();
	}

	public boolean isHasMoreItems() {
		return itemIndex < coursesCount;
	}

	private static int getIntParam(String s, int def) {
		int start = def;
		try {
			start = (s != null) ? Integer.parseInt(s) : start;
		} catch (Exception e) {
		}

		return start;
	}
}
