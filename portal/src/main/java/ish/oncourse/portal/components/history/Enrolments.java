package ish.oncourse.portal.components.history;

import ish.oncourse.model.Enrolment;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
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

    @Inject
    private Messages messages;

    @SetupRender
    void setupRender(){
        enrolments = portalService.getEnrolments();

    }

    public String getDate()
    {
        TimeZone timeZone = courseClassService.getClientTimeZone(enrolment.getCourseClass());
        /**
         * we added created == null condition to exlude NPE when some old enrolment has null value in create field.
         * If the field has null value we show "(not set)" string
         * TODO The condition should be deleted after 21309 will be closed
         */
        Date date = enrolment.getCreated();
        if (date != null)
        {
            return String.format("%s", FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_d_MMMMM_h_mma, timeZone).format(date));
        }
        else
        {
            return messages.get(PortalUtils.MESSAGE_KEY_notSet);
        }

    }


    public boolean isNew()
    {
        return portalService.isNew(enrolment);
    }


}
