package ish.oncourse.portal.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Resources {

	private static final String KEY_selfPacedMessage = "selfPacedMessage";
	private static final String KEY_classNotHaveSessionsMessage = "classNotHaveSessionsMessage";

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IPortalService portalService;

	@Inject
	private ICourseClassService courseClassService;

    @Property
    private CourseClass courseClass;

    @Property
    private List<CourseClass> courseClasses;

    @Inject
    private IBinaryDataService binaryDataService;

	@Inject
	private Messages messages;

	@Property
	private List<BinaryInfo> tutorsMaterials;

	@Property
	private BinaryInfo tutorsMaterial;

	@Inject
	private Request request;

	@Inject
	private ICookiesService cookieService;

	private Date lastLoginDate;


    @SetupRender
    void  setupRender(){

		String sd = cookieService.getCookieValue("lastLoginTime");
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
		try{
			lastLoginDate= format.parse(sd);
		}catch (Exception ex){

			new IllegalArgumentException(ex);

		}


		if(authenticationService.isTutor()){
			tutorsMaterials = portalService.getCommonTutorsBinaryInfo();
		}

        courseClasses = portalService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.CURRENT);
    }


    public String getDate(CourseClass courseClass)
    {
		if(courseClass.getIsDistantLearningCourse())
			return  messages.get(KEY_selfPacedMessage);

		if(courseClass.getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        TimeZone timeZone = courseClassService.getClientTimeZone(courseClass);

        return String.format("%s - %s",
                FormatUtils.getDateFormat("dd MMMMMM yyyy", timeZone).format(courseClass.getStartDate()),
                FormatUtils.getDateFormat("dd MMMMMM yyyy", timeZone).format(courseClass.getEndDate()));
    }


    public boolean hasResources(CourseClass courseClass){

        List<BinaryInfo> materials = portalService.getAttachedFiles(courseClass,authenticationService.getUser());
        return !materials.isEmpty();
    }

	public String getContextPath() {
		return request.getContextPath();
	}

	public boolean isNew(Date material){
		return  material.after(lastLoginDate);
	}

}
