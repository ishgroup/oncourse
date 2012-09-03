package ish.oncourse.portal.components;

import ish.oncourse.model.WaitingList;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import static ish.oncourse.portal.services.PortalUtils.getCourseDetailsURLBy;

public class WaitingListInfo {

    @Property
    @Parameter(required = true)
    private WaitingList waitingList;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ITextileConverter textileConverter;

    @Inject
    private IPlainTextExtractor plainTextExtractor;


    public String getCourseName() {
        return waitingList.getCourse().getName();
    }

    public String getCourseDetailsURL() {

        return getCourseDetailsURLBy(waitingList.getCourse(), webSiteService);
    }


    public String getCourseDetail() {
        return PortalUtils.getCourseDetailsBy(waitingList.getCourse(), textileConverter, plainTextExtractor);
    }

}
