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

import ish.common.types.VETFeeExemptionType
import ish.oncourse.server.api.v1.model.EnrolmentExemptionTypeDTO

trait EnrolmentExemptionTypeDTOTrait {

    VETFeeExemptionType getDbType() {
        switch (this as EnrolmentExemptionTypeDTO) {
            case EnrolmentExemptionTypeDTO.NOT_SET:
                return VETFeeExemptionType.UNSET
            case EnrolmentExemptionTypeDTO.NO_N_:
                return VETFeeExemptionType.NO
            case EnrolmentExemptionTypeDTO.YES_Y_:
                return VETFeeExemptionType.YES
            case EnrolmentExemptionTypeDTO.QLD_ONLY_CONCESSIONAL_PARTICIPANT_C_:
                return VETFeeExemptionType.C
            case EnrolmentExemptionTypeDTO.WA_ONLY_PENSIONER_CONCESSION_CARD_D_:
                return VETFeeExemptionType.D
            case EnrolmentExemptionTypeDTO.WA_ONLY_REPATRIATION_HEALTH_BENEFITS_CARDS_E_:
                return VETFeeExemptionType.E
            case EnrolmentExemptionTypeDTO.WA_ONLY_FEE_EXEMPT_F_:
                return VETFeeExemptionType.F
            case EnrolmentExemptionTypeDTO.VIC_VCE_SCHOLARSHIP_WA_AUSTUDY_ABSTUDY_G_:
                return VETFeeExemptionType.G
            case EnrolmentExemptionTypeDTO.VIC_ONLY_HEALTH_CARE_CARD_H_:
                return VETFeeExemptionType.H
            case EnrolmentExemptionTypeDTO.WA_ONLY_HEALTH_CARE_CARD_YOUTH_ALLOWANCE_JOB_SEEKER_FEE_FREE_I_:
                return VETFeeExemptionType.I
            case EnrolmentExemptionTypeDTO.WA_ONLY_JOB_NETWORK_CARD_FEE_FREE_J_:
                return VETFeeExemptionType.J
            case EnrolmentExemptionTypeDTO.WA_ONLY_UNDER_18_YEARS_OF_AGE_L_:
                return VETFeeExemptionType.L
            case EnrolmentExemptionTypeDTO.VIC_ONLY_PRISONER_M_:
                return VETFeeExemptionType.M
            case EnrolmentExemptionTypeDTO.QLD_NON_CONCESSIONAL_PARTICIPANT_WA_HEALTH_CARE_CARD_N_:
                return VETFeeExemptionType.N
            case EnrolmentExemptionTypeDTO.VIC_OTHER_WA_YOUTH_ALLOWANCE_O_:
                return VETFeeExemptionType.O
            case EnrolmentExemptionTypeDTO.VIC_ONLY_PENSIONER_CONCESSION_CARD_P_:
                return VETFeeExemptionType.P
            case EnrolmentExemptionTypeDTO.WA_ONLY_CUSTODIAL_INSTITUTION_INMATES_PRISON_INMATES_Q_:
                return VETFeeExemptionType.Q
            case EnrolmentExemptionTypeDTO.WA_ONLY_HEALTH_CARE_CARD_NEW_START_FEE_FREE_S_:
                return VETFeeExemptionType.S
            case EnrolmentExemptionTypeDTO.VIC_VETERAN_GOLD_CARD_CONCESSION_WA_FEES_WAIVED_DUE_TO_SEVERE_FINANCIAL_HARDSHIP_V_:
                return VETFeeExemptionType.V
            case EnrolmentExemptionTypeDTO.VIC_NONE_WA_NO_CONCESSION_Z_:
                return VETFeeExemptionType.Z
            case EnrolmentExemptionTypeDTO.OUTREACH_SUPPORT_OS_:
                return VETFeeExemptionType.OS
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    EnrolmentExemptionTypeDTO fromDbType(VETFeeExemptionType dataType) {
        if (!dataType) {
            return null
        }
        switch (dataType) {
            case VETFeeExemptionType.UNSET:
                return EnrolmentExemptionTypeDTO.NOT_SET
            case VETFeeExemptionType.NO:
                return EnrolmentExemptionTypeDTO.NO_N_
            case VETFeeExemptionType.YES:
                return EnrolmentExemptionTypeDTO.YES_Y_
            case VETFeeExemptionType.C:
                return EnrolmentExemptionTypeDTO.QLD_ONLY_CONCESSIONAL_PARTICIPANT_C_
            case VETFeeExemptionType.D:
                return EnrolmentExemptionTypeDTO.WA_ONLY_PENSIONER_CONCESSION_CARD_D_
            case VETFeeExemptionType.E:
                return EnrolmentExemptionTypeDTO.WA_ONLY_REPATRIATION_HEALTH_BENEFITS_CARDS_E_
            case VETFeeExemptionType.F:
                return EnrolmentExemptionTypeDTO.WA_ONLY_FEE_EXEMPT_F_
            case VETFeeExemptionType.G:
                return EnrolmentExemptionTypeDTO.VIC_VCE_SCHOLARSHIP_WA_AUSTUDY_ABSTUDY_G_
            case VETFeeExemptionType.H:
                return EnrolmentExemptionTypeDTO.VIC_ONLY_HEALTH_CARE_CARD_H_
            case VETFeeExemptionType.I:
                return EnrolmentExemptionTypeDTO.WA_ONLY_HEALTH_CARE_CARD_YOUTH_ALLOWANCE_JOB_SEEKER_FEE_FREE_I_
            case VETFeeExemptionType.J:
                return EnrolmentExemptionTypeDTO.WA_ONLY_JOB_NETWORK_CARD_FEE_FREE_J_
            case VETFeeExemptionType.L:
                return EnrolmentExemptionTypeDTO.WA_ONLY_UNDER_18_YEARS_OF_AGE_L_
            case VETFeeExemptionType.M:
                return EnrolmentExemptionTypeDTO.VIC_ONLY_PRISONER_M_
            case VETFeeExemptionType.N:
                return EnrolmentExemptionTypeDTO.QLD_NON_CONCESSIONAL_PARTICIPANT_WA_HEALTH_CARE_CARD_N_
            case VETFeeExemptionType.O:
                return EnrolmentExemptionTypeDTO.VIC_OTHER_WA_YOUTH_ALLOWANCE_O_
            case VETFeeExemptionType.P:
                return EnrolmentExemptionTypeDTO.VIC_ONLY_PENSIONER_CONCESSION_CARD_P_
            case VETFeeExemptionType.Q:
                return EnrolmentExemptionTypeDTO.WA_ONLY_CUSTODIAL_INSTITUTION_INMATES_PRISON_INMATES_Q_
            case VETFeeExemptionType.S:
                return EnrolmentExemptionTypeDTO.WA_ONLY_HEALTH_CARE_CARD_NEW_START_FEE_FREE_S_
            case VETFeeExemptionType.V:
                return EnrolmentExemptionTypeDTO.VIC_VETERAN_GOLD_CARD_CONCESSION_WA_FEES_WAIVED_DUE_TO_SEVERE_FINANCIAL_HARDSHIP_V_
            case VETFeeExemptionType.Z:
                return EnrolmentExemptionTypeDTO.VIC_NONE_WA_NO_CONCESSION_Z_
            case VETFeeExemptionType.OS:
                return EnrolmentExemptionTypeDTO.OUTREACH_SUPPORT_OS_
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
