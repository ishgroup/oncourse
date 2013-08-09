package ish.oncourse.services.courseclass;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.List;

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
	 * The method allows to load classes by id from any context not only from shared.
	 * It is usefull if we what to get clear object which is loading from db directly.
	 */
	List<CourseClass> loadByIds(ObjectContext objectContext,Object... ids);

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

    public List<CourseClass> getContactCourseClasses(Contact contact, CourseClassFilter period);

    Attendance loadAttendanceById(Object ids);

	/**
	 * Returns all surveys  for this tutor
	 */
	public List<Survey> getSurveysFor(Tutor tutor);

	/**
	 * Returns all surveys  for this courseClass
	 */
	public List<Survey> getSurveysFor(CourseClass courseClass);
}
