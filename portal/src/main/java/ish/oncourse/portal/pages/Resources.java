package ish.oncourse.portal.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;

import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.TimeZone;

public class Resources {

	private static final String KEY_selfPacedMessage = "selfPacedMessage";
	private static final String KEY_classNotHaveSessionsMessage = "classNotHaveSessionsMessage";

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IPortalService portalService;

    @Property
    private CourseClass courseClass;

    @Property
    private List<CourseClass> courseClasses;

    @Inject
    private IBinaryDataService binaryDataService;

	@Inject
	private Messages messages;

    @SetupRender
    void  setupRender(){

        courseClasses = portalService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.CURRENT);
    }


    public String getDate(CourseClass courseClass)
    {
		if(courseClass.getIsDistantLearningCourse())
			return  messages.get(KEY_selfPacedMessage);

		if(courseClass.getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        TimeZone timeZone = FormatUtils.getClientTimeZone(courseClass);

        return String.format("%s - %s",
                FormatUtils.getDateFormat("dd MMMMMM yyyy", timeZone).format(courseClass.getStartDate()),
                FormatUtils.getDateFormat("dd MMMMMM yyyy", timeZone).format(courseClass.getEndDate()));
    }


        public boolean hasResources(CourseClass courseClass){

        List<BinaryInfo> materials = binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false);
        return !materials.isEmpty();
    }


}
