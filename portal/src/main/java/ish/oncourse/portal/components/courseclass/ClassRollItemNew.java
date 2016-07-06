package ish.oncourse.portal.components.courseclass;


import ish.common.types.AttendanceType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.utils.DateUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.util.Date;
import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 3:30 PM
 */
public class ClassRollItemNew {

	@Parameter
	@Property
	private Enrolment enrolment;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private IPortalService portalService;

	@SetupRender
	boolean setupRender() {
		if (enrolment == null) {
			return false;
		}
		return true;
	}

	public boolean isEnableDetails(){
		return !preferenceController.getHideStudentDetailsFromTutor();
	}

	public String getAvatarContextPath()
	{
		if (isEnableDetails()){
			return portalService.getProfilePictureUrl(enrolment.getStudent().getContact());
		}
		return null;
	}

	public String getPhoneNumber(){

		if(enrolment.getStudent().getContact().getMobilePhoneNumber()!=null)
			return enrolment.getStudent().getContact().getMobilePhoneNumber();
		else if(enrolment.getStudent().getContact().getHomePhoneNumber()!=null)
			return enrolment.getStudent().getContact().getHomePhoneNumber();
		else if(enrolment.getStudent().getContact().getBusinessPhoneNumber()!=null)
			return enrolment.getStudent().getContact().getBusinessPhoneNumber();
		else if(enrolment.getStudent().getContact().getFaxNumber()!=null)
			return enrolment.getStudent().getContact().getFaxNumber();
		else
			return null;
	}

	public Integer getAttendancePercent() {
		return AttendanceUtils.getAttendancePercent(enrolment);
	}	
}
