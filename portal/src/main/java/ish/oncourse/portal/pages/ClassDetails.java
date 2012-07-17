package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class ClassDetails {
	
	private static final int CLASS_DETAILS_LENGTH = 490;
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
			StringBuffer textileDetails = new StringBuffer();
			if(courseClass.getDetail() != null && courseClass.getDetail().length() > 0) {
				textileDetails.append(courseClass.getDetail());
			}
			
			if (courseClass.getCourse().getDetail() != null &&  courseClass.getCourse().getDetail().length() > 0) {
				if(textileDetails.toString().length() < 0) {
					textileDetails.append("\n");
				}
				textileDetails.append(courseClass.getCourse().getDetail());
			}
			
			details = textileConverter.convertCustomTextile(textileDetails.toString(), new ValidationErrors());
			details = extractor.extractFromHtml(details);
			details = StringUtils.abbreviate(details, CLASS_DETAILS_LENGTH);
		}
		return null;
	}

	public String getClassDetailsLink() {
		return courseClass != null ? String.format(URL_TEMPLATE,webSiteService.getCurrentDomain().getName(),
				courseClass.getCourse().getCode(),courseClass.getCode()) : "";
	}
	
	public boolean isHidden() {

		return courseClass == null || !courseClass.getIsWebVisible() || webSiteService.getCurrentDomain() == null;
	}
}
