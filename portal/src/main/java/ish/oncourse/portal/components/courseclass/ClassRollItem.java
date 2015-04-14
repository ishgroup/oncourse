package ish.oncourse.portal.components.courseclass;


import ish.common.types.AttendanceType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Student;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 3:30 PM
 */
public class ClassRollItem {

    @Property
    private Attendance attendance;

    @Parameter
    @Property
    private Student student;

    @Inject
    private PreferenceController preferenceController;

	@Inject
	private IPortalService portalService;

    @SetupRender
    boolean setupRender() {
        if (student == null) {
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
			return portalService.getProfilePicturePath(student.getContact());
		}
		return null;
    }

    public String getPhoneNumber(){

        if(student.getContact().getMobilePhoneNumber()!=null)
            return student.getContact().getMobilePhoneNumber();
        else if(student.getContact().getHomePhoneNumber()!=null)
            return student.getContact().getHomePhoneNumber();
        else if(student.getContact().getBusinessPhoneNumber()!=null)
            return student.getContact().getBusinessPhoneNumber();
        else if(student.getContact().getFaxNumber()!=null)
            return student.getContact().getFaxNumber();
        else
            return null;
    }

    public boolean isAttended() {
        return AttendanceType.ATTENDED.getDatabaseValue().equals(attendance.getAttendanceType());
    }

    public boolean isAbsent() {
        return (AttendanceType.DID_NOT_ATTEND_WITH_REASON.getDatabaseValue().equals(attendance.getAttendanceType())
                || AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON.getDatabaseValue().equals(attendance.getAttendanceType()));
    }
}
