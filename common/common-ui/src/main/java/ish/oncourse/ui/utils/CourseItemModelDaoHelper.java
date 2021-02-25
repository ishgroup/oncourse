package ish.oncourse.ui.utils;

import ish.oncourse.model.*;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.preference.GetPreference;
import ish.oncourse.services.preference.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectById;

import java.util.*;
import java.util.stream.Collectors;

import static ish.oncourse.model.auto._EntityRelation.*;
import static java.lang.Boolean.TRUE;
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

public class CourseItemModelDaoHelper {

	public static CheckClassAge hideClassOnWebAge(College college) {
		String age = new GetPreference(college, Preferences.HIDE_CLASS_ON_WEB_AGE, college.getObjectContext()).getValue();
		String type = new GetPreference(college, Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE, college.getObjectContext()).getValue();
		return new CheckClassAge().classAge(ClassAge.valueOf(age, type));
	}

	public static CheckClassAge stopWebEnrolmentsAge(College college) {
		String age = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE, college.getObjectContext()).getValue();
		String type = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE, college.getObjectContext()).getValue();
		return new CheckClassAge().classAge(ClassAge.valueOf(age, type));
	}
	
	public static List<Product> selectRelatedProducts(Course course) {
		ObjectContext context = course.getObjectContext();

		List<EntityRelation> fromRelations = ObjectSelect.query(EntityRelation.class)
				.where(FROM_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
				.and(FROM_ENTITY_WILLOW_ID.eq(course.getId()))
				.and(TO_ENTITY_IDENTIFIER.eq(Product.class.getSimpleName()))
				.and(EntityRelation.COLLEGE.eq(course.getCollege()))
				.and(RELATION_TYPE.isNull().orExp(RELATION_TYPE.dot(EntityRelationType.IS_SHOWN_ON_WEB).eq(TRUE)))
				.cacheStrategy(LOCAL_CACHE, EntityRelation.class.getSimpleName())
				.select(context);

		List<Product> products = fromRelations
				.stream()
				.map((r) -> Collections.singletonList(SelectById.query(Product.class, r.getToEntityWillowId()).selectOne(context)))
				.flatMap(Collection::stream)
				.filter(product -> TRUE.equals(product.getIsWebVisible()))
				.collect(Collectors.toList());

		List<EntityRelation> toRelations = ObjectSelect.query(EntityRelation.class)
				.where(TO_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
				.and(TO_ENTITY_WILLOW_ID.eq(course.getId()))
				.and(FROM_ENTITY_IDENTIFIER.eq(Product.class.getSimpleName()))
				.and(EntityRelation.COLLEGE.eq(course.getCollege()))
				.and(RELATION_TYPE.isNull().orExp(RELATION_TYPE.dot(EntityRelationType.IS_SHOWN_ON_WEB).eq(TRUE)))
				.cacheStrategy(LOCAL_CACHE, EntityRelation.class.getSimpleName())
				.select(context);

		products.addAll(toRelations
				.stream()
				.map((r) -> Collections.singletonList(SelectById.query(Product.class, r.getFromEntityWillowId()).selectOne(context)))
				.flatMap(Collection::stream)
				.filter(product -> TRUE.equals(product.getIsWebVisible()))
				.collect(Collectors.toList())
		);
		return products;
	}

	static List<CourseClass> getWebVisibleClasses(Course course) {
		return ObjectSelect.query(CourseClass.class)
							.where(CourseClass.COURSE.eq(course))
							.and(CourseClass.IS_WEB_VISIBLE.isTrue())
							.and(CourseClass.CANCELLED.isFalse())
							.orderBy(CourseClass.START_DATE.asc())
							.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, CourseClass.class.getSimpleName())
							.select(course.getObjectContext());
	}
}
