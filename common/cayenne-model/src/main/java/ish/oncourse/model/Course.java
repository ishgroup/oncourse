package ish.oncourse.model;

import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.auto._Course;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.commitlog.CommitLog;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Course extends _Course implements Queueable {
	
	private static final long serialVersionUID = 254942637990278217L;
	private static final Logger logger = LogManager.getLogger();

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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
}
