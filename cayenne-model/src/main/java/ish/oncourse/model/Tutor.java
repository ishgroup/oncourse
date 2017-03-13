package ish.oncourse.model;

import ish.oncourse.model.auto._Tutor;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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

		Expression validRolesQualifier = ExpressionFactory
				.matchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.CANCELLED_PROPERTY, false)
				.andExp(ExpressionFactory.matchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.CANCELLED_PROPERTY, false))
				.andExp(ExpressionFactory.noMatchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.END_DATE_PROPERTY, null));

		Expression qualifier = validRolesQualifier.andExp(ExpressionFactory
				.matchExp(TutorRole.TUTOR_PROPERTY, this)
				.andExp(ExpressionFactory.matchExp(TutorRole.IN_PUBLICITY_PROPERTY, true))
				.andExp(ExpressionFactory.greaterOrEqualExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.END_DATE_PROPERTY,
						calendar.getTime()))
				.andExp(ExpressionFactory.matchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.IS_WEB_VISIBLE_PROPERTY, true)));

		SelectQuery query = new SelectQuery(TutorRole.class, qualifier);

		@SuppressWarnings("unchecked")
		List<TutorRole> result = getObjectContext().performQuery(query);

		return result == null ? new ArrayList<TutorRole>() : result;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
