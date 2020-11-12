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

package ish.oncourse.server.api.traits


import ish.common.types.StudyReason
import ish.oncourse.server.api.v1.model.EnrolmentStudyReasonDTO

trait EnrolmentStudyReasonDTOTrait {

    StudyReason getDbType() {
        switch (this as EnrolmentStudyReasonDTO) {
            case EnrolmentStudyReasonDTO.NOT_STATED:
                return StudyReason.STUDY_REASON_NOT_STATED
            case EnrolmentStudyReasonDTO.TO_GET_A_JOB:
                return StudyReason.STUDY_REASON_JOB
            case EnrolmentStudyReasonDTO.TO_DEVELOP_MY_EXISTING_BUSINESS:
                return StudyReason.STUDY_REASON_DEVELOP_BUSINESS
            case EnrolmentStudyReasonDTO.TO_START_MY_OWN_BUSINESS:
                return StudyReason.STUDY_REASON_START_BUSINESS
            case EnrolmentStudyReasonDTO.TO_TRY_FOR_A_DIFFERENT_CAREER:
                return StudyReason.STUDY_REASON_CAREER_CHANGE
            case EnrolmentStudyReasonDTO.TO_GET_A_BETTER_JOB_OR_PROMOTION:
                return StudyReason.STUDY_REASON_BETTER_JOB
            case EnrolmentStudyReasonDTO.TO_GET_SKILLS_FOR_COMMUNITY_VOLUNTARY_WORK:
                return StudyReason.STUDY_REASON_VOLUNTARY_WORK
            case EnrolmentStudyReasonDTO.IT_WAS_A_REQUIREMENT_OF_MY_JOB:
                return StudyReason.STUDY_REASON_JOB_REQUIREMENT
            case EnrolmentStudyReasonDTO.I_WANTED_EXTRA_SKILLS_FOR_MY_JOB:
                return StudyReason.STUDY_REASON_EXTRA_JOB_SKILLS
            case EnrolmentStudyReasonDTO.TO_GET_INTO_ANOTHER_COURSE_OF_STUDY:
                return StudyReason.STUDY_REASON_FOR_ANOTHER_COURSE
            case EnrolmentStudyReasonDTO.OTHER_REASONS:
                return StudyReason.STUDY_REASON_OTHER
            case EnrolmentStudyReasonDTO.FOR_PERSONAL_INTEREST_OR_SELF_DEVELOPMENT:
                return StudyReason.STUDY_REASON_PERSONAL_INTEREST
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    EnrolmentStudyReasonDTO fromDbType(StudyReason dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case StudyReason.STUDY_REASON_NOT_STATED:
                return EnrolmentStudyReasonDTO.NOT_STATED
            case StudyReason.STUDY_REASON_JOB:
                return EnrolmentStudyReasonDTO.TO_GET_A_JOB
            case StudyReason.STUDY_REASON_DEVELOP_BUSINESS:
                return EnrolmentStudyReasonDTO.TO_DEVELOP_MY_EXISTING_BUSINESS
            case StudyReason.STUDY_REASON_START_BUSINESS:
                return EnrolmentStudyReasonDTO.TO_START_MY_OWN_BUSINESS
            case StudyReason.STUDY_REASON_CAREER_CHANGE:
                return EnrolmentStudyReasonDTO.TO_TRY_FOR_A_DIFFERENT_CAREER
            case StudyReason.STUDY_REASON_BETTER_JOB:
                return EnrolmentStudyReasonDTO.TO_GET_A_BETTER_JOB_OR_PROMOTION
            case StudyReason.STUDY_REASON_VOLUNTARY_WORK:
                return EnrolmentStudyReasonDTO.TO_GET_SKILLS_FOR_COMMUNITY_VOLUNTARY_WORK
            case StudyReason.STUDY_REASON_JOB_REQUIREMENT:
                return EnrolmentStudyReasonDTO.IT_WAS_A_REQUIREMENT_OF_MY_JOB
            case StudyReason.STUDY_REASON_EXTRA_JOB_SKILLS:
                return EnrolmentStudyReasonDTO.I_WANTED_EXTRA_SKILLS_FOR_MY_JOB
            case StudyReason.STUDY_REASON_FOR_ANOTHER_COURSE:
                return EnrolmentStudyReasonDTO.TO_GET_INTO_ANOTHER_COURSE_OF_STUDY
            case StudyReason.STUDY_REASON_OTHER:
                return EnrolmentStudyReasonDTO.OTHER_REASONS
            case StudyReason.STUDY_REASON_PERSONAL_INTEREST:
                return EnrolmentStudyReasonDTO.FOR_PERSONAL_INTEREST_OR_SELF_DEVELOPMENT
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
