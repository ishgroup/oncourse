package ish.oncourse.services.courseclass;

import java.util.Date;
import java.util.List;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;

/**
 * 
 * @author anton
 *
 */
public interface ICourseClassService {
	/**
	 * Finds course class by full code, which is combination of <course code> - <course class code>  
	 * @param code full code
	 * @return course class
	 */
	CourseClass getCourseClassByFullCode(String code);

	/**
	 * 
	 * @param angelId
	 * @return
	 */
	CourseClass loadByAngelId(Long angelId);

	/**
	 * 
	 * @param ids
	 * @return
	 */
	List<CourseClass> loadByIds(Object... ids);

	/**
	 * 
	 * @param ids
	 * @return
	 */
	List<CourseClass> loadByIds(List<Long> ids);

	/**
	 * Find contact session within month. Used for timetables.
	 * @param contact willow contact
	 * @param month month
	 * @return session list
	 */
	List<Session> getContactSessionsForMonth(Contact contact, Date month);
	
	/**
	 * Find contact session. Used for timetable list
	 * @param contact willow contact
	 * @return session list
	 */
	List<Session> getContactSessions(Contact contact);
	
	/**
	 * Find contact courseClasses.
	 * @param contact willow contact
	 * @return courseClass list
	 */
	public List<CourseClass> getContactCourseClasses(Contact contact);
	
	Attendance loadAttendanceById(Object ids);
}
