package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class Classes {

    private final static Logger LOGGER = Logger.getLogger(Classes.class);

    @Parameter
    @Property
    private CourseClass courseClass;

    private List<CourseClass> classes;

    @Parameter
    @Property
    private TutorRole tutorRole;

    @SuppressWarnings("all")
    private List<TutorRole> tutorRoles;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private Request request;

    @Parameter
    private CourseClassFilter courseClassFilter = CourseClassFilter.CURRENT;

    public String getContextPath() {
        return request.getContextPath();
    }

    void onActivate(String period) {
        try {
            courseClassFilter = CourseClassFilter.valueOf(period);
        } catch (Throwable e) {
            courseClassFilter = CourseClassFilter.CURRENT;
            LOGGER.warn(String.format("Undefined period \"%s\"", period),e);
        }
    }

    String onPassivate() {
        return courseClassFilter.name();
    }

    @SetupRender
    void beforeRender() {
        if (courseClassFilter == null) {
            courseClassFilter = CourseClassFilter.CURRENT;
        }
        Contact contact = authenticationService.getUser();
        if (contact != null) {

            this.classes = courseClassService.getContactCourseClasses(contact, courseClassFilter);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Number of found classes is %s.",
                        this.classes.size()));
            }
        }
    }

    public boolean isHasClasses() {
        return !(this.classes == null || this.classes.isEmpty());
    }

    public boolean isCurrentContactStudent() {
        return !authenticationService.isTutor();
    }

    public List<CourseClass> getClasses() {
        return classes;
    }

    public String getCourseName() {
        return courseClass.getCourse().getName();
    }

    public String getCourseClassCode() {
        return "(" + courseClass.getCourse().getCode() + "-" + courseClass.getCode() + ")";
    }

    public String getRoom() {
        return PortalUtils.getClassPlaceBy(courseClass);
    }

    public String getClassSessionsInfo() {
        return PortalUtils.getClassSessionsInfoBy(courseClass);
    }

    public String getClassIntervalInfo() {
        return PortalUtils.getClassIntervalInfoBy(courseClass);
    }

    public List<TutorRole> getTutorRoles() {
        return courseClass.getTutorRoles();
    }

    public String getTutorRoleName() {
        return tutorRole.getTutor().getContact().getFullName();
    }

    public String getClassPageName() {
        return "class";
    }

    public String getActiveMenu() {
        switch (courseClassFilter) {
        	case UNCONFIRMED:
        		return "m_UnconfirmedClasses";
            case PAST:
                return "m_PastClasses";
            default:
                return "m_CurrentClasses";
        }
    }

    public boolean getFilterCurrent() {
        return courseClassFilter == CourseClassFilter.CURRENT;
    }
    
    public boolean getFilterPast() {
    	return courseClassFilter == CourseClassFilter.PAST;
    }
    
    public boolean getFilterUnconfirmed() {
    	return courseClassFilter == CourseClassFilter.UNCONFIRMED;
    }

}
