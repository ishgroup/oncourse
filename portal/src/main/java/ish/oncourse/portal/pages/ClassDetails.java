package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

@Deprecated
public class ClassDetails {
	
    private static final String URL_TEMPLATE = "http://%s/class/%s-%s";

	@Property
	private CourseClass courseClass;
	
	@Property
	private String details;

	@Inject
	private ICourseClassService courseClassService;
	
	@Inject
	private ITextileConverter textileConverter;
	
	@Inject
	private IPlainTextExtractor extractor;
	
	@Inject
	private IWebSiteService webSiteService;
	
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
			details = PortalUtils.getClassDetailsBy(courseClass,textileConverter,extractor);
		}
		return null;
	}

	public String getClassDetailsLink() {
		return PortalUtils.getClassDetailsURLBy(courseClass, webSiteService);
	}
	
	public boolean isHidden() {

		return courseClass == null || !courseClass.getIsWebVisible() || webSiteService.getCurrentDomain() == null;
	}
}
