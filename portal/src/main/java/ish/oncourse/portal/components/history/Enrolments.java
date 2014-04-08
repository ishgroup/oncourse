package ish.oncourse.portal.components.history;

import ish.oncourse.model.Enrolment;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 11/4/13
 * Time: 9:28 AM
 */
public class Enrolments {


	@Inject
	private  ICourseClassService courseClassService;

    @Inject
    private IPortalService portalService;


    @Property
    private List<Enrolment> enrolments;

    @Property
    private Enrolment enrolment;

    @SetupRender
    void setupRender(){
        enrolments = portalService.getEnrolments();

    }

    public String getDate(Enrolment enrolment)
    {
        TimeZone timeZone = courseClassService.getClientTimeZone(enrolment.getCourseClass());
        return String.format("%s", FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_d_MMMMM_h_mma, timeZone).format(enrolment.getCreated()));
    }


    public boolean isNew()
    {
        return portalService.isNew(enrolment);
    }


}
