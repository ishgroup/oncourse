package ish.oncourse.services.tutor;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TutorService implements ITutorService{
	@Inject
	private ICayenneService cayenneService;
	
	public Tutor getTutorById(Long tutorId) {
		SelectQuery query = new SelectQuery(Tutor.class, ExpressionFactory.matchExp(Tutor.ID_PK_COLUMN, tutorId));
		List<Tutor> result = cayenneService.sharedContext().performQuery(query);
		return result!=null&&!result.isEmpty()?result.get(0):null;
	}
	
	public List<TutorRole> getCurrentVisibleTutorRoles(Tutor tutor){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Expression validRolesQualifier=ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY+"."+Tutor.DELETED_PROPERTY, false)
		.andExp(ExpressionFactory.matchExp(TutorRole.COURSE_CLASS_PROPERTY+"."+CourseClass.CANCELLED_PROPERTY,false))
		.andExp(ExpressionFactory.matchExp(TutorRole.COURSE_CLASS_PROPERTY+"."+CourseClass.DELETED_PROPERTY,false))
		.andExp(ExpressionFactory.noMatchExp(TutorRole.COURSE_CLASS_PROPERTY+"."+CourseClass.END_DATE_PROPERTY,null));
		
		
		Expression qualifier = validRolesQualifier.andExp(ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY, tutor)
		.andExp(ExpressionFactory.matchExp(TutorRole.IS_CONFIRMED_PROPERTY, true))
		.andExp(ExpressionFactory.greaterOrEqualExp(TutorRole.COURSE_CLASS_PROPERTY+"."+CourseClass.END_DATE_PROPERTY, calendar.getTime()))
		.andExp(ExpressionFactory.matchExp(TutorRole.COURSE_CLASS_PROPERTY+"."+CourseClass.WEB_VISIBLE_PROPERTY, true)));
		SelectQuery query = new SelectQuery(TutorRole.class, qualifier);
		
		List<TutorRole> result = cayenneService.sharedContext().performQuery(query);
		return result==null?new ArrayList<TutorRole>():result;
	}
}
