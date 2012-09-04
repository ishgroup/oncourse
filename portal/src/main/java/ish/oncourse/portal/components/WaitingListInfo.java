package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.WaitingList;
import ish.oncourse.portal.pages.WaitingLists;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

import static ish.oncourse.portal.services.PortalUtils.getCourseDetailsURLBy;

public class WaitingListInfo {

    @Parameter(required = true)
    @Property
    private WaitingList waitingList;

    @Property
    private List<CourseClass> classes;

    @Property
    private CourseClass courseClass;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ITextileConverter textileConverter;

    @Inject
    private IPlainTextExtractor plainTextExtractor;

    @InjectPage
    private WaitingLists waitingListsPage;


    public String getCourseName() {
        return waitingList.getCourse().getName();
    }

    public String getCourseDetailsURL() {

        return getCourseDetailsURLBy(waitingList.getCourse(), webSiteService);
    }


    public String getCourseDetail() {
        return PortalUtils.getCourseDetailsBy(waitingList.getCourse(), textileConverter, plainTextExtractor);
    }

    public String getClassSessionsInfo() {
        return PortalUtils.getClassSessionsInfoBy(courseClass);
    }

    public String getClassIntervalInfo() {
        return PortalUtils.getClassIntervalInfoBy(courseClass);
    }

    public String getClassPlace() {
        return PortalUtils.getClassPlaceBy(courseClass);
    }


    @SetupRender
    void setupRender() {
        classes = waitingList.getCourse().getEnrollableClasses();
    }

    public Object onActionFromDelete(int id){
        waitingListsPage.deleteWaitingListBy(id);
        return waitingListsPage;
    }
}