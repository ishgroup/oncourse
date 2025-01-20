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

package ish.oncourse.server.api.v1.function

import ish.common.types.AttendanceType
import ish.common.types.CreditLevel
import ish.common.types.CreditProviderType
import ish.common.types.CreditType
import ish.common.types.RecognitionOfPriorLearningIndicator
import ish.common.types.StudentStatusForUnitOfStudy
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.EnrolmentCreditLevelDTO
import ish.oncourse.server.api.v1.model.EnrolmentCreditProviderTypeDTO
import ish.oncourse.server.api.v1.model.EnrolmentCreditTotalDTO
import ish.oncourse.server.api.v1.model.EnrolmentCreditTypeDTO
import ish.oncourse.server.api.v1.model.EnrolmentFeeStatusDTO
import ish.oncourse.server.cayenne.Enrolment

class EnrolmentFunctions {

    static final BidiMap<StudentStatusForUnitOfStudy, EnrolmentFeeStatusDTO> FEE_STATUS_MAP = new BidiMap<StudentStatusForUnitOfStudy, EnrolmentFeeStatusDTO>() {{
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.NON_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT, EnrolmentFeeStatusDTO.RESTRICTED_ACCESS_ARRANGEMENT)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.VICTORIAN_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_TASMANIA_STATE_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.TASMANIA_STATE_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED)
        put(StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED, EnrolmentFeeStatusDTO.AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED)
    }}

    static final BidiMap<CreditProviderType, EnrolmentCreditProviderTypeDTO> CREDIT_PROVIDER_TYPE_MAP = new BidiMap<CreditProviderType, EnrolmentCreditProviderTypeDTO>() {{
        put(CreditProviderType.NO_CREDIT_RPL_WAS_OFFERED_FOR_VET, EnrolmentCreditProviderTypeDTO.NO_CREDIT_RPL_WAS_OFFERED_FOR_VET)
        put(CreditProviderType.UNIVERSITY, EnrolmentCreditProviderTypeDTO.UNIVERSITY)
        put(CreditProviderType.OTHER_HIGHER_EDUCATION_PROVIDER, EnrolmentCreditProviderTypeDTO.OTHER_HIGHER_EDUCATION_PROVIDER)
        put(CreditProviderType.TAFE, EnrolmentCreditProviderTypeDTO.TAFE)
        put(CreditProviderType.SECONDARY_SCHOOLS_OR_COLLEGES, EnrolmentCreditProviderTypeDTO.SECONDARY_SCHOOLS_COLLEGES)
        put(CreditProviderType.OTHER_REGISTERED_TRAINING_ORGANIZATIONS, EnrolmentCreditProviderTypeDTO.OTHER_REGISTERED_TRAINING_ORGANISATIONS)
        put(CreditProviderType.NOT_ELSEWHERE_CATEGORIZED, EnrolmentCreditProviderTypeDTO.NOT_ELSEWHERE_CATEGORISED)
    }}

    static final BidiMap<RecognitionOfPriorLearningIndicator, EnrolmentCreditTotalDTO> CREDIT_TOTAL_MAP = new BidiMap<RecognitionOfPriorLearningIndicator, EnrolmentCreditTotalDTO>() {{
        put(RecognitionOfPriorLearningIndicator.NOT_RPL_UNIT_OF_STUDY, EnrolmentCreditTotalDTO.IS_NOT_AN_RPL_UNIT_OF_STUDY)
        put(RecognitionOfPriorLearningIndicator.UNIT_OF_STUDY_CONSISTS_WHOLLY_OF_RPL, EnrolmentCreditTotalDTO.CONSISTS_WHOLLY_OF_RPL)
        put(RecognitionOfPriorLearningIndicator.UNIT_OF_STUDY_HAS_A_COMPONENT_OF_RPL, EnrolmentCreditTotalDTO.HAS_A_COMPONENT_OF_RPL)
    }}


    static final BidiMap<CreditType, EnrolmentCreditTypeDTO> CREDIT_TYPE_MAP = new BidiMap<CreditType, EnrolmentCreditTypeDTO>() {{
        put(CreditType.NO_CREDIT_RPL_WAS_OFFERED, EnrolmentCreditTypeDTO.NO_CREDIT_RPL_WAS_OFFERED)
        put(CreditType.CREDIT_RPL_FOR_PRIOR_HIGHER_EDUCATION_STUDY_ONLY, EnrolmentCreditTypeDTO.CREDIT_RPL_WAS_OFFERED_FOR_PRIOR_HIGHER_EDUCATION_STUDY_ONLY)
        put(CreditType.CREDIT_RPL_FOR_PRIOR_VET_STUDY_ONLY, EnrolmentCreditTypeDTO.CREDIT_RPL_WAS_OFFERED_FOR_PRIOR_VET_STUDY_ONLY)
        put(CreditType.CREDIT_RPL_FOR_COMBINATION_OF_PRIOR_HIGHER_EDUCATION_AND_VET_STUDY, EnrolmentCreditTypeDTO.CREDIT_RPL_WAS_OFFERED_FOR_A_COMBINATION_OF_PRIOR_HIGHER_EDUCATION_AND_VET_STUDY)
        put(CreditType.CREDIT_RPL_FOR_STUDY_OUTSIDE_AUSTRALIA, EnrolmentCreditTypeDTO.CREDIT_RPL_WAS_OFFERED_FOR_STUDY_UNDERTAKEN_AT_AN_EDUCATION_PROVIDER_OUTSIDE_AUSTRALIA)
        put(CreditType.CREDIT_RPL_FOR_WORK_EXPERIENCE, EnrolmentCreditTypeDTO.CREDIT_RPL_WAS_OFFERED_FOR_WORK_EXPERIENCE_UNDERTAKEN_INSIDE_OR_OUTSIDE_AUSTRALIA)
        put(CreditType.OTHER, EnrolmentCreditTypeDTO.OTHER)
    }}

    static final BidiMap<CreditLevel, EnrolmentCreditLevelDTO> CREDIT_LEVEL_MAP = new BidiMap<CreditLevel, EnrolmentCreditLevelDTO>() {{
        put(CreditLevel.NO_CREDIT_RPL_WAS_OFFERED_FOR_VET, EnrolmentCreditLevelDTO.NO_CREDIT_RPL_WAS_OFFERED_FOR_VET)
        put(CreditLevel.VOCATIONAL_GRADUATE_CERTIFICATE, EnrolmentCreditLevelDTO.VOCATIONAL_GRADUATE_CERTIFICATE)
        put(CreditLevel.VOCATIONAL_GRADUATE_DIPLOMA, EnrolmentCreditLevelDTO.VOCATIONAL_GRADUATE_DIPLOMA)
        put(CreditLevel.ADVANCED_DIPLOMA, EnrolmentCreditLevelDTO.ADVANCED_DIPLOMA)
        put(CreditLevel.STATEMENT_OF_ATTEINMENT_AT_ADVANCED_DIPLOMA_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_ADVANCED_DIPLOMA_LEVEL)
        put(CreditLevel.BRIDGING_AND_ENABLING_COURSE_AT_ADVANCED_DIPLOMA, EnrolmentCreditLevelDTO.BRIDGING_AND_ENABLING_COURSE_AT_ADVANCED_DIPLOMA)
        put(CreditLevel.DIPLOMA, EnrolmentCreditLevelDTO.DIPLOMA)
        put(CreditLevel.STATEMENT_OF_ATTEINMENT_AT_DIPLOMA_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_DIPLOMA_LEVEL)
        put(CreditLevel.BRIDGING_AND_ENABLING_COURSE_AT_DIPLOMA, EnrolmentCreditLevelDTO.BRIDGING_AND_ENABLING_COURSE_AT_DIPLOMA)
        put(CreditLevel.CERTIFICATE_4_LEVEL, EnrolmentCreditLevelDTO.CERTIFICATE_IV)
        put(CreditLevel.STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_4_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_CERTIFICATE_IV_LEVEL)
        put(CreditLevel.BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_4_LEVEL, EnrolmentCreditLevelDTO.BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_IV_LEVEL)
        put(CreditLevel.CERTIFICATE_3_LEVEL, EnrolmentCreditLevelDTO.CERTIFICATE_III)
        put(CreditLevel.STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_3_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_CERTIFICATE_III_LEVEL)
        put(CreditLevel.BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_3_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_CERTIFICATE_III_LEVEL)
        put(CreditLevel.CERTIFICATE_2_LEVEL, EnrolmentCreditLevelDTO.CERTIFICATE_II)
        put(CreditLevel.STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_2_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_CERTIFICATE_II_LEVEL)
        put(CreditLevel.BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_2_LEVEL, EnrolmentCreditLevelDTO.BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_II_LEVEL)
        put(CreditLevel.CERTIFICATE_1_LEVEL, EnrolmentCreditLevelDTO.CERTIFICATE_I)
        put(CreditLevel.STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_1_LEVEL, EnrolmentCreditLevelDTO.STATEMENT_OF_ATTAINMENT_AT_CERTIFICATE_I_LEVEL)
        put(CreditLevel.OTHER, EnrolmentCreditLevelDTO.OTHER_QUALIFICATION)
    }}


    static List<Long> filterEnrolmentsWithCompletedClasses(List<Enrolment> enrolments){
        return enrolments.findAll {e -> e.attendances.findAll {it.attendanceType == AttendanceType.ATTENDED }.size() >= e.courseClass.minimumSessionsToComplete}
                .collect {it.id}
    }
}
