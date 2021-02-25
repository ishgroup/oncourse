package ish.oncourse.model;

import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.auto._Course;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static ish.oncourse.model.auto._EntityRelation.*;
import static java.lang.Boolean.TRUE;
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

public class Course extends _Course implements Queueable {
	
	private static final long serialVersionUID = 254942637990278217L;
	private static final Logger logger = LogManager.getLogger();

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public List<CourseClass> getAvailableClasses() {
		return ObjectSelect.query(CourseClass.class)
				.where(CourseClass.COURSE.eq(this))
				.and(CourseClass.IS_ACTIVE.isTrue())
				.and(CourseClass.IS_WEB_VISIBLE.isTrue())
				.and(CourseClass.CANCELLED.isFalse())
				.and(CourseClass.IS_DISTANT_LEARNING_COURSE.isTrue().orExp(CourseClass.END_DATE.gt(new Date())))
				.orderBy(CourseClass.START_DATE.asc())
				.select(getObjectContext());
	}
	
	// TODO: (ari) what is this entire method doing here. It looks like it is just an anti-optimisation method
	public List<Module> getModules() {
		final List<Module> result = new ArrayList<>();
		for (final CourseModule courseModule : getCourseModules()) {
			Module module = null;
			try {
				module = courseModule.getModule();
			} catch (Exception e) {
				logger.warn("Exception occurs when try to load course module with course id {} for college {}", getId(),
						getCollege().getId(), e);
				module = null;
			}
			if (module != null && module.getPersistenceState() == PersistenceState.HOLLOW) {
				
				final SelectQuery moduleQuery = new SelectQuery(Module.class);
				moduleQuery.andQualifier(ExpressionFactory.matchDbExp(Module.ID_PK_COLUMN, module.getId()));
				@SuppressWarnings("unchecked")
				List<Module> queryResult = getObjectContext().performQuery(moduleQuery);
				module = queryResult.isEmpty() ? null : queryResult.get(0);
				if (queryResult.size() > 1) {
					logger.warn("{} objects found for module with id {} to course {} relationship but should be 1",
							queryResult.size(), module.getId(), getId());
				}
			}
			if (module != null) {
				result.add(module);
			}
		}
		return result;
	}

	// TODO: (ari) what is this entire method doing here. It looks like it is just an anti-optimisation method
	@Override
	public Qualification getQualification() {
		Qualification qualification = null;
		try {
			qualification = super.getQualification();
		} catch (Exception e) {
			logger.warn("Exception occurrs when try to load course qualification with course id {} for college {}", getId(),
					getCollege().getId(), e);
			qualification = null;
		}
		if (qualification != null && qualification.getPersistenceState() == PersistenceState.HOLLOW) {
			final SelectQuery qualificationQuery = new SelectQuery(Qualification.class);
			qualificationQuery.andQualifier(ExpressionFactory.matchDbExp(Qualification.ID_PK_COLUMN, qualification.getId()));
			@SuppressWarnings("unchecked")
			List<Qualification> result = getObjectContext().performQuery(qualificationQuery);
			qualification = result.isEmpty() ? null : result.get(0);
			if (result.size() > 1) {
				logger.warn("{} objects found for qualification with id {} to course {} relationship but should be 1",
						result.size(), qualification.getId(), getId());
			}
		}
		return qualification;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	@Property(value = FieldProperty.CUSTOM_FIELD_COURSE, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, CourseCustomField.class);
	}

	List<EntityRelation> getToCourses() {
		return ObjectSelect.query(EntityRelation.class)
				.where(EntityRelation.FROM_ENTITY_WILLOW_ID.eq(getId()))
				.select(objectContext);
	}

	List<EntityRelation> getFromCourses() {
		return ObjectSelect.query(EntityRelation.class)
				.where(EntityRelation.TO_ENTITY_WILLOW_ID.eq(getId()))
				.select(objectContext);
	}

	public Set<Tutor> getTutors() {
		return getCourseClasses().stream()
				.filter(clazz -> !clazz.isCancelled()
						&& clazz.getIsWebVisible()
				)
				.map(CourseClass::getTutorRoles)
				.flatMap(Collection::stream)
				.map(TutorRole::getTutor)
				.filter(tutor -> tutor.getFinishDate() == null || tutor.getFinishDate().after(new Date()))
				.collect(Collectors.toSet());
	}
	
	public List<Course> getRelatedCourses() {

		List<EntityRelation> relations = new LinkedList<>(
				ObjectSelect.query(EntityRelation.class)
						.or(FROM_ENTITY_WILLOW_ID.eq(getId()),
								TO_ENTITY_WILLOW_ID.eq(getId()))
						.and(FROM_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
						.and(TO_ENTITY_IDENTIFIER.eq(Course.class.getSimpleName()))
						.and(EntityRelation.COLLEGE.eq(getCollege()))
						.and(RELATION_TYPE.isNull().orExp(RELATION_TYPE.dot(EntityRelationType.IS_SHOWN_ON_WEB).eq(TRUE)))
						.cacheStrategy(LOCAL_CACHE, EntityRelation.class.getSimpleName())
						.select(getObjectContext()));

		return relations.stream()
				.map((r) -> Arrays.asList(SelectById.query(Course.class, r.getFromEntityWillowId()).selectOne(getObjectContext()),
						SelectById.query(Course.class, r.getToEntityWillowId()).selectOne(getObjectContext())))
				.flatMap(Collection::stream)
				.filter(c -> !c.getId().equals(getId()) && c.getIsWebVisible())
				.sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
				.collect(Collectors.toList());
	}
}
