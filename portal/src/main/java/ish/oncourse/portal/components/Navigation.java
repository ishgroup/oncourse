package ish.oncourse.portal.components;


import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Contact;
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

import java.util.ArrayList;
import java.util.Date;
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

        notification = new Notification();
        notification.setNewResultsCount(portalService.getNewResultsCount());

        resources = new ArrayList<>();

        fillResources();

        notification.setNewHistoryCount(portalService.getNewPaymentsCount() +
                portalService.getNewEnrolmentsCount() +
                portalService.getNewInvoicesCount());
    }

    private void fillResources() {
        Date lastLoginTime = portalService.getLastLoginTime();
        int newResourcesCount = 0;
        this.resources.addAll(portalService.getTutorCommonResources());
        for (PCourseClass pCourseClasse : pCourseClasses) {
            {
                this.resources.addAll(portalService.getResourcesBy(pCourseClasse.getCourseClass()));
            }
        }

        for (BinaryInfo binaryInfo : resources) {
            if (binaryInfo.getModified().after(lastLoginTime)) {
                newResourcesCount++;
            }
        }
        notification.setNewResourcesCount(newResourcesCount);
    }

    public boolean hasApprovals() {
        return contact.getTutor() != null && approvals > 0;
    }


    public boolean hasResources() {
        return !resources.isEmpty();
    }

    public boolean hasResults() {

        return portalService.hasResults();
    }

    public boolean isHistoryEnabled() {
        return portalService.isHistoryEnabled();
    }


    public String getActiveClassBy(String menutItem) {
        return (menutItem.equals(activeMenu)) ? messages.get("class.active") : StringUtils.EMPTY;
    }

    public class Notification {
        private int newResourcesCount = 0;
        private int newResultsCount = 0;
        private int newHistoryCount = 0;

        public boolean hasNewResources() {
            return newResourcesCount > 0;
        }

        public boolean hasNewResults() {
            return newResultsCount > 0;
        }

        public boolean hasNewHistory() {
            return newHistoryCount > 0;
        }

        public int getNewResourcesCount() {
            return newResourcesCount;
        }

        public void setNewResourcesCount(int newResourcesCount) {
            this.newResourcesCount = newResourcesCount;
        }

        public int getNewResultsCount() {
            return newResultsCount;
        }

        public void setNewResultsCount(int newResultsCount) {
            this.newResultsCount = newResultsCount;
        }

        public int getNewHistoryCount() {
            return newHistoryCount;
        }

        public void setNewHistoryCount(int newHistoryCount) {
            this.newHistoryCount = newHistoryCount;
        }
    }
}
