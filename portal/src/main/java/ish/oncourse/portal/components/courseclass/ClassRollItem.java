package ish.oncourse.portal.components.courseclass;


import ish.common.types.AttendanceType;
import ish.oncourse.model.*;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

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

    private BinaryInfo avatar;

    @Inject
    private PreferenceController preferenceController;

    @Inject
    private IBinaryDataService binaryDataService;


    @SetupRender
    boolean setupRender() {
        if (student == null) {
            return false;
        }
        avatar = binaryDataService.getProfilePicture(student.getContact());





        return true;
    }





    public boolean isEnableDetails(){
        return !preferenceController.getHideStudentDetailsFromTutor();

    }

    public String getAvatarContextPath()
    {
        if (avatar != null && isEnableDetails())
        {
           return avatar.getContextPath();
        }
        return "/s/portal/img/ico-student-default.png";
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
