package ish.oncourse.portal.components;


import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.Notification;
import ish.oncourse.portal.services.PCourseClass;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class Navigation {

    private static final Logger logger = Logger.getLogger(Navigation.class);

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

    @Inject
    private org.apache.tapestry5.ioc.Messages messages;

    @Property
    private CourseClass pastCourseClass;

    @Property
    private List<CourseClass> pastCourseClasses;

    @Property
    private CourseClass nearestCourseClass;

    @Property
    private List<PCourseClass> pCourseClasses;

    @Property
    private List<BinaryInfo> resources;

    @Property
    private PCourseClass pCourseClass;

    @Property
    private int approvals = 0;

    @Property
    private Contact contact;

    @Property
    private CourseClass unconfirmedClass;

    @Property
    private Notification notification;

    @SetupRender
    public void setupRender() {

        pCourseClasses = portalService.fillCourseClassSessions(CourseClassFilter.CURRENT);

        pastCourseClasses = portalService.getContactCourseClasses(CourseClassFilter.PAST);

        nearestCourseClass = !pCourseClasses.isEmpty() ? pCourseClasses.get(0).getCourseClass() : null;

        if (nearestCourseClass == null)
            nearestCourseClass = !pastCourseClasses.isEmpty() ? pastCourseClasses.get(0) : null;

        contact = authenticationService.getUser();
        if (contact.getTutor() != null) {
            List<CourseClass> unconfirmedClasses = courseClassService.getContactCourseClasses(contact, CourseClassFilter.UNCONFIRMED);
            approvals = unconfirmedClasses.size();
            if (approvals > 0) {
                unconfirmedClass = unconfirmedClasses.get(0);
            }
        }

        notification = portalService.getNotification();

        resources = portalService.getResources();

        updateNotification();
    }

    private void updateNotification() {
        if (activeMenu != null) {
            try {
                NavId navId = NavId.valueOf(activeMenu);
                switch (navId) {
                    case timetable:
                        break;
                    case resources:
                        notification.setNewResourcesCount(0);
                        break;
                    case results:
                        notification.setNewResultsCount(0);
                        break;
                    case profile:
                        break;
                    case subscriptions:
                        break;
                    case history:
                        notification.setNewHistoryCount(0);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public boolean hasApprovals() {
        return contact.getTutor() != null && approvals > 0;
    }

    public boolean isHistoryEnabled() {
        return portalService.isHistoryEnabled();
    }


    public String getActiveClassBy(String menutItem) {
        return (menutItem.equals(activeMenu)) ? messages.get("class.active") : StringUtils.EMPTY;
    }

    public static enum NavId
    {
        timetable,
        resources,
        results,
        profile,
        subscriptions,
        history,
    }
}
