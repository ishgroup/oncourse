package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.Map;

public class TutorsDetails extends ISHCommon {
    
    @Inject
    private IBinaryDataService binaryDataService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private IRichtextConverter textileConverter;
    
    @Parameter
    private Course course;

    @Parameter
    private CourseClass courseClass;

    @Parameter
    private Long id;

    @Parameter
    private String tagName;
    
    @Parameter
    private Integer count;

    @Property
    private List<Tutor> tutors;

    @Property
    private Tutor tutor;

    @SetupRender
    void beginRender() {
        if (id != null) {
            tutors = ObjectSelect.query(Tutor.class)
                    .where(Tutor.ANGEL_ID.eq(id))
                    .and(Tutor.COLLEGE.eq(webSiteService.getCurrentCollege()))
                    .select(cayenneService.newContext());
        } else if (course != null) {
//            tutors = course.getCourseClasses().stream().map(CourseClass::getTutorRoles)
        } else if (courseClass != null) {
//            tutors = course.getCourseClasses().stream().map(CourseClass::getTutorRoles)
        } else if (tagName != null) {
//            tutors = course.getCourseClasses().stream().map(CourseClass::getTutorRoles)
        }

    }
    
    public String getProfilePictureUrl() {
       return binaryDataService.getProfilePictureUrl(tutor.getContact());
    }

    public String getResume() {
        String resume = StringUtils.trimToNull(tutor.getResume());
        if (resume != null) {
            return textileConverter.convertCustomText(resume, new ValidationErrors());
        } else {
            return String.format("%s. Tutor of %s", tutor.getFullName(), webSiteService.getCurrentCollege().getName());
        }
    }
}
