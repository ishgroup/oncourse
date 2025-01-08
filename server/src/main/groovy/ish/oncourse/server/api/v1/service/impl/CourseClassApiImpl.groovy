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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.api.service.CourseClassApiService
import ish.oncourse.server.api.v1.model.CancelCourseClassDTO
import ish.oncourse.server.api.v1.model.CourseClassDTO
import ish.oncourse.server.api.v1.model.CourseClassDuplicateDTO
import ish.oncourse.server.api.v1.model.TrainingPlanDTO
import ish.oncourse.server.api.v1.service.CourseClassApi


class CourseClassApiImpl implements CourseClassApi {

    @Inject
    private CourseClassApiService service


    @Override
    void cancelClass(CancelCourseClassDTO cancelCourseClass) {
        service.cancelClass(cancelCourseClass)
    }

    @Override
    Long create(CourseClassDTO courseClass) {
        service.create(courseClass).id
    }

    @Override
    List<Long> duplicateClass(CourseClassDuplicateDTO courseClassDuplicate) {
        service.duplicateClass(courseClassDuplicate)
    }

    @Override
    CourseClassDTO get(Long id) {
        service.get(id)
    }


    @Override
    List<TrainingPlanDTO> getTrainingPlan(Long id) {
        return service.getTrainingPlan(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, CourseClassDTO courseClass) {
        service.update(id, courseClass)
    }

    @Override
    void updateTrainingPlan( Long id, List<TrainingPlanDTO> trainingPlans) {
        service.updateTrainingPlan(id, trainingPlans)

    }
}
