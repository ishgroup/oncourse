package ish.oncourse.ui.pages;

import ish.oncourse.model.*;
import ish.oncourse.model.auto._Site;
import ish.oncourse.services.course.GetCourses;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tutor.GetIsActiveTutor;
import ish.oncourse.services.tutor.GetNotFinishedTutors;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Meta("tapestry.response-content-type=text/xml")
public class SitemapXML extends ISHCommon {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICourseService courseService;

	@Inject
	@Property
	private IWebNodeService webNodeService;

	@Inject
	private ISitesService sitesService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITutorService tutorService;

	@Inject
	private ITagService tagService;

	@Property
	private String hostName;

	@Property
	private Date siteModificationDate;

	@Property
	private List<Course> courses;

	@Property
	private Course course;

	@Property
	private List<Site> sites;

	@Property
	private Site site;

	@Property
	private List<Tutor> tutors;

	@Property
	private Tutor tutor;

	@Property
	private List<WebNode> pages;

	@Property
	private WebNode page;

	@SetupRender
	public void beforeRender() {
		hostName = request.getServerName();
		siteModificationDate = setupSiteModificationDate();
		String coursesRootTagName = webSiteService.getCurrentWebSite().getCoursesRootTagName();
		if (StringUtils.trimToNull(coursesRootTagName) != null) {
			Tag tag = tagService.getTagByFullPath(coursesRootTagName);
			if (tag != null)
				courses = courseService.getCourses(tag, null, false, null);
		}

		if (courses == null)
			courses = new GetCourses(cayenneService.sharedContext(), webSiteService.getCurrentCollege())
					.get();

		sites = webSiteService.getCurrentCollege().getSites()
				.stream().filter(Site::isWebVisible).collect(Collectors.toList());
		tutors = GetNotFinishedTutors.valueOf(cayenneService.sharedContext(),
				null,
				webSiteService.getCurrentCollege()).get();
		pages = webNodeService.getSiteMapNodes();
	}

	private Date setupSiteModificationDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -20);
		Date latest = calendar.getTime();
		Date courseLatestModifiedDate = courseService.getLatestModifiedDate();
		if (courseLatestModifiedDate != null
				&& courseLatestModifiedDate.after(latest)) {
			latest = courseLatestModifiedDate;
		}

		Date webNodeLatestModifiedDate = webNodeService.getLatestModifiedDate();
		if (webNodeLatestModifiedDate != null
				&& webNodeLatestModifiedDate.after(latest)) {
			latest = webNodeLatestModifiedDate;
		}

		Date sitesLatestModifiedDate = sitesService.getLatestModifiedDate();
		if (sitesLatestModifiedDate != null
				&& sitesLatestModifiedDate.after(latest)) {
			latest = sitesLatestModifiedDate;
		}

		Date tutorsLatestModifiedDate = tutorService.getLatestModifiedDate();
		if (tutorsLatestModifiedDate != null
				&& tutorsLatestModifiedDate.after(latest)) {
			latest = tutorsLatestModifiedDate;
		}

		return latest;
	}

	public Format getDateFormat() {
		return DATE_FORMAT;
	}

	public boolean isActiveTutor() {
		return GetIsActiveTutor.valueOf(tutor).get();
	}
}
