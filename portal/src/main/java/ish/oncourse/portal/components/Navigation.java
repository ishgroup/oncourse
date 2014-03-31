package ish.oncourse.portal.components;


import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PCourseClass;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class Navigation {


    @Parameter
    private String activeMenu;

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    @Property
    private Request request;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IPortalService portalService;

    @Property
    private CourseClass pastCourseClass;

    @Property
    private List<CourseClass> pastCourseClasses;

    @Property
    private CourseClass nearestCourseClass;

    @Property
    private List<PCourseClass> pCourseClasses;

    @Property
    private PCourseClass pCourseClass;

    private static final String DATE_FORMAT = "d MMMM h:mma ('UTC'Z)";


    @SetupRender
    public void setupRender() {

        pCourseClasses = portalService.fillCourseClassSessions(authenticationService.getUser(), CourseClassFilter.CURRENT);

        pastCourseClasses = portalService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.PAST);

        nearestCourseClass = !pCourseClasses.isEmpty() ? pCourseClasses.get(0).getCourseClass() : null;

        if (nearestCourseClass == null)
            nearestCourseClass = !pastCourseClasses.isEmpty() ? pastCourseClasses.get(0) : null;
    }


    public boolean isHasResources() {

        for (PCourseClass pCourseClasse : pCourseClasses) {
            if (!portalService.getAttachedFiles(pCourseClasse.getCourseClass(), authenticationService.getUser()).isEmpty()) {
                return true;
            }
        }

        return authenticationService.isTutor() && !portalService.getCommonTutorsBinaryInfo().isEmpty();
    }


    public boolean isHasResults() {

        if (authenticationService.getUser().getStudent() != null) {
            authenticationService.getUser().getStudent().getEnrolments();
            return !authenticationService.getUser().getStudent().getEnrolments().isEmpty();
        }
        return false;
    }

    public boolean isHistoryEnabled() {
        return portalService.isHistoryEnabled();
    }


    public String getActiveClassBy(String menutItem) {
        if (menutItem.equals(activeMenu))
            return "active";
        return StringUtils.EMPTY;

    }
}
