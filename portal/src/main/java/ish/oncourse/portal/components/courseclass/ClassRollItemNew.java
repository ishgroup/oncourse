package ish.oncourse.portal.components.courseclass;


import ish.oncourse.model.*;
import ish.oncourse.portal.services.GetContactPhone;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.portal.util.GetAge;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.stream.Collectors;

import static ish.oncourse.services.preference.Preferences.DEFAULT_contactAgeWhenNeedParent;

public class ClassRollItemNew {

    private static final String NAME_AGE = "%s (age %d)";

	@Parameter
	@Property
	private Enrolment enrolment;

	@Property
	private Contact contact;

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

	public String getPhoneNumber(Contact c){
        return GetContactPhone.valueOf(c).get();
	}

	public Integer getAttendancePercent() {
		return AttendanceUtils.getAttendancePercent(enrolment);
	}

	public String getFullName(Student s) {
		Integer age = GetAge.valueOf(s.getContact()).get();
		if (age != null && age < DEFAULT_contactAgeWhenNeedParent) {
			return String.format(NAME_AGE, s.getContact().getFullName(), age);
		} else {
			return s.getContact().getFullName();
		}
	}

	public List<Contact> getGuardians(Student s) {
		return s.getContact().getFromContacts().stream()
				.filter(relation -> relation.getRelationType() != null && relation.getRelationType().getAngelId() == -1)
                .map(relation -> relation.getFromContact())
				.collect(Collectors.toList());
	}
}
