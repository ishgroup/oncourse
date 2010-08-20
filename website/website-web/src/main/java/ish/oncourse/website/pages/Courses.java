package ish.oncourse.website.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.search.ISearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Courses {

	@Inject
	private ICourseService courseService;

	@Inject
	private ISearchService searchService;
	
	@Inject
	private Request request;

	@Property
	private List<Course> courses;

	@Property
	private Course course;

	@SetupRender
	public void beforeRender() {
		this.courses = (request.getParameterNames().size() == 0) ? courseService
				.getCourses() : searchCourses();
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
		QueryResponse resp = searchService.searchCourses(buildSearchParams());
		
		List<String> ids = new ArrayList<String>(resp.getResults().size());
		
		for (SolrDocument doc : resp.getResults()) {
			ids.add((String) doc.getFieldValue("id"));
		}

		return courseService.loadByIds(ids);
	}

	private Map<String, String> buildSearchParams() {
		Map<String, String> m = new HashMap<String, String>();
		for (String s : request.getParameterNames()) {
			m.put(s, request.getParameter(s));
		}
		return m;
	}
}
