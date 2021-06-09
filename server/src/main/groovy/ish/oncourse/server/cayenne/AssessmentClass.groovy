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

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.cayenne.AssessmentClassInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._AssessmentClass
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull
import javax.annotation.Nullable

//TODO docs
@API
@QueueableEntity
class AssessmentClass extends _AssessmentClass implements Queueable, AssessmentClassInterface {



    @Override
    void validateForSave(ValidationResult validationResult) {
        if (getCourseClass() != null && getAssessment() != null && isDuplicate()) {
            validationResult.addFailure(ValidationFailure.validationFailure(this, ASSESSMENT.getName(), "Link to this assessment is already exists"))
        }

        super.validateForSave(validationResult)
    }

    private boolean isDuplicate() {
        if (getCourseClass().isNewRecord() && getCourseClass().getObjectId().isTemporary()) {
            long count = 0
            for (AssessmentClass ac : getCourseClass().getAssessmentClasses()) {
                if (getAssessment() == ac.getAssessment()) {
                    count++
                }
            }
            return count > 1
        } else {
            return detectDuplicate([ASSESSMENT.getName(), COURSE_CLASS.getName() ] as String[]) != null
        }
    }

    /**
     * @return the date and time this record was created
     */
    @Nonnull
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    @Nonnull
    @API
    @Override
    Date getDueDate() {
        return super.getDueDate()
    }


    /**
     * @return the date and time this record was modified
     */
    @Nonnull
    @API
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * If a task does not have a release date, it will be available to enrolled students in the portal immediately after enrolment, like other course/class resources.
     *
     * @return release date
     */
    @Nullable
    @API
    @Override
    Date getReleaseDate() {
        return super.getReleaseDate()
    }

    @Nonnull
    @API
    @Override
    Assessment getAssessment() {
        return super.getAssessment()
    }

    @Nonnull
    @Override
    List<AssessmentClassTutor> getAssessmentClassTutors() {
        return super.getAssessmentClassTutors()
    }

    /**
     * @return a list of tutors related to this assessment
     */
    @Nonnull
    @API
    List<Tutor> getTutors() {
        return super.assessmentClassTutors*.tutor
    }


    @Nonnull
    @API
    @Override
    List<AssessmentSubmission> getAssessmentSubmissions() {
        return super.getAssessmentSubmissions()
    }

    AssessmentSubmission getAssessmentSubmission(Enrolment enrolment) {
        return getAssessmentSubmissions().find {submittion -> submittion.enrolment.id == enrolment.id } 
        
    }

    @Nonnull
    @API
    @Override
    CourseClass getCourseClass() {
        return super.getCourseClass()
    }

    /**
     * Gets a list of modules associated with this assessment
     *
     * @return a list of modules related to this assessment
     */
    @Nonnull
    @API
    List<Module> getModules() {
        List<Module> modules = new ArrayList<>(getAssessmentClassModules().size())
        for(AssessmentClassModule assessmentClassModule : getAssessmentClassModules()) {
            modules.add(assessmentClassModule.getModule())
        }
        return modules
    }

    @Override
    void prePersist() {
        super.prePersist()
        updateRelatedOutcomes()
    }

    @Override
    void preUpdate() {
        super.preUpdate()
        updateRelatedOutcomes()
    }

    private void updateRelatedOutcomes() {
        List<Outcome> outcomes = assessmentClassModules*.module*.outcomes*.findAll{courseClass.enrolments*.id.contains(it.enrolment?.id)}.flatten() as List<Outcome>

        outcomes.findAll{it.startDateOverridden == null || it.startDateOverridden == false}
                .each { o ->
                    o.setStartDate(o.getActualStartDate())
                }
        outcomes.findAll{it.endDateOverridden == null || it.endDateOverridden == false}
                .each {  o ->
                    o.setEndDate(o.getActualEndDate())
                }
    }

}



