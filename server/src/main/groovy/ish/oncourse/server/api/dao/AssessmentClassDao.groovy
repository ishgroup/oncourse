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

package ish.oncourse.server.api.dao

import javax.inject.Inject
import ish.oncourse.server.CayenneService
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Tutor
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class AssessmentClassDao implements ClassRelatedDao<AssessmentClass> {

    @Inject
    private CayenneService cayenne

    @Override
    List<AssessmentClass> getByClassId(ObjectContext context, Long courseClassId) {
        return ObjectSelect.query(AssessmentClass)
                .where(AssessmentClass.COURSE_CLASS.dot(CourseClass.ID).eq(courseClassId))
                .prefetch(AssessmentClass.ASSESSMENT.joint())
                .prefetch(AssessmentClass.ASSESSMENT_CLASS_TUTORS.dot(AssessmentClassTutor.TUTOR).dot(Tutor.CONTACT).joint())
                .select(context)
    }

    List<AssessmentClassModule> getTrainingPlan(ObjectContext context, CourseClass courseClass, ish.oncourse.server.cayenne.Module module) {
        return ObjectSelect.query(AssessmentClassModule)
                .where(AssessmentClassModule.ASSESSMENT_CLASS.dot(AssessmentClass.COURSE_CLASS).eq(courseClass))
                .and(AssessmentClassModule.MODULE.eq(module))
                .select(context)
    }


    @Override
    AssessmentClass newObject(ObjectContext context) {
        return context.newObject(AssessmentClass)
    }

    AssessmentClassModule newObject(ObjectContext context, AssessmentClass assessmentClass, ish.oncourse.server.cayenne.Module module) {
        AssessmentClassModule assessmentClassModule = context.newObject(AssessmentClassModule)
        assessmentClassModule.assessmentClass = assessmentClass
        assessmentClassModule.module = module
        assessmentClassModule
    }

    @Override
    AssessmentClass getById(ObjectContext context, Long id) {
        return SelectById.query(AssessmentClass, id).selectOne(context)
    }

    boolean hasDuplicates(Long id, CourseClass courseClass, Assessment assessment) {
        AssessmentClass aClass = ObjectSelect.query(AssessmentClass)
                .where(AssessmentClass.ASSESSMENT.eq(assessment))
                .and(AssessmentClass.COURSE_CLASS.eq(courseClass))
                .selectOne(cayenne.newContext)
        return aClass != null && (id == null || aClass.id != id)
    }

}
