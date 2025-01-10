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


import ish.common.types.SurveyVisibility
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Survey

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Surveys are a record of student satisfaction with a particular enrolment. We score three different attributes:
 * course, tutor and venue.
 *
 * Each score is a value from 0 to 5.
 *
 * We also store a net promoter score to measure loyalty, a public testimonial and a flag for whether that testimonial should be published.
 */
@API
@QueueableEntity
class Survey extends _Survey implements Queueable, ExpandableTrait, ContactActivityTrait {


    @Override
    protected void postAdd() {
        super.postAdd()
        if (getVisibility() == null) {
            setVisibility(SurveyVisibility.REVIEW)
        }
    }

    /**
     * Where students are given the opportunity to record a comment, it is stored here. This comment is not intended to
     * be shared publically.
     *
     * @return comments made by student on survey
     */
    @API
    @Override
    String getComment() {
        return super.getComment()
    }

    /**
     * @return a score from 0-5 for the course and materials
     */
    @Nonnull
    @API
    @Override
    Integer getCourseScore() {
        return super.getCourseScore()
    }

    /**
     * @return the date and time this record was created
     */
    @Nonnull @Override @API
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    @Override
    String getInteractionName() {
        return enrolment.courseClass.course.name
    }
/**
     * @return the date and time this record was modified
     */
    @Nonnull @Override @API
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * Use getVisibility() instead
     *
     * @return is this comment sharable
     */
    @Nonnull @Deprecated
    Boolean getPublicComment() {
        return SurveyVisibility.TESTIMONIAL == super.getVisibility()
    }

    /**
     * @return a score from 0-5 for the tutor(s)
     */
    @Nonnull @Override @API
    Integer getTutorScore() {
        return super.getTutorScore()
    }

    /**
     * @return a score from 0-5 for the room and site
     */
    @Nonnull @Override @API
    Integer getVenueScore() {
        return super.getVenueScore()
    }

    /**
     * A net promoter score as a mark out of 10 as the answer to the question:
     * {@code How likely is it that you would recommend our company/product/service to a friend or colleague?}
     *
     * It is designed to measure loyalty.
     *
     * @return NPS as a value from 0-10
     */
    @Nullable @Override @API
    Integer getNetPromoterScore() {
        return super.getNetPromoterScore()
    }

    /**
     * The college admin staff may wish to edit student comments before displaying it publicly. The edited
     * version will be available here.
     *
     * @return public facing text which might be displayed on a website
     */
    @Nullable @Override @API
    String getTestimonial() {
        return super.getTestimonial()
    }

    /**
     * @return the enrolment for which the student is reporting their results
     */
    @Nonnull @Override @API
    Enrolment getEnrolment() {
        return super.getEnrolment()
    }

    /**
     * Visibility of survey on a website or other location.
     *
     * @return is this comment sharable
     */
    @Nonnull @Override @API
    SurveyVisibility getVisibility() {
        return super.getVisibility()
    }

    @Override
    String getSummaryDescription() {
        if(getEnrolment() == null || getEnrolment().getStudent() == null || getEnrolment().getStudent().getContact() == null) {
            return super.getSummaryDescription()
        }
        return getEnrolment().getStudent().getContact().getFullName()
    }
    
    @Override
    Class<? extends CustomField> getCustomFieldClass() {
        return SurveyCustomField
    }
}



