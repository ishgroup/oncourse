package ish.oncourse.model;

import ish.oncourse.model.auto._Tutor;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.List;

public class Tutor extends _Tutor implements Queueable {
	private static final long serialVersionUID = 6926881335601111383L;
	private static final Logger logger = LogManager.getLogger();
			
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public String getFullName() {
		
		if(getContact()==null){
			logger.error("The tutor with id:{} doesn't have the contact!", getId());
			return "";
		}
		
		return getContact().getFullName();
	}
    
	/**
	 * Returns the roles of this tutor which is visible for website and not cancelled
	 * @return list of roles
	 */
	public List<TutorRole> getCurrentVisibleTutorRoles() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return ObjectSelect.query(TutorRole.class).
				where(TutorRole.COURSE_CLASS.dot( CourseClass.CANCELLED).isFalse()).
				and(TutorRole.COURSE_CLASS.dot( CourseClass.IS_WEB_VISIBLE).isTrue()).
				and(TutorRole.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.IS_WEB_VISIBLE).isTrue()).
				and(TutorRole.TUTOR.eq(this)).
				and(TutorRole.IN_PUBLICITY.isTrue()).
				and(TutorRole.COURSE_CLASS.dot(CourseClass.END_DATE).gte(calendar.getTime())).
				select(getObjectContext());
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
