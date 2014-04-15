package ish.oncourse.portal.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Resources {

    private static final Logger logger = Logger.getLogger(Resources.class);

    private static final String KEY_selfPacedMessage = "selfPacedMessage";
    private static final String KEY_classNotHaveSessionsMessage = "classNotHaveSessionsMessage";

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IPortalService portalService;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IWebSiteService webSiteService;

    @Property
    private CourseClass courseClass;

    @Property
    private List<CourseClass> courseClasses;

    @Inject
    private IBinaryDataService binaryDataService;

    @Inject
    private Messages messages;

    @Property
    private List<BinaryInfo> tutorsMaterials;

    @Property
    private BinaryInfo tutorsMaterial;

    @Inject
    private Request request;

    @Inject
    private ICookiesService cookieService;

    private Date lastLoginDate;

    @Property
    private String target;


    @SetupRender
    void setupRender() {

        lastLoginDate = portalService.getLastLoginTime();
        if (authenticationService.isTutor()) {
            tutorsMaterials = portalService.getTutorCommonResources();
        }

        courseClasses = portalService.getContactCourseClasses(CourseClassFilter.CURRENT);
    }


    public String getDate(CourseClass courseClass) {
        if (courseClass.getIsDistantLearningCourse())
            return messages.get(KEY_selfPacedMessage);

        if (courseClass.getSessions().isEmpty())
            return messages.get(KEY_classNotHaveSessionsMessage);

        TimeZone timeZone = courseClassService.getClientTimeZone(courseClass);

        return String.format("%s - %s",
                FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy, timeZone).format(courseClass.getStartDate()),
                FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy, timeZone).format(courseClass.getEndDate()));
    }


    public boolean hasResources(CourseClass courseClass) {

        List<BinaryInfo> materials = portalService.getResourcesBy(courseClass);
        return !materials.isEmpty();
    }

    public boolean hasAnyResources()
    {
        return !portalService.getResources().isEmpty();
    }


    public String getContextPath() {
        return request.getContextPath();
    }

    public boolean isNew(Date material) {
        return lastLoginDate == null || material.after(lastLoginDate);
    }

    public String getTutorsMaterialUrl() {
        return binaryDataService.getUrl(tutorsMaterial);
    }

    public String getUrl() {
        return portalService.getUrlBy(courseClass);
    }

    public String getSize() {
        return FileUtils.byteCountToDisplaySize(tutorsMaterial.getByteSize());
    }
}
