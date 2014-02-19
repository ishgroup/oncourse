package ish.oncourse.portal.components.subscriptions;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.WaitingList;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;


public class WaitingListItem {

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
	private IPortalService portalService;

    @Inject
    private IPlainTextExtractor plainTextExtractor;

    @InjectContainer
    private WaitingLists waitingListsPage;

	@Property
	private String target;

	@Property
	private String URL;

    public String getCourseName() {
        return waitingList.getCourse().getName();
    }

 	@BeginRender
	public void befoRender() {
		String[] params = portalService.getUrlBy(waitingList.getCourse());
		URL = params[0];
		target = params[1];
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

    @OnEvent(value="delete")
    public Object delete(int id){
        waitingListsPage.deleteWaitingListBy(id);
        return waitingListsPage;
    }

	public boolean isWebVisible() {
		return waitingList != null && waitingList.getCourse().getIsWebVisible();
	}

}