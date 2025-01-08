/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.duplicate;

import ish.duplicate.ClassDuplicationRequest;
import ish.duplicate.DuplicationResult;
import ish.oncourse.entity.services.CourseClassService;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.dao.CourseClassDao;
import ish.oncourse.server.api.v1.model.ClassCostDTO;
import ish.oncourse.server.cayenne.CourseClass;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DuplicateClassService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private CourseClassService courseClassService;

	@Inject
	private CourseClassDao courseClassDao;

	private static final Logger logger = LogManager.getLogger();

	private DataContext context;

	public DuplicationResult duplicateClasses(ClassDuplicationRequest request) {
		return duplicateClasses(request, null);
	}

	public DuplicationResult duplicateClasses(ClassDuplicationRequest request, ClassCostDTO classCostDto) {
		var result = new DuplicationResult();
		context = cayenneService.getNewContext();
		List<CourseClass> newClasses = new ArrayList<>();
		for (var id : request.getIds()) {

			var query = ObjectSelect.query(CourseClass.class).where(CourseClass.ID.eq(id));

			var oldClass =  query.selectOne(context);

			CourseClass courseClass = DuplicateCourseClass.valueOf(oldClass, request, courseClassService, context, courseClassDao, classCostDto)
					.duplicate();
			newClasses.add(courseClass);
		}

		try {
			context.commitChanges();
		} catch (ValidationException ve) {
			logger.error(ve);
			context.rollbackChanges();
			var vf = ve.getValidationResult().getFailures().get(0);
			result.setFailure(vf);
			result.setFailed(true);
			return result;
		} catch (Exception e) {
			logger.error("An exception (not a validation error) was thrown when trying to duplicate a class.", e);
			context.rollbackChanges();
			result.setFailed(true);
			return result;
		}

		newClasses.forEach(courseClass -> result.addNewId(courseClass.getId()));

		return result;
	}
}
